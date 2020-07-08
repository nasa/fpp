// package fpp.compiler.codegen

// import fpp.compiler.ast._
// import fpp.compiler.codegen._
// import fpp.compiler.util._

// /** Writes out an F Prime XML array as FPP source */
// object ArrayXmlFppWriter extends LineUtils {

//   def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
//     for (tuMember <- FppBuilder.tuMember(file))
//       yield FppWriter.tuMember(tuMember)

//   private object FppBuilder {

//     def defArray(file: XmlFppWriter.File): Result.Result[Ast.DefArray] =
//       for {
//         name <- file.getAttribute(file.elem, "name")
//       }
//       yield {
//         val size = FPPBuilder.arrSize(file)
//         val eltType = FppBuilder.eltType(file)
//         val default = FppBuilder.default(file)
//         val format = FppBuilder.arrFormat(file)

//         Ast.DefArray(name, size, eltType, default, format)
//       }


//   /* Array definition */
//   final case class DefArray(
//     name: Ident,
//     size: AstNode[Expr],
//     eltType: AstNode[TypeName],
//     default: Option[AstNode[Expr]],
//     format: Option[AstNode[String]]
//   )

//     def arrSize(file: XmlFppWriter.File): AstNode[Expr] = {
//         None
//     }

//     def eltType(file: XmlFppWriter.File): AstNode[TypeName] = {
//         None
//     }

//     def default(file: XmlFppWriter.File): Option[AstNode[Expr]] = {
//         None
//     }

//     def arrFormat(file: XmlFppWriter.File): Option[AstNode[String]] = {
//         None
//     }

//     /** Generates the TU member */
//     def tuMember(file: XmlFppWriter.File): Result.Result[Ast.TUMember] = {
//       for (data <- defEnum(file))
//       yield {
//         val moduleNames = XmlFppWriter.getAttributeNamespace(file.elem)
//         val node = AstNode.create(data)
//         val aNode = moduleNames match {
//           case Nil => (a, Ast.TUMember.DefArray(node), Nil)
//           case head :: tail => {
//             val aNodeList1 = List((a, Ast.ModuleMember.DefArray(node), Nil))
//             val aNodeList2 = XmlFppWriter.FppBuilder.encloseWithModuleMemberModules(tail.reverse)(aNodeList1)
//             XmlFppWriter.FppBuilder.encloseWithTuMemberModule(head)(aNodeList2)
//           }
//         }
//         Ast.TUMember(aNode)
//       }      
//     }

//   }
// }