package fpp.compiler.util

import util.parsing.input.Position

/** A location used in compilation */
final case class Location(file: File, pos: Position) {
    override def toString = s"${file}: ${pos.toString}\n${pos.longString}"
}
