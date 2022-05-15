package fpp.compiler.analysis

import fpp.compiler.util._

/** A dependency on the F Prime framework */
sealed trait FrameworkDependency

object FrameworkDependency {

  case object FwComp extends FrameworkDependency {
    override def toString = "Fw_Comp"
  }
  case object FwCompQueued extends FrameworkDependency {
    override def toString = "Fw_CompQueued"
  }
  case object Os extends FrameworkDependency {
    override def toString = "Os"
  }

  val orderMap: Map[FrameworkDependency, Int] = List(
    FwCompQueued,
    Os,
    FwComp
  ).zipWithIndex.toMap

  def sort(s: Seq[FrameworkDependency]): Seq[FrameworkDependency] =
    s.sortWith((a, b) => orderMap(a) < orderMap(b))

}
