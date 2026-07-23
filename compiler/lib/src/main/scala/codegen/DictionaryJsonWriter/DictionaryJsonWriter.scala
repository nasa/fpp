package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._
import io.circe._
import io.circe.syntax._

/** ====================================================================== 
 *  Write out FFP dictionary JSON for each Topology in an Analysis
 *  ======================================================================*/
object DictionaryJsonWriter extends AstStateVisitor {

    type State = DictionaryJsonEncoderState

    override def defModuleAnnotatedNode(
        s: DictionaryJsonEncoderState,
        aNode: Ast.Annotated[AstNode[Ast.DefModule]]
    ) = {
        val (_, node, _) = aNode
        val data = node.data
        visitList(s, data.members, matchModuleMember)
    }

    override def defTopologyAnnotatedNode(
      s: DictionaryJsonEncoderState,
      aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
    ) = {
        val (_, node, _) = aNode
        val data = node.data
        if data.isDeployment
        then
            val fileName = DictionaryJsonEncoderState.getTopologyFileName(data.name)
            val json = constructJson(s, Symbol.Topology(aNode))
            writeJson(s, fileName, json)
        else Right(s)
    }

    override def defSystemAnnotatedNode(
      s: DictionaryJsonEncoderState,
      aNode: Ast.Annotated[AstNode[Ast.DefSystem]]
    ) = {
        val (_, node, _) = aNode
        val data = node.data
        val fileName = {
            val baseName = s.writeSymbolName(Symbol.System(aNode))
            DictionaryJsonEncoderState.getSystemFileName(baseName)
        }
        val json = {
            val Right(topSymbol) = s.a.getTopologySymbol(data.topology.id): @unchecked
            constructJson(s, topSymbol)
        }
        writeJson(s, fileName, json)
    }

    override def transUnit(s: DictionaryJsonEncoderState, tu: Ast.TransUnit) =
        visitList(s, tu.members, matchTuMember)

    private def constructJson(
        s: DictionaryJsonEncoderState,
        topSymbol: Symbol.Topology
    ) = {
        val dictionary = s.a.dictionaryMap(topSymbol)
        val deploymentName = s.a.getQualifiedName(topSymbol).toString()
        val metadata = s.metadata.copy(deploymentName = deploymentName)
        val encoder = DictionaryJsonEncoder(
            dictionary,
            s.copy(metadata = metadata)
        )
        encoder.dictionaryAsJson
    }

    private def writeJson (
      s: DictionaryJsonEncoderState,
      fileName: String,
      json: io.circe.Json
    ) = {
        val path = java.nio.file.Paths.get(s.dir, fileName)
        val file = File.Path(path)
        for (writer <- file.openWrite()) yield {
            writer.println(json)
            writer.close()
            s
        }
    }

}
