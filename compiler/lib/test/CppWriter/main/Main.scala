import fpp.compiler.codegen._

object Main extends LineUtils {

  def main(args: Array[String]) = {
    val hppFile = "Foo.hpp"
    val includeGuard = "FOO_HPP"
    val c = {
      val linesMember1 = {
        val tag = CppDocWriter.accessTag("public")
        val comment = CppDocWriter.bannerComment("Banner comment")
        val lines = CppDoc.Lines(tag ++ comment)
        CppDoc.Class.Member.Lines(lines)
      }
      val constMember = {
        val comment = Some("This is line 1.\nThis is line 2.")
        val param1 = CppDoc.Function.Param(
          CppDoc.Function.Const,
          CppDoc.Type("unsigned", None),
          "x",
          Some("This is parameter x")
        )
        val params = List(param1)
        val const = CppDoc.Class.Constructor(comment, params, Nil, Nil)
        CppDoc.Class.Member.Constructor(const)
      }
      val linesMember2 = {
        val tag = CppDocWriter.accessTag("private")
        val comment = CppDocWriter.doxygenComment("Member variable y")
        val y = lines("int y;")
        val content = tag ++ comment ++ y
        val cppDocLines = CppDoc.Lines(content)
        CppDoc.Class.Member.Lines(cppDocLines)
      }
      val members = List(linesMember1, constMember, linesMember2)
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
