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
        // Given the topology symbol, lookup topology in analysis topology map
        val topSymbol = Symbol.Topology(aNode)
        val name = s.getName(topSymbol)
        val topology = s.a.topologyMap(topSymbol)
        val fileName = DictionaryJsonEncoderState.getTopologyFileName(name)
        // Construct dictionary for topology
        val constructedDictionary = Dictionary().buildDictionary(s.a, topology)
        // Generate JSON for dictionary and write JSON to file
        writeJson(s, fileName, DictionaryJsonEncoder(constructedDictionary, s).dictionaryAsJson)
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