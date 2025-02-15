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

    override def defTopologyAnnotatedNode(s: DictionaryJsonEncoderState, aNode: Ast.Annotated[AstNode[Ast.DefTopology]]) = {
        val (_, node, _) = aNode
        val data = node.data
        val topSymbol = Symbol.Topology(aNode)
        val fileName = DictionaryJsonEncoderState.getTopologyFileName(s.getName(topSymbol))
        // Given the topology symbol, look up the dictionary in the dictionary map
        val dictionary = s.a.dictionaryMap(topSymbol)
        // Update metadata to use topology name for the name of the deployment
        val updatedMetadata = s.metadata.copy(deploymentName=s.a.getQualifiedName(topSymbol).toString())
        // Generate JSON for dictionary and write JSON to file
        writeJson(s, fileName, DictionaryJsonEncoder(dictionary, s.copy(metadata=updatedMetadata)).dictionaryAsJson)
    }

    override def transUnit(s: DictionaryJsonEncoderState, tu: Ast.TransUnit) =
        visitList(s, tu.members, matchTuMember)

    def writeJson (s: DictionaryJsonEncoderState, fileName: String, json: io.circe.Json) = {
        val path = java.nio.file.Paths.get(s.dir, fileName)
        val file = File.Path(path)
        for (writer <- file.openWrite()) yield {
            writer.println(json)
            writer.close()
            s
        }
    }

}
