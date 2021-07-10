package fpp.compiler.codegen

/** A C++ doc visitor */
trait CppDocVisitor {

  type Input

  type Output

  def default(in: Input): Output

  def visitClass(in: Input, c: CppDoc.Class): Output = default(in)
 
  def visitConstructor(in: Input, constructor: CppDoc.Class.Constructor): Output = default(in)

  def visitDestructor(in: Input, destructor: CppDoc.Class.Destructor): Output = default(in)

  def visitFunction(in: Input, function: CppDoc.Function): Output = default(in)

  def visitLines(in: Input, function: CppDoc.Lines): Output = default(in)

  def visitNamespace(in: Input, namespace: CppDoc.Namespace): Output = default(in)

  def visitMember(in: Input, member: CppDoc.Member): Output = {
    import CppDoc.Member._
    member match {
      case Class(c) => visitClass(in, c)
      case Lines(lines) => visitLines(in, lines)
      case Function(function) => visitFunction(in, function)
      case Namespace(namespace) => visitNamespace(in, namespace)
    }
  }

  def visitNamespaceMember(in: Input, member: CppDoc.Member): Output =
    visitMember(in, member)

  def visitClassMember(in: Input, member: CppDoc.Class.Member): Output = {
    import CppDoc.Class.Member._
    member match {
      case Class(c) => visitClass(in, c)
      case Constructor(constructor) => visitConstructor(in, constructor)
      case Destructor(destructor) => visitDestructor(in, destructor)
      case Lines(lines) => visitLines(in, lines)
      case Function(function) => visitFunction(in, function)
    }
  }

}
