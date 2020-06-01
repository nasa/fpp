package fpp.compiler.codegen

/** A C++ Writer doc visitor */
trait CppWriterDocVisitor {

  type Input

  type Output

  def default(in: Input): Output

  def visitClass(in: Input, c: CppWriterDoc.Class): Output = default(in)
 
  def visitConstructor(in: Input, constructor: CppWriterDoc.Class.Constructor): Output = default(in)

  def visitFunction(in: Input, function: CppWriterDoc.Function): Output = default(in)

  def visitLines(in: Input, function: CppWriterDoc.Lines): Output = default(in)

  def visitNamespace(in: Input, namespace: CppWriterDoc.Namespace): Output = default(in)

  def visitMember(in: Input, member: CppWriterDoc.Member): Output = {
    import CppWriterDoc.Member._
    member match {
      case Class(c) => visitClass(in, c)
      case Lines(lines) => visitLines(in, lines)
      case Function(function) => visitFunction(in, function)
      case Namespace(namespace) => visitNamespace(in, namespace)
    }
  }

  def visitNamespaceMember(in: Input, member: CppWriterDoc.Namespace.Member): Output =
    visitMember(in, member)

  def visitClassMember(in: Input, member: CppWriterDoc.Class.Member): Output = {
    import CppWriterDoc.Class.Member._
    member match {
      case Class(c) => visitClass(in, c)
      case Constructor(constructor) => visitConstructor(in, constructor)
      case Lines(lines) => visitLines(in, lines)
      case Function(function) => visitFunction(in, function)
    }
  }

}
