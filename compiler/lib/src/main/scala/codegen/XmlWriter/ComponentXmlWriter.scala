package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for component definitions */
object ComponentXmlWriter extends AstVisitor with LineUtils {

  override def default(s: XmlWriterState) = Nil

  override def defComponentAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val symbol = Symbol.Component(aNode)
    val c = s.a.componentMap(symbol)
    val pairs = {
      val data = aNode._2.data
      s.getNamespaceAndName(data.name) :+ ("kind", data.kind.toString)
    }
    val body = {
      def addBlank(ls: List[Line]) = ls match {
        case Nil => Nil
        case _ => Line.blank :: ls
      }
      val comment = AnnotationXmlWriter.multilineComment(aNode)
      List(
        comment,
        writeImports(s, c),
        writePorts(s, c),
        writeInternalInterfaces(s, c),
        writeCommands(s, c),
        writeEvents(s, c),
        writeParams(s, c),
        writeTlmChannels(s, c)
      ).flatMap(addBlank) :+ Line.blank
    }
    XmlTags.taggedLines ("component", pairs) (body.map(indentIn))
  }

  private def writeImports(s: XmlWriterState, c: Component) = {
    val Right(a1) = UsedSymbols.defComponentAnnotatedNode(s.a, c.aNode)
    s.copy(a = a1).writeImportDirectives
  }

  private def writePorts(s: XmlWriterState, c: Component) = {
    def writeGeneralPort(name: String, general: PortInstance.General) = {
      import PortInstance.General._
      def writeDataType(ty: PortInstance.General.Type) = ty match {
        case Type.DefPort(symbol) => s.writeSymbol(symbol)
        case Type.Serial => "Serial"
      }
      def writeKind(kind: Kind) = kind match {
        case _ : Kind.AsyncInput => "async_input"
        case Kind.GuardedInput => "guarded_input"
        case Kind.SyncInput => "sync_input"
        case Kind.Output => "output"
      }
      val data = general.aNode._2.data
      val pairs = List(
        ("name", name),
        ("data_type", writeDataType(general.ty)),
        ("kind", writeKind(general.kind)),
        ("max_number", general.size.toString)
      )
      val comment = AnnotationXmlWriter.multilineComment(general.aNode)
      XmlTags.taggedLines ("port", pairs) (comment.map(indentIn))
    }
    def writeSpecialPort(name: String, special: PortInstance.Special) = {
      val kind = {
        import Ast.SpecPortInstance._
        special.specifier.kind match {
          case CommandRecv => "input"
          case _ => "output"
        }
      }
      val role = {
        import Ast.SpecPortInstance._
        special.specifier.kind match {
          case CommandRecv => "Cmd"
          case CommandReg => "CmdRegistration"
          case CommandResp => "CmdResponse"
          case Event => "LogEvent"
          case ParamGet => "ParamGet"
          case ParamSet => "ParamSet"
          case Telemetry => "Telemetry"
          case TextEvent => "LogTextEvent"
          case TimeGet => "TimeGet"
        }
      }
      val pairs = List(
        ("name", name),
        ("data_type", s.writeSymbol(special.symbol)),
        ("kind", kind),
        ("role", role),
        ("max_number", "1")
      )
      val comment = AnnotationXmlWriter.multilineComment(special.aNode)
      XmlTags.taggedLines ("port", pairs) (comment.map(indentIn))
    }
    def writePort(name: String, instance: PortInstance) = instance match {
      case general: PortInstance.General => writeGeneralPort(name, general)
      case special: PortInstance.Special => writeSpecialPort(name, special)
      case _ => Nil
    }
    val ports = c.portMap.keys.toList.sortWith(_ < _).
      flatMap(key => writePort(key, c.portMap(key)))
    XmlTags.taggedLinesOpt ("ports") (ports.map(indentIn))
  }

  private def writeInternalInterfaces(s: XmlWriterState, c: Component) = {
    // TODO
    default(s)
  }

  private def writeCommands(s: XmlWriterState, c: Component) = {
    // TODO
    default(s)
  }

  private def writeEvents(s: XmlWriterState, c: Component) = {
    // TODO
    default(s)
  }

  private def writeParams(s: XmlWriterState, c: Component) = {
    // TODO
    default(s)
  }

  private def writeTlmChannels(s: XmlWriterState, c: Component) = {
    // TODO
    default(s)
  }

  type In = XmlWriterState

  type Out = List[Line]

}
