import fpp.compiler.codegen._

object Main extends LineUtils {

  def main(args: Array[String]) = {
    val hppFile = "Foo.hpp"
    val includeGuard = "FOO_HPP"
    val c = {
      val linesMember = {
        val tag = CppDocWriter.accessTag("public")
        val comment = CppDocWriter.bannerComment("Banner comment")
        val lines = CppDoc.Lines(tag ++ comment)
        CppDoc.Class.Member.Lines(lines)
      }
      val constMember = {
        val const = CppDoc.Class.Constructor(Nil, Nil, Nil)
        CppDoc.Class.Member.Constructor(const)
      }
      val members = List(linesMember, constMember)
      CppDoc.Class("C", None, members)
    }
    val cppDoc = {
      val members = List(CppDoc.Member.Class(c))
      CppDoc(hppFile, includeGuard, members)
    }
    val map = CppDocWriter.visitCppDoc(cppDoc)
    map(hppFile).map(Line.write(Line.stdout) _)
    ()
  }

}
