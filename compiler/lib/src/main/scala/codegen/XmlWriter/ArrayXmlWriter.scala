package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for array definitions */
object ArrayXmlWriter extends AstVisitor with LineUtils {

  override def default(s: XmlWriterState) = Nil

  override def defArrayAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val node = aNode._2
    val data = node.data
    val name = s.getSymbolName(Symbol.Array(aNode))
    val tags = {
      val pairs = s.getNamespaceAndName(name)
      XmlTags.tags("array", pairs)
    }

    val body = {
      val Right(a1) = UsedSymbols.defArrayAnnotatedNode(s.a, aNode)
      val s1 = s.copy(a = a1)
      val comment = AnnotationXmlWriter.multilineComment(aNode)
      val imports = s1.writeImportDirectives
      val arrayType @ Type.Array(_, _, _, _) = s.a.typeMap(node.id) 

      val arrType = {
        val typeName = TypeXmlWriter.getName(s, arrayType.anonArray.eltType)
        val stringSize = TypeXmlWriter.getSize(s, arrayType.anonArray.eltType)
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
        val defaultTags = XmlTags.tags("default")
        val ls = arrayType.getDefaultValue.map(arrayTypeDefaultValue(s, _))
        XmlTags.taggedLines(defaultTags)(ls.get.map(indentIn))
      }
      imports ++ comment ++ arrType ++ arrSize ++ arrFormat ++ arrDefault
    } 
    XmlTags.taggedLines(tags)(body.map(indentIn))
  }

  // Returns list of value lines for each value in array
  def arrayTypeDefaultValue(
    s: XmlWriterState,
    defaultValue: Value.Array
  ): List[Line] = {
    val valueTags = XmlTags.tags("value")
    val elements = defaultValue.anonArray.elements
    val defaultType = defaultValue.anonArray.getType
    val valueList = elements.map( ValueXmlWriter.getValue(s, _) )
    val tags = valueList.map( XmlTags.taggedString(valueTags)(_) )
    tags.map(line(_))
  }

  type In = XmlWriterState

  type Out = List[Line]

}
