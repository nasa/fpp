import fpp.compiler.codegen._

object Main extends LineUtils {

  def main(args: Array[String]) = {
    val hppFile = "Foo.hpp"
    val includeGuard = "FOO_HPP"
    val lines1 = CppDoc.Lines(lines("// This is a line."))
    val nsMembers = List(CppDoc.Member.Lines(lines1))
    val namespace = CppDoc.Namespace("N", nsMembers)
    val members = List(CppDoc.Member.Namespace(namespace))
    val cppDoc = CppDoc(hppFile, includeGuard, members)
    System.out.println(cppDoc)
    val map = CppDocWriter.visitCppDoc(cppDoc)
    System.out.println(map)
    map(hppFile).map(Line.write(Line.stdout) _)
    ()
  }

}
