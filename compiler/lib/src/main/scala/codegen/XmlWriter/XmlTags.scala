package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Utilities for constructing XML Tags */
object XmlTags extends LineUtils {

  def tags(name: String, pairs: List[(String, String)] = Nil) = (openTag(name, pairs), closeTag(name))

  def closeTag(name: String) = s"</$name>"

  def openCloseTag(name: String, pairs: List[(String, String)] = Nil) = openTagPrefix(name, pairs) ++ "/>"

  def openTag(name: String, pairs: List[(String, String)] = Nil) = openTagPrefix(name, pairs) ++ ">"

  def quoted(s: String) =  "\"" ++ s ++ "\""

  def taggedString (tags: (String, String)) (s: String) = {
    val (openTag, closeTag) = tags
    openTag ++ s ++ closeTag
  }

  def taggedLines (tags: (String, String)) (ls: List[Line]) = {
    val (openTag, closeTag) = tags
    (line(openTag) :: ls) :+ line(closeTag)
  }

  private def openTagPrefix(name: String, pairs: List[(String, String)]) = 
    pairs.foldLeft(s"<$name")({ case (s, key -> value) => s ++ s" $key=${quoted(value)}" })

}
