package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Utilities for constructing XML Tags */
object XmlTags {

  def closeTag(name: String) = s"</$name>"

  def openCloseTag(name: String, pairs: List[(String, String)]) = openTagPrefix(name, pairs) ++ "/>"

  def openTag(name: String, pairs: List[(String, String)] = Nil) = openTagPrefix(name, pairs) ++ ">"

  def quoted(s: String) =  "\"" ++ s ++ "\""

  def taggedString (name: String, pairs: List[(String, String)] = Nil) (s: String) = 
    openTag(name, pairs) ++ s ++ closeTag(name)

  private def openTagPrefix(name: String, pairs: List[(String, String)]) = 
    pairs.foldLeft(s"<$name")({ case (s, key -> value) => s ++ s" $key=${quoted(value)}" })

}
