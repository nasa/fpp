package fpp.compiler.codegen

/** Utilities for writing C++ lines */
trait CppWriterLineUtils extends LineUtils {

  def wrapInScope(
    s1: String,
    ll: List[Line],
    s2: String
  ): List[Line] = ll match {
    case Nil => Nil
    case _ => List(lines(s1), ll.map(indentIn), lines(s2)).flatten
  }

  def wrapInAnonymousNamespace(ll: List[Line]): List[Line] =
    wrapInScope("namespace {", ll, "}")

  def wrapInNamespace(namespace: String, ll: List[Line]): List[Line] =
    wrapInScope(s"namespace $namespace {", ll, "}")

  def wrapInEnum(ll: List[Line]): List[Line] =
    wrapInScope("enum {", ll, "};")

}
