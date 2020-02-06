package fpp.compiler.util

import util.parsing.input.Position

case class Location(pos: Position) {
    override def toString = s"${pos.line}:${pos.column}"
}
