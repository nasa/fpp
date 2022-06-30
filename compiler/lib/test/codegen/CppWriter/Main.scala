import fpp.compiler.codegen._
import CppDoc._

object Program extends LineUtils {

  val cppDoc = CppDoc(
    description = "CppDoc test",
    hppFile = HppFile("C.hpp", "N_C_HPP"),
    cppFileName = "C.cpp",
    members = List(
      Member.Lines(
        lines = Lines(
          content = List(
            Line.blank,
            line("#include \"C.hpp\"")
          ),
          output = Lines.Cpp
        )
      ),
      Member.Namespace(
        namespace = Namespace(
          name = "N", 
          members = List(
            Member.Class(
              c = Class(
                comment = None, 
                name = "C",
                superclassDecls = None,
                members = List(
                  Class.Member.Lines(
                    lines = Lines(
                      content = CppDocHppWriter.writeAccessTag("public")
                    )
                  ),
                  Class.Member.Lines(
                    lines = Lines(
                      content = CppDocWriter.writeBannerComment("Nested class"),
                      output = Lines.Both
                    )
                  ),
                  Class.Member.Class(
                    CppDoc.Class(
                      comment = None,
                      name = "N",
                      superclassDecls = None,
                      members = List(
                        Class.Member.Lines(
                          lines = Lines(
                            content = CppDocHppWriter.writeAccessTag("public")
                          )
                        ),
                        Class.Member.Constructor(
                          constructor = Class.Constructor(
                            comment = Some("This is line 1.\nThis is line 2."),
                            params = Nil,
                            initializers = Nil,
                            body = lines("// line1\n// line2")
                          )
                        ),
                        Class.Member.Destructor(
                          Class.Destructor(
                            comment = Some("This is line 1.\nThis is line 2."),
                            body = lines("// Body line 1\n// Body line 2")
                          )
                        ),
                        Class.Member.Function(
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
                        ),
                      )
                    )
                  ),
                  Class.Member.Lines(
                    lines = Lines(
                      content = CppDocWriter.writeBannerComment("Consructors and destructors"),
                      output = Lines.Both
                    )
                  ),
                  Class.Member.Constructor(
                    constructor = Class.Constructor(
                      comment = Some("This is line 1.\nThis is line 2."),
                      params = List(
                        Function.Param(
                          t = Type("const double", None),
                          name = "x",
                          comment = Some("This is parameter x")
                        ),
                        Function.Param(
                          t = Type("const int", None),
                          name = "y",
                          comment = Some("This is parameter y")
                        )
                      ),
                      initializers = List("x(x)", "y(y)"),
                      body = lines("// line1\n// line2")
                    )
                  ),
                  Class.Member.Destructor(
                    Class.Destructor(
                      comment = Some("This is line 1.\nThis is line 2."),
                      body = lines("// Body line 1\n// Body line 2")
                    )
                  ),
                  Class.Member.Lines(
                    lines = Lines(
                      content = CppDocHppWriter.writeAccessTag("public")
                    )
                  ),
                  Class.Member.Lines(
                    Lines(
                      content = CppDocWriter.writeBannerComment("Public member functions"),
                      output = Lines.Both
                    )
                  ),
                  Class.Member.Function(
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
                  ),
                  Class.Member.Lines(
                    lines = Lines(
                      content = List(
                        CppDocHppWriter.writeAccessTag("private"),
                        CppDocWriter.writeBannerComment("Private member variables"),
                        CppDocWriter.writeDoxygenComment("Member variable x"),
                        lines("double x;"),
                        CppDocWriter.writeDoxygenComment("Member variable y"),
                        lines("int y;")
                      ).flatten
                    )
                  )
                )
              )
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
