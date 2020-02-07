package fpp.compiler.util

/** The result of a compilation step */
object Result {

  type Result[T] = Either[Error, T]
  
}
