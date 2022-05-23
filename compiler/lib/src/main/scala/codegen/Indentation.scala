package fpp.compiler.codegen

/**
 *  Line indentation 
 */
case class Indentation(n: Int) {

  /** Format the indentation as a string of spaces */
  override def toString = {
    def helper(n: Int, s: String): String =
      if (n < 1) s else helper(n - 1, " " ++ s)
    helper(n, "")
  }

  /** Get the indentation space count */
  def toInt: Int = n

  /** Indent in by the given space count */
  def indentIn(incr: Int): Indentation = Indentation(n + incr)

  /** Indent out by the given space count */
  def indentOut(incr: Int): Indentation = Indentation(n - incr)

}
