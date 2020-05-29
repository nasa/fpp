package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Write out F Prime XML */
object XmlWriter extends AstStateVisitor {

  /** XML Writer state */
  case class State(
    /** The result of semantic analysis */
    a: Analysis,
    /** The output directory */
    dir: String,
    /** The list of include prefixes */
    prefixes: List[String],
  ) {

    /** Remove the longest prefix from a Java path */
    def removeLongestPrefix(path: File.JavaPath): File.JavaPath = {
      def removePrefix(s: String) = {
        val prefix = java.nio.file.Paths.get(s)
        if (path.startsWith(prefix)) path.relativize(prefix) else path
      }
      prefixes.map(removePrefix(_)) match {
        case Nil => path
        case head :: tail => {
          def min(p1: File.JavaPath, p2: File.JavaPath) = 
            if (p1.getNameCount < p2.getNameCount) p1 else p2
          tail.fold(head)(min)
        }
      }
    }

    /** Write import directives as lines */
    def writeImportDirectives(currentFile: File): List[Line] = {
      def getDirectiveForSymbol(sym: Symbol): Option[String] =
        for {
          loc <- sym.getLocOpt
          locPath <- loc.file match {
            case File.Path(p) => Some(p)
            case _ => None
          }
          tagFileName <- sym match {
            case Symbol.AbsType(_) => Some(
              "include_header",
              sym.getUnqualifiedName ++ ".hpp"
            )
            case Symbol.Array(aNode) => Some(
              "include_array_type",
              ComputeXmlFiles.getArrayFileName(aNode._2.getData)
            )
            case Symbol.Enum(aNode) => Some(
              "include_enum_type",
              ComputeXmlFiles.getEnumFileName(aNode._2.getData)
            )
            case Symbol.Struct(aNode) => Some(
              "import_serializable_type",
              ComputeXmlFiles.getStructFileName(aNode._2.getData)
            )
            case _ => None
          }
        }
        yield {
          val (tag, fileName) = tagFileName
          val filePath = java.nio.file.Paths.get(fileName)
          val dir = locPath.getParent
          val path = removeLongestPrefix(dir.resolve(filePath))
          XmlTags.taggedString(tag)(path.toString)
        }
      val set = a.usedSymbolSet.map(getDirectiveForSymbol(_)).filter(_.isDefined).map(_.get)
      val array = set.toArray
      scala.util.Sorting.quickSort(array)
      array.toList.map(Line(_))
    }

    /** Get the enclosing namespace */
    def getNamespace: String = a.moduleNameList.reverse match {
      case Nil => ""
      case head :: Nil => head
      case head :: tail => tail.foldLeft(head)({ case (s, name) => s ++ "::" ++ name })
    }

  }

  override def defArrayAnnotatedNode(s: State, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    // TODO
    default(s)
  }

  override def defEnumAnnotatedNode(s: State, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    // TODO
    default(s)
  }

  override def defModuleAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val data = node.getData
    val a = s.a.copy(moduleNameList = data.name :: s.a.moduleNameList)
    val s1 = s.copy(a = a)
    visitList(s1, data.members, matchModuleMember)
  }

  override def defStructAnnotatedNode(s: State, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val loc = Locations.get(node.getId)
    val data = node.getData
    val fileName = ComputeXmlFiles.getStructFileName(data)
    val lines = StructXmlWriter.defStructAnnotatedNode(s, aNode)
    for {
      _ <- if (data.members.length == 0) Left(CodeGenError.EmptyStruct(loc)) else Right(())
      s <- writeXmlFile(s, fileName, lines)
    } yield s
  }

  override def transUnit(s: State, tu: Ast.TransUnit) = 
    visitList(s, tu.members, matchTuMember)

  private def writeXmlFile(s: State, fileName: String, lines: List[Line]) = {
    val path = java.nio.file.Paths.get(s.dir, fileName)
    val file = File.Path(path)
    for (writer <- file.openWrite()) yield { 
      lines.map(Line.write(writer) _)
      writer.close()
      s
    }
  }

}
