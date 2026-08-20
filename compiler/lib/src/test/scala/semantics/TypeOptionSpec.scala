package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._
import org.scalatest.wordspec.AnyWordSpec

import Helpers._
import Type._
import Types._

/** Tests for the type-option rules used to type state-machine signals, actions,
 *  and guards. These follow the FPP spec section "Type Options"
 *  (docs/spec/Type-Options.adoc): "Conversion of Type Options" and "Computing a
 *  Common Type Option".
 */
class TypeOptionSpec extends AnyWordSpec {

  // Convenience: wrap a type as Some(t)
  private def some(t: Type): TypeOption.T = Some(t)
  private val none: TypeOption.T = None

  "common type option" should {

    // (o1, o2) -> expected common type option
    val resolvablePairs: List[((TypeOption.T, TypeOption.T), TypeOption.T)] = List(
      // Rule 1: None is absorbing
      (some(I32), none) -> none,
      (none, some(I32)) -> none,
      (none, none) -> none,
      // Rule 2.1: identical types
      (some(I32), some(I32)) -> some(I32),
      (some(defaultEnum), some(defaultEnum)) -> some(defaultEnum),
      // Rules 2.3/2.4: same-signedness integers resolve to the wider type
      (some(I8), some(I32)) -> some(I32),
      (some(I32), some(I8)) -> some(I32),
      (some(U16), some(U64)) -> some(U64),
      // Rule 2.5: floats resolve to the wider float
      (some(F32), some(F64)) -> some(F64),
      (some(F64), some(F32)) -> some(F64),
      // Rule 2.6: strings resolve to string
      (some(String(None)), some(String(None))) -> some(String(None)),
      (
        some(stringWithSize("8", 0)),
        some(stringWithSize("16", 1))
      ) -> some(String(None)),
      // Rule 2.2: an alias is replaced with its underlying type, then the rules
      // reapply. alias(I16) vs I32 widens to I32 exactly as bare I16 vs I32.
      (some(aliasType("A", I16, 10)), some(I32)) -> some(I32),
      (some(I32), some(aliasType("A", I16, 10))) -> some(I32),
      // Alias whose underlying type is identical to the other operand
      (some(aliasType("A", I32, 11)), some(I32)) -> some(I32),
      // Chained alias: A2 = A1 = I32
      (
        some(aliasType("A2", aliasType("A1", I32, 12), 13)),
        some(I32)
      ) -> some(I32),
      // Two distinct aliases of the same underlying primitive
      (
        some(aliasType("B1", I32, 14)),
        some(aliasType("B2", I32, 15))
      ) -> some(I32),
      // Alias of a wider int vs a narrower alias, same signedness -> wider
      (
        some(aliasType("W", I32, 16)),
        some(aliasType("N", I8, 17))
      ) -> some(I32),
    )

    // Pairs that have no common type option (resolution is invalid)
    val unresolvablePairs: List[(TypeOption.T, TypeOption.T)] = List(
      // Mixed signedness has no rule
      (some(I32), some(U32)),
      (some(aliasType("A", I32, 20)), some(U32)),
      // Enum is NOT unwrapped to its representation type here (unlike the
      // general Type.commonType); an enum vs. a primitive is a mismatch.
      (some(enumeration("E", I32, 21)), some(I32)),
      (some(aliasType("AE", enumeration("E", I32, 21), 22)), some(I32)),
      // Numeric vs. string
      (some(I32), some(String(None))),
      // Boolean vs. numeric
      (some(Boolean), some(I32)),
    )

    resolvablePairs.zipWithIndex.foreach { case ((pair, expected), i) =>
      s"[$i] resolve ${TypeOption.show(pair._1)} and ${TypeOption.show(pair._2)} to ${TypeOption.show(expected)}" in {
        assert(TypeOption.commonType(pair._1, pair._2) == Some(expected))
      }
    }

    unresolvablePairs.zipWithIndex.foreach { case (pair, i) =>
      s"[$i] not resolve ${TypeOption.show(pair._1)} and ${TypeOption.show(pair._2)}" in {
        assert(TypeOption.commonType(pair._1, pair._2) == None)
      }
    }
  }

  "type option conversion" should {

    // Pairs (o1, o2) where o1 may be converted to o2
    val convertiblePairs: List[(TypeOption.T, TypeOption.T)] = List(
      // Any type option may be converted to None
      (some(I32), none),
      (none, none),
      // Identical types
      (some(I32), some(I32)),
      // Same-signedness widening
      (some(I8), some(I32)),
      (some(U16), some(U64)),
      // Float widening
      (some(F32), some(F64)),
      // Strings
      (some(String(None)), some(String(None))),
      (some(stringWithSize("8", 0)), some(String(None))),
      // Rule 2.2: alias unwrapping. alias(I16) -> I32 (widening after unwrap).
      (some(aliasType("A", I16, 30)), some(I32)),
      (some(I16), some(aliasType("A", I32, 31))),
      (some(aliasType("A", I32, 32)), some(I32)),
    )

    // Pairs where o1 may NOT be converted to o2
    val inconvertiblePairs: List[(TypeOption.T, TypeOption.T)] = List(
      // None -> Some is not allowed
      (none, some(I32)),
      // Narrowing
      (some(I32), some(I16)),
      (some(I32), some(aliasType("A", I16, 33))),
      // Mixed signedness
      (some(I32), some(U32)),
      // Float narrowing
      (some(F64), some(F32)),
      // Enum is not unwrapped -> not convertible to a primitive here
      (some(enumeration("E", I32, 34)), some(I32)),
    )

    convertiblePairs.zipWithIndex.foreach { case (pair, i) =>
      s"[$i] allow ${TypeOption.show(pair._1)} -> ${TypeOption.show(pair._2)}" in {
        assert(TypeOption.isConvertibleTo(pair._1, pair._2))
      }
    }

    inconvertiblePairs.zipWithIndex.foreach { case (pair, i) =>
      s"[$i] not allow ${TypeOption.show(pair._1)} -> ${TypeOption.show(pair._2)}" in {
        assert(!TypeOption.isConvertibleTo(pair._1, pair._2))
      }
    }
  }

}
