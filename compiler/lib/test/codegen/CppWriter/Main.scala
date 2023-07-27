import fpp.compiler.codegen._
import CppDoc._

object Program extends LineUtils {
  val includeHeader = List(
    Line.blank,
    line("#include \"C.hpp\"")
  )

  val cppDoc = CppDoc(
    description = "CppDoc test",
    hppFile = HppFile("C.hpp", "N_C_HPP"),
    cppFileName = "C.cpp",
    members = List(
      Member.Lines(
        lines = Lines(
          content = includeHeader,
          output = Lines.Cpp
        )
      ),
      Member.Lines(
        lines = Lines(
          content = includeHeader,
          output = Lines.Cpp,
          cppFileNameBaseOpt = Some("Other")
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
                            comment = Some("This is line 1.\n\nThis is line 3."),
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
                                Type("const double"),
                                "x",
                                Some("This is parameter x line 1.\n\nThis is parameter x line 3.")
                              ),
                              Function.Param(
                                Type("const int"),
                                "y",
                                Some("This is parameter y line 1.\nThis is parameter y line 2.")
                              )
                            ),
                            retType = Type("void"),
                            body = Nil,
                            cppFileNameBaseOpt = Some("Other")
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
                          t = Type("const double"),
                          name = "x",
                          comment = Some("This is parameter x")
                        ),
                        Function.Param(
                          t = Type("const int"),
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
                          Type("const double"),
                          "x",
                          Some("This is parameter x"),
                          Some("0.0")
                        ),
                        Function.Param(
                          Type("const int"),
                          "y",
                          Some("This is parameter y"),
                          Some("0")
                        )
                      ),
                      retType = Type("void"),
                      body = Nil
                    )
                  ),
                  Class.Member.Function(
                    Function(
                      comment = Some("This is line 1.\nThis is line 2."),
                      name = "g",
                      params = Nil,
                      retType = Type("void"),
                      body = Nil,
                      Function.PureVirtual,
                      Function.Const
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
      ),
      Member.Namespace(
        namespace = Namespace(
          name = "M",
          members = List(
            Member.Class(
              CppDoc.Class(
                comment = None,
                name = "M",
                superclassDecls = None,
                members = List(
                  Class.Member.Lines(
                    lines = Lines(
                      content = CppDocHppWriter.writeAccessTag("public")
                    )
                  ),
                  Class.Member.Constructor(
                    constructor = Class.Constructor(
                      comment = Some("This is line 1.\n\nThis is line 3."),
                      params = Nil,
                      initializers = Nil,
                      body = lines("// line1\n// line2"),
                      Some("Other")
                    )
                  ),
                  Class.Member.Destructor(
                    Class.Destructor(
                      comment = Some("This is line 1.\nThis is line 2."),
                      body = lines("// Body line 1\n// Body line 2"),
                      Class.Destructor.Virtual,
                      Some("Other")
                    )
                  ),
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

object otherCpp {

  def main(args: Array[String]): Unit = {
    val output = CppDocCppWriter.visitCppDoc(Program.cppDoc, Some("Other"))
    output.map(Line.write(Line.stdout) _)
    ()
  }

}
