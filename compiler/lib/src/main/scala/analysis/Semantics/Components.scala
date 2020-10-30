package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check components */
object Components {

  /** Checks whether a component is valid */
  def checkValidity(a: Analysis, c: Component): 
    Result.Result[Unit] = {
    // TODO: If component is passive, then no async ports
    // TODO: If component is active or queued, then at least one async input 
    // port or one command
    // TODO: Component provides ports required by dictionaries
    // TODO: No duplicate names in dictionaries
    Right(())
  }

}
