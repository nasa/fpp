package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for component definitions */
object ComponentXmlWriter extends AstVisitor with LineUtils {

  type In = XmlWriterState

  type Out = List[Line]

  override def defComponentAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val symbol = Symbol.Component(aNode)
    val c = s.a.componentMap(symbol)
    val pairs = {
      val data = aNode._2.data
      s.getNamespaceAndName(symbol) :+ ("kind", data.kind.toString)
    }
    val body = {
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
      ).flatMap(XmlWriterState.addBlankPrefix) :+ Line.blank
    }
    XmlTags.taggedLines ("component", pairs) (body.map(indentIn))
  }

  override def default(s: XmlWriterState) = Nil

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
          ("opcode", XmlWriterState.writeId(opcode)),
          ("mnemonic", data.name),
        )
        val priority = nonParam.kind match {
          case Async(Some(priority), _) =>
            List(("priority", priority.toString))
          case _ => Nil
        }
        val queueFull = nonParam.kind match {
          case Async(_, queueFull) => List(("full", queueFull.toString))
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
    import Event._
    def writeEvent(id: Id, event: Event) = {
      val data = event.aNode._2.data
      def writeSeverity(severity: Ast.SpecEvent.Severity) = {
        import Ast.SpecEvent._
        severity match {
          case ActivityHigh => "ACTIVITY_HI"
          case ActivityLow => "ACTIVITY_LO"
          case Command => "COMMAND"
          case Diagnostic => "DIAGNOSTIC"
          case Fatal => "FATAL"
          case WarningHigh => "WARNING_HI"
          case WarningLow => "WARNING_LO"
        }
      }
      val pairs = {
        val typeNames = data.params.map(_._2.data.typeName)
        val format = FormatXmlWriter.formatToString(
          event.format,
          typeNames
        )
        val pairs1 = List(
          ("id", XmlWriterState.writeId(id)),
          ("name", data.name),
          ("severity", writeSeverity(data.severity)),
          ("format_string", format)
        )
        val throttle = event.throttle match {
          case Some(throttle) => List(("throttle", throttle.toString))
          case _ => Nil
        }
        pairs1 ++ throttle
      }
      val body = {
        val comment = AnnotationXmlWriter.multilineComment(event.aNode)
        val args = FormalParamsXmlWriter.formalParamList(s, data.params)
        comment ++ args
      }
      XmlTags.taggedLines ("event", pairs) (body.map(indentIn))
    }
    val events = c.eventMap.keys.toList.sortWith(_ < _).
      flatMap(key => writeEvent(key, c.eventMap(key)))
    XmlTags.taggedLinesOpt ("events") (events.map(indentIn))
  }

  private def writeImports(s: XmlWriterState, c: Component) = {
    val Right(a) = UsedSymbols.defComponentAnnotatedNode(s.a, c.aNode)
    s.writeImportDirectives(a.usedSymbolSet)
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

  private def writeParams(s: XmlWriterState, c: Component) = {
    import Param._
    def writeParam(id: Id, param: Param) = {
      val data = param.aNode._2.data
      val pairs = List(
        List (
          ("id", XmlWriterState.writeId(id)),
          ("set_opcode", XmlWriterState.writeId(param.setOpcode)),
          ("save_opcode", XmlWriterState.writeId(param.saveOpcode)),
          ("name", data.name),
        ),
        TypeXmlWriter.getPairs(s, param.paramType, "data_type"),
        param.default match {
          case Some(value) => List(("default", ValueXmlWriter.write(s, value)))
          case _ => Nil
        }
      ).flatten
      val comment = AnnotationXmlWriter.multilineComment(param.aNode)
      XmlTags.taggedLines ("parameter", pairs) (comment.map(indentIn))
    }
    val params = c.paramMap.keys.toList.sortWith(_ < _).
      flatMap(key => writeParam(key, c.paramMap(key)))
    XmlTags.taggedLinesOpt ("parameters") (params.map(indentIn))
  }

  private def writePorts(s: XmlWriterState, c: Component) = {
    def writeGeneralPort(name: String, general: PortInstance.General) = {
      import PortInstance.General._
      def writeDataType(ty: PortInstance.Type) = ty match {
        case PortInstance.Type.DefPort(symbol) => s.writeSymbol(symbol)
        case PortInstance.Type.Serial => "Serial"
      }
      def writeKind(kind: Kind) = kind match {
        case _ : Kind.AsyncInput => "async_input"
        case Kind.GuardedInput => "guarded_input"
        case Kind.SyncInput => "sync_input"
        case Kind.Output => "output"
      }
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
          // Hook queue full option becomes drop in XML
          case Kind.AsyncInput(_, Ast.QueueFull.Hook) =>
            List(("full", Ast.QueueFull.Drop.toString))
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
          // This should never happen, because of XML lowering
          case _ => throw new InternalError(s"invalid specifier kind ${special.specifier.kind}")
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
      case special: PortInstance.Special =>
        // Lower data product ports to generic XML
        DpPortXmlLowering(s, name, special).lower match {
          case Some(general) => writeGeneralPort(name, general)
          case None => writeSpecialPort(name, special)
        }
      case _ => Nil
    }
    val ports = c.portMap.keys.toList.sortWith(_ < _).
      flatMap(key => writePort(key, c.portMap(key)))
    XmlTags.taggedLinesOpt ("ports") (ports.map(indentIn))
  }

  private def writeTlmChannels(s: XmlWriterState, c: Component) = {
    import TlmChannel._
    def writeTlmChannel(id: Id, tlmChannel: TlmChannel) = {
      val data = tlmChannel.aNode._2.data
      def writeUpdate(update: Ast.SpecTlmChannel.Update) = {
        import Ast.SpecTlmChannel._
        update match {
          case Always => "always"
          case OnChange => "on_change"
        }
      }
      def writeLimits(name: String, limits: Limits) = {
        import Ast.SpecTlmChannel._
        def writeLimit(kind: LimitKind, value: Value) = {
          val left = s"${name}_${kind.toString}"
          val right = ValueXmlWriter.write(s, value)
          (left, right)
        }
        limits.keys.toList.
          map(key => writeLimit(key, limits(key)._2)).
          sortWith((a, b) => a._1 < b._1)
      }
      val pairs = List(
        List(
          ("id", XmlWriterState.writeId(id)),
          ("name", data.name),
        ),
        TypeXmlWriter.getPairs(s, tlmChannel.channelType, "data_type"),
        List(
          ("update", writeUpdate(tlmChannel.update))
        ),
        tlmChannel.format match {
          case Some(format) => List((
            "format_string",
            FormatXmlWriter.formatToString(format, List(data.typeName))
          ))
          case None => Nil
        },
        writeLimits("low", tlmChannel.lowLimits),
        writeLimits("high", tlmChannel.highLimits),
      ).flatten
      val comment = AnnotationXmlWriter.multilineComment(tlmChannel.aNode)
      XmlTags.taggedLines ("channel", pairs) (comment.map(indentIn))
    }
    val tlmChannels = c.tlmChannelMap.keys.toList.sortWith(_ < _).
      flatMap(key => writeTlmChannel(key, c.tlmChannelMap(key)))
    XmlTags.taggedLinesOpt ("telemetry") (tlmChannels.map(indentIn))
  }

}
