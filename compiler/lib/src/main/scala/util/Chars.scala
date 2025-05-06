package fpp.compiler.util

import scala.annotation.switch
import Character.{LETTER_NUMBER, LOWERCASE_LETTER, OTHER_LETTER, TITLECASE_LETTER, UPPERCASE_LETTER}
import Character.{MATH_SYMBOL, OTHER_SYMBOL}
import Character.{isJavaIdentifierPart, isUnicodeIdentifierStart, isUnicodeIdentifierPart}

/** Contains constants and classifier methods for characters */
object Chars {
  inline val LF = '\u000A'
  inline val FF = '\u000C'
  inline val CR = '\u000D'
  inline val SU = '\u001A'

  /** Convert a character digit to an Int according to given base,
    *  -1 if no success
    */
  def digit2int(ch: Char, base: Int): Int = {
    val num = if (ch <= '9') ch - '0'
    else if ('a' <= ch && ch <= 'z') ch - 'a' + 10
    else if ('A' <= ch && ch <= 'Z') ch - 'A' + 10
    else -1
    if (0 <= num && num < base) num else -1
  }

  def isIdentifierPart(c: Char): Boolean = isUnicodeIdentifierPart(c)
}
