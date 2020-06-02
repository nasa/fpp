import fpp.compiler.codegen._

object Main extends LineUtils {

  def main(args: Array[String]) = {
    val hppFile = CppDoc.HppFile("Foo.hpp", "FOO_HPP")
    val cppFile = "Foo.cpp"
    val namespace = {
      val c = {
        val publicTag = {
          val tag = CppDocWriter.accessTag("public")
          val lines = CppDoc.Lines(tag)
          CppDoc.Class.Member.Lines(lines)
        }
        val constComment = {
          val comment = CppDocWriter.bannerComment("Consructors and destructors")
          val lines = CppDoc.Lines(comment, CppDoc.Lines.Both)
          CppDoc.Class.Member.Lines(lines)
        }
        val constMember = {
          val comment = Some("This is line 1.\nThis is line 2.")
          val param1 = CppDoc.Function.Param(
            CppDoc.Type("const unsigned", None),
            "x",
            Some("This is parameter x")
          )
          val param2 = CppDoc.Function.Param(
            CppDoc.Type("const int", None),
            "y",
            Some("This is parameter y")
          )
          val params = List(param1, param2)
          val const = CppDoc.Class.Constructor(comment, params, List("x(0)", "y(1)"), lines("// line1\n// line2"))
          CppDoc.Class.Member.Constructor(const)
        }
        val destMember = {
          val comment = Some("This is line 1.\nThis is line 2.")
          val body = lines("// Body line 1\n// Body line 2")
          val dest = CppDoc.Class.Destructor(comment, CppDoc.Class.Destructor.Virtual, body)
          CppDoc.Class.Member.Destructor(dest)
        }
        val publicFunc = {
          val tag = CppDocWriter.accessTag("public")
          val comment = CppDocWriter.bannerComment("Public member functions")
          val lines = CppDoc.Lines(tag ++ comment)
          CppDoc.Class.Member.Lines(lines)
        }
        val functionMember = {
          val comment = Some("This is line 1.\nThis is line 2.")
          val name = "f"
          val param1 = CppDoc.Function.Param(
            CppDoc.Type("const double", None),
            "x",
            Some("This is parameter x")
          )
          val param2 = CppDoc.Function.Param(
            CppDoc.Type("const int", None),
            "y",
            Some("This is parameter y")
          )
          val params = List(param1, param2)
          val retType = CppDoc.Type("void", None)
          val body = Nil
          val svQualifier = CppDoc.Function.NonSV
          val constQualifier = CppDoc.Function.NonConst
          val function = CppDoc.Function(comment, name, params, retType, body, svQualifier, constQualifier)
          CppDoc.Class.Member.Function(function)
        }
        val variables = {
          val tag = CppDocWriter.accessTag("private")
          val comment = CppDocWriter.doxygenComment("Member variable y")
          val y = lines("int y;")
          val content = tag ++ comment ++ y
          val cppDocLines = CppDoc.Lines(content)
          CppDoc.Class.Member.Lines(cppDocLines)
        }
        val members = List(publicTag, constComment, constMember, destMember, publicFunc, functionMember, variables)
        CppDoc.Class("C", None, members)
      }
      val member = CppDoc.Member.Class(c)
      CppDoc.Namespace("N", List(member))
    }
    val cppDoc = {
      val members = List(CppDoc.Member.Namespace(namespace))
      CppDoc(hppFile, cppFile, members)
    }
    val output = CppDocWriter.visitCppDoc(cppDoc)
    output.cppLines.map(Line.write(Line.stdout) _)
    ()
  }

}
