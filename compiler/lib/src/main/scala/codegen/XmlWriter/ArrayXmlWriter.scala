package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for array definitions */
object ArrayXmlWriter extends AstVisitor with LineUtils {

  override def default(s: XmlWriterState) = Nil

  override def defArrayAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val node = aNode._2
    val loc = Locations.get(node.getId)
    val data = node.getData
    val tags = {
      val pairs = s.getNamespaceAndName(data.name)
      XmlTags.tags("array", pairs)
    }

    val body = {
      val imports = s.writeImportDirectives(loc.file)
      val comment = AnnotationXmlWriter.multilineComment(aNode)
      val arrayType @ Type.Array(_, _, _, _) = s.a.typeMap(node.getId) 

      val arrType = {
        val typeName = TypeXmlWriter.getName(s, data.eltType)
        val stringSize = TypeXmlWriter.getSize(s, data.eltType)
        val openTag = stringSize match {
          case Some(openTag)=> XmlTags.openTag("type", List( ("size", openTag) ))
          case None => XmlTags.openTag("type", Nil)
        }
        val closeTag = XmlTags.closeTag("type")
        val tags = openTag ++ typeName ++ closeTag
        List(line(tags))
      }
      val arrSize = {
        val tags = XmlTags.tags("size")
        val mappedSize = arrayType.getArraySize match {
          case Some(mappedSize) => mappedSize.toString
          case None => "0"
        }
        List(line(XmlTags.taggedString(tags)(mappedSize)))
      }
      val arrFormat = {
        val tags = XmlTags.tags("format")
        val format = arrayType.format match {
          case Some(format) => format
          case None => Format("", List((Format.Field.Default,"")))
        }
        val s = FormatXmlWriter.formatToString(format, List(data.eltType))
        List(line(XmlTags.taggedString(tags)(s)))
      }
      val arrDefault = {
        val valueTags = XmlTags.tags("value")
        val defaultTags = XmlTags.tags("default")
        val ls = arrayType.getDefaultValue.map(arrayTypeDefaultNode(s, arrayType, _))
        XmlTags.taggedLines(defaultTags)(ls.get)
      }
      imports ++ comment ++ arrType ++ arrSize ++ arrFormat ++ arrDefault
    } 
    XmlTags.taggedLines(tags)(body.map(indentIn))
  }

  // Returns list of value lines for each value in array
  def arrayTypeDefaultNode(
    s: XmlWriterState,
    arrayType: Type.Array,
    defaultValue: Value.Array
  ): List[Line] = {
    // arrayNode.anonArray.getClass.getMethods.map(_.getName) foreach println
    // defaultValue.anonArray.elements.map(_.value) foreach println
    // val elements = defaultValue.anonArray.elements
    // val expandArray = elements.map(v) foreach match {
    //   Value.Array(v) => 
    // }
    List(line("Nil"))
  }

  type In = XmlWriterState

  type Out = List[Line]

}
