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
    def toIdentList: List[Unqualified] = (base :: qualifier).reverse

    /** Computes a short qualified name
     *  Deletes the longest prefix provided by the enclosing scope */
    def shortName(enclosingNames: List[Ast.Ident]): Name.Qualified = {
      def helper(prefix: List[String], resultList: List[String]): 
        Name.Qualified  = {
          val result = Name.Qualified.fromIdentList(resultList)
          (prefix, resultList) match {
            case (head1 :: tail1, head2 :: tail2) => 
              if (head1 == head2) helper(tail1, tail2) else result
            case _ => result
          }
        }
      helper(enclosingNames, this.toIdentList)
    }

  }

  object Qualified {

    /** Create a qualified name from a string */
    def fromString(s: String): Qualified = fromIdentList(s.split(".").toList)

    /** Create a qualified name A.B.C from an identifer list [ A, B, C ] */
    def fromIdentList(il: List[Ast.Ident]): Qualified = {
      il.reverse match {
        case head :: tail => Qualified(tail.reverse, head)
        case _ => throw new InternalError("empty identifier list")
      }
    }

    /** Create a qualified name from an identifier */
    def fromIdent(id: Ast.Ident): Qualified = Qualified(Nil, id)

    /** Create a qualified name from a qualified identifier */
    def fromQualIdent(qualIdent: Ast.QualIdent): Qualified =
      fromIdentList(qualIdent.toIdentList)

  }

}
