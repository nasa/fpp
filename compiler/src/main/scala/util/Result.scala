package fpp.compiler.util

object Result {

  type Result[T] = Either[Error, T]
  
}
