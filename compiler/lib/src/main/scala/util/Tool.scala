package fpp.compiler.util

import scopt.OParser

/** An FPP compilation tool */
final case class Tool(name: String) {

  override def toString = name

  /** The main method for the tool */
  def mainMethod[Options,Return](
    args: Array[String],
    oparser: OParser[Unit, Options],
    initialOptions: Options,
    command: Options => Result.Result[Return]
  ): Unit = {
    def errorExit = System.exit(1)
    Error.setTool(this)
    OParser.parse(oparser, args, initialOptions) match {
      case Some(options) => command(options) match {
        case Left(error) => {
          error.print
          errorExit
        }
        case _ => ()
      }
      case None => errorExit
    }
  }

}
