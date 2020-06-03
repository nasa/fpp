import fpp.compiler.codegen._
import CppDoc._

object Program extends LineUtils {

  val hppFile = HppFile("Foo.hpp", "FOO_HPP")
  val cppFile = "Foo.cpp"
  val cppDoc = CppDoc(
    hppFile,
    cppFile,
    members = List(
      Member.Namespace(
        Namespace(
          name = "N", 
          members = List(
            Member.Class(
              {
                val publicTag = {
                  val tag = CppDocHppWriter.writeAccessTag("public")
                  val lines = Lines(tag)
                  Class.Member.Lines(lines)
                }
                val constComment = {
                  val comment = CppDocWriter.writeBannerComment("Consructors and destructors")
                  val lines = Lines(comment, Lines.Both)
                  Class.Member.Lines(lines)
                }
                val constMember = {
                  val comment = Some("This is line 1.\nThis is line 2.")
                  val param1 = Function.Param(
                    Type("const unsigned", None),
                    "x",
                    Some("This is parameter x")
                  )
                  val param2 = Function.Param(
                    Type("const int", None),
                    "y",
                    Some("This is parameter y")
                  )
                  val params = List(param1, param2)
                  val const = Class.Constructor(comment, params, List("x(0)", "y(1)"), lines("// line1\n// line2"))
                  Class.Member.Constructor(const)
                }
                val destMember = {
                  val comment = Some("This is line 1.\nThis is line 2.")
                  val body = lines("// Body line 1\n// Body line 2")
                  val dest = Class.Destructor(comment, Class.Destructor.Virtual, body)
                  Class.Member.Destructor(dest)
                }
                val publicFunc = {
                  val comment = CppDocWriter.writeBannerComment("Public member functions")
                  val lines = Lines(comment, Lines.Both)
                  Class.Member.Lines(lines)
                }
                val functionMember = Class.Member.Function(
                  Function(
                    comment = Some("This is line 1.\nThis is line 2."),
                    name = "f",
                    params = List(
                      Function.Param(
                        Type("const double", None),
                        "x",
                        Some("This is parameter x")
                      ),
                      Function.Param(
                        Type("const int", None),
                        "y",
                        Some("This is parameter y")
                      )
                    ),
                    retType = Type("void", None),
                    body = Nil
                  )
                )
                val variables = {
                  val tag = CppDocHppWriter.writeAccessTag("private")
                  val comment = CppDocWriter.writeDoxygenComment("Member variable y")
                  val y = lines("int y;")
                  val content = tag ++ comment ++ y
                  val cppDocLines = Lines(content)
                  Class.Member.Lines(cppDocLines)
                }
                val members = List(
                  publicTag,
                  constComment,
                  constMember,
                  destMember,
                  publicTag,
                  publicFunc,
                  functionMember,
                  variables
                )
                Class(None, "C", None, members)
              }
            )
          )
        )
      )
    )
  )
}

object hpp {

  def main(args: Array[String]): Unit = {
    val output = CppDocHppWriter.visitCppDoc(Program.cppDoc)
    output.map(Line.write(Line.stdout) _)
    ()
  }

}

object cpp {

  def main(args: Array[String]): Unit = {
    val output = CppDocCppWriter.visitCppDoc(Program.cppDoc)
    output.map(Line.write(Line.stdout) _)
    ()
  }

}
