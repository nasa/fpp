package fpp.compiler.util

import util.parsing.input.Position

/** A location used in compilation */
final case class Location(file: File, pos: Position) {
    override def toString = pos match {
      case scala.util.parsing.input.NoPosition => s"${file}: end of input"
      case _ => s"${file}: ${pos.toString}\n${pos.longString}"
    }
}
