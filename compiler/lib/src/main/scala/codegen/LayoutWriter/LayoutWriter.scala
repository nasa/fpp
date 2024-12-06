package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

object LayoutWriter extends AstStateVisitor with LineUtils {
    type State = LayoutWriterState

    override def defModuleAnnotatedNode(
        s: LayoutWriterState,
        aNode: Ast.Annotated[AstNode[Ast.DefModule]]
    ) = {
        val (_, node, _) = aNode
        val data = node.data
        visitList(s, data.members, matchModuleMember)
    }

    override def defTopologyAnnotatedNode(
        s: LayoutWriterState, 
        aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
    ) = {
        val (_, node, _) = aNode
        val data = node.data
        val topSymbol = Symbol.Topology(aNode)
        val name = s.getName(topSymbol)
        val directoryName = LayoutWriterState.getTopologyDirectoryName(name)

        // Delete existing layout directory and files
        val d = new java.io.File(s.dir + "/" + directoryName)
        if (d.exists && d.isDirectory) {
            for(file <- d.listFiles.filter(_.isFile).toList) 
                yield file.delete()
            d.delete()
        }

        // Given the topology symbol, look up topology in analysis topology map
        val topology = s.a.topologyMap(topSymbol)
        for((cGraphName, connections) <- topology.connectionMap) yield {
            val cLines = getConnectionLayoutLines(topology, topology.sortConnections(connections))
            val fileName = LayoutWriterState.getConnectionGraphFileName(cGraphName)
            writeLinesToFile(s, directoryName, fileName, cLines)
        }
        Right(s)
    }

    override def transUnit(s: LayoutWriterState, tu: Ast.TransUnit) =
        visitList(s, tu.members, matchTuMember)

    def getConnectionLayoutLines(
        topology: Topology, 
        cl: Seq[Connection]
    ): List[Line] = {
        cl.foldLeft(List[Line]()) ((l, c) => {
            var fromCi = c.from.port.componentInstance
            var fromPi = c.from.port.portInstance
            var fromPn = topology.fromPortNumberMap(c) 
            var toCi = c.to.port.componentInstance
            var toPi = c.to.port.portInstance
            var toPn = topology.toPortNumberMap(c)
            l ++ List(
                line(fromCi.getUnqualifiedName), 
                line(fromPi.toString), 
                line(fromPn.toString), 
                line(toCi.getUnqualifiedName), 
                line(toPi.toString),
                line(toPn.toString),
                line(""))
        })
    }

    def writeLinesToFile (s: LayoutWriterState, dirName: String, fileName: String, lines: List[Line]) = {
        val path = java.nio.file.Paths.get(s.dir, dirName)
        java.nio.file.Files.createDirectories(path)
        val file = File.Path(java.nio.file.Paths.get(s.dir, dirName, fileName))
        for (writer <- file.openWrite()) yield {
            lines.map(Line.write(writer) _)
            writer.close()
        }
    }
}
