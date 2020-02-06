package fpp.compiler.error

import util.parsing.input.Position

sealed trait Error

case class LexerError(location: Location, msg: String) extends Error

case class Location(pos: Position) {
    override def toString = s"${pos.line}:${pos.column}"
}
