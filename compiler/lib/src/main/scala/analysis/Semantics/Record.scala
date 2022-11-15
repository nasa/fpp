package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP data product containter */
final case class Record(
  aNode: Ast.Annotated[AstNode[Ast.SpecRecord]],
  recordType: Type,
) {

  /** Gets the name of the container */
  def getName = aNode._2.data.name

  /** Gets the location of the container */
  def getLoc: Location = Locations.get(aNode._2.id)

}

object Record {

  type Id = Int

  /** Creates a record from a record specifier */
  def fromSpecRecord(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecRecord]]):
    Record = {
      val node = aNode._2
      val data = node.data
      val recordType = a.typeMap(data.typeName.id)
      Record(aNode, recordType)
    }

}
