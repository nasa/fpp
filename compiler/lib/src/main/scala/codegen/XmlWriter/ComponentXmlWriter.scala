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
      val pairs = {
        val pairs1 = List(
          ("name", name),
          ("data_type", writeDataType(general.ty)),
          ("kind", writeKind(general.kind)),
          ("max_number", general.size.toString)
        )
        val priority = general.kind match {
          case Kind.AsyncInput(Some(priority), _) =>
            List(("priority", priority.toString))
          case _ => Nil
        }
        val queueFull = general.kind match {
          case Kind.AsyncInput(_, queueFull) => 
            List(("full", queueFull.toString))
          case _ => Nil
        }
        pairs1 ++ priority ++ queueFull
      }
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
    def writeInternalPort(name: String, internal: PortInstance.Internal) = {
      import PortInstance.Internal._
      val data = internal.aNode._2.data
      val pairs = {
        val namePair = ("name", name)
        val priority = internal.priority match {
          case Some(priority) => List(("priority", priority.toString))
          case _ => Nil
        }
        val queueFull = ("full", internal.queueFull.toString)
        (namePair :: priority) :+ queueFull
      }
      val body = {
        val comment = AnnotationXmlWriter.multilineComment(internal.aNode)
        val args = FormalParamsXmlWriter.formalParamList(s, data.params)
        comment ++ args
      }
      XmlTags.taggedLines ("internal_interface", pairs) (body.map(indentIn))
    }
    def writeInternalInterface(name: String, instance: PortInstance) = instance match {
      case internal: PortInstance.Internal => writeInternalPort(name, internal)
      case _ => Nil
    }
    val ports = c.portMap.keys.toList.sortWith(_ < _).
      flatMap(key => writeInternalInterface(key, c.portMap(key)))
    XmlTags.taggedLinesOpt ("internal_interfaces") (ports.map(indentIn))
  }

  private def writeCommands(s: XmlWriterState, c: Component) = {
    import Command._
    def writeNonParamCommand(opcode: Opcode, nonParam: NonParam) = {
      import NonParam._
      val data = nonParam.aNode._2.data
      def writeKind(kind: NonParam.Kind) = {
        kind match {
          case _: Async => "async"
          case Guarded => "guarded"
          case Sync => "sync"
        }
      }
      val pairs = {
        val pairs1 = List(
          ("kind", writeKind(nonParam.kind)),
          ("opcode", Integer.toString(opcode, 16).toUpperCase),
          ("mnemonic", data.name),
        )
        val priority = nonParam.kind match {
          case Async(Some(priority), _) =>
            List(("priority", priority.toString))
          case _ => Nil
        }
        val queueFull = nonParam.kind match {
          case Async(_, queueFull) => 
            List(("full", queueFull.toString))
          case _ => Nil
        }
        pairs1 ++ priority ++ queueFull
      }
      val body = {
        val comment = AnnotationXmlWriter.multilineComment(nonParam.aNode)
        val args = FormalParamsXmlWriter.formalParamList(s, data.params)
        comment ++ args
      }
      XmlTags.taggedLines ("command", pairs) (body.map(indentIn))
    }
    def writeCommand(opcode: Opcode, command: Command) = command match {
      case nonParam: NonParam => writeNonParamCommand(opcode, nonParam)
      // Parameter commands are implicit in the XML component representation
      case param: Param => Nil
    }
    val commands = c.commandMap.keys.toList.sortWith(_ < _).
      flatMap(key => writeCommand(key, c.commandMap(key)))
    XmlTags.taggedLinesOpt ("commands") (commands.map(indentIn))
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
