/**
 * A location used in compilation
 */

package fpp.compiler.util

import util.parsing.input.Position

final case class Location(file: File, pos: Position) {
    override def toString = s"${file}: ${pos.toString}\n${pos.longString}"
}
