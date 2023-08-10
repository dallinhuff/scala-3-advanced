package com.rockthejvm.part4context

import scala.concurrent.{ExecutionContext, Future}

object ContextFunctions:

  val aList: List[Int] = List(1, 2, 3, 4)
  val sortedList: List[Int] = aList.sorted // works thanks to given/implicit

  // methods can take "using" clauses
  def methodWithoutContextArgs(nonContextArg: Int)(nonContextArg2: String): String = ???
  def methodWithContextArgs(nonContextArg: Int)(using contextArg: String): String = ???

  // eta-expansion doesn't work for methods with "using" clauses

  // context function
  val funcWithContextArgs: Int => String ?=> String = methodWithContextArgs

  val someResult: String = funcWithContextArgs(2)(using "Scala")
  given other: String = "other"
  val otherResult: String = funcWithContextArgs(2)

  // val incrementAsync: Int => Future[Int] = x => Future(x + 1) // doesn't work without execution context in scope

  val incrementAsync: ExecutionContext ?=> Int => Future[Int] =
    x => Future(x + 1)

  def main(args: Array[String]): Unit = ()
