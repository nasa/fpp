package fpp.compiler.util

sealed trait Error

case class LexerError(location: Location, msg: String) extends Error
