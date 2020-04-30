package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._
import org.scalatest.wordspec.AnyWordSpec

import Type._

class TypeSpec extends AnyWordSpec {

  "identical types" should {
    expectIdenticalTypes(
      List(
        duplicate(Boolean),
        duplicate(Float(Float.F32)),
        duplicate(Float(Float.F64)),
        duplicate(Integer),
        duplicate(PrimitiveInt(PrimitiveInt.I16)),
        duplicate(PrimitiveInt(PrimitiveInt.I32)),
        duplicate(PrimitiveInt(PrimitiveInt.I64)),
        duplicate(PrimitiveInt(PrimitiveInt.I8)),
        duplicate(PrimitiveInt(PrimitiveInt.U16)),
        duplicate(PrimitiveInt(PrimitiveInt.U32)),
        duplicate(PrimitiveInt(PrimitiveInt.U64)),
        duplicate(PrimitiveInt(PrimitiveInt.U8)),
        duplicate(String),
        duplicate(AbsType(annotatedNode(Ast.DefAbsType("T"))))
      )
    )
  }

  def annotate[T](x: T) = (List(), x, List())

  def annotatedNode[T](x: T) = annotate(AstNode.create(x))

  def annotatedNode[T](x: T, id: AstNode.Id) = annotate(AstNode.create(x, id))

  def duplicate[T](x: T) = (x, x)

  def expectIdenticalTypes(pairs: List[(Type, Type)]): Unit = {
    pairs.foreach { pair => s"be identical: $pair" in assert(areIdentical(pair._1, pair._2)) }
  }

}
