package fpp.compiler.codegen

/** Utilities for constructing XML Tags */
object XmlTags extends LineUtils {

  def closeTag(name: String) = s"</$name>"

  def openCloseTag(name: String, pairs: List[(String, String)] = Nil) = openTagPrefix(name, pairs) ++ "/>"

  def openTag(name: String, pairs: List[(String, String)] = Nil) = openTagPrefix(name, pairs) ++ ">"

  def quoted(s: String) =  "\"" ++ s ++ "\""

  def taggedLines (tags: (String, String)) (ls: List[Line]) = {
    val (openTag, closeTag) = tags
    (line(openTag) :: ls) :+ line(closeTag)
  }

  def taggedString (tags: (String, String)) (s: String) = {
    val (openTag, closeTag) = tags
    openTag ++ s ++ closeTag
  }

  def tags(name: String, pairs: List[(String, String)] = Nil) = (openTag(name, pairs), closeTag(name))

  private def openTagPrefix(name: String, pairs: List[(String, String)]) = 
    pairs.foldLeft(s"<$name")({ case (s, key -> value) => s ++ s" $key=${quoted(value)}" })

}
