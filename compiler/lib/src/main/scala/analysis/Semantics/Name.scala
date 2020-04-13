package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A qualified or unqualified name */
object Name {

  type Unqualified = String

  case class Qualified(
    qualifier: List[Unqualified],
    base: Unqualified
  ) {

    /** Convert a qualified name to a string */
    override def toString = {
      def f(s1: String, s2: String) = s1 ++ "." ++ s2
      def convertQualifier = qualifier match {
        case Nil => ""
        case head :: tail => (tail.fold (head) (f)) ++ "."
      }
      convertQualifier ++ base
    }

    /** Convert a qualified name to an identifier list */
    def toIdentList = (base :: qualifier).reverse

  }

  object Qualified {

    /** Create a qualified name from a string */
    def fromString(s: String) = fromIdentList(s.split(".").toList)

    /** Create a qualified name A.B.C from an identifer list [ C, B, A ] */
    def fromIdentList(il: List[Ast.Ident]) = {
      il match {
        case head :: tail => Qualified(tail.reverse, head)
        case _ => throw new InternalError("empty identifier list")
      }
    }

    /** Create a qualified name from an identifier */
    def fromIdent(id: Ast.Ident) = Qualified(Nil, id)

  }

}
