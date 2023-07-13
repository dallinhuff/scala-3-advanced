package com.rockthejvm.part2afp

import scala.annotation.targetName

lazy val listStory: Boolean =
  val aList = List(1, 2, 3)

  val listMultiply = for
    x <- aList
    y <- List(4, 5, 6)
  yield x * y

  val listMultiply_v2 = aList.flatMap(x => List(4, 5, 6).map(_ * x))

  val f = (x: Int) => List(x, x + 1)
  val g = (x: Int) => List(x, 2 * x)
  val pure = (x: Int) => List(x)

  // prop1: left identity
  val leftIdentity = pure(42).flatMap(f) == f(42) // for every x, for every f

  // prop2: right identity
  val rightIdentity = aList.flatMap(pure) == aList // for every List

  // prop3: associativity (guarantees the order of application of f and g)
  val associativity = aList.flatMap(f).flatMap(g) == aList.flatMap(f(_).flatMap(g))

  leftIdentity && rightIdentity && associativity

lazy val optionStory: Boolean =
  val anOption = Option(42)

  val optionString = for
    lang <- Option("Scala")
    ver <- Option(3)
  yield s"$lang $ver"

  val optionString_v2 = Option("Scala").flatMap(lang => Option(3).map(ver => s"$lang $ver"))

  val f = (x: Int) => Option(x + 1)
  val g = (x: Int) => Option(x * 2)
  val pure = (x: Int) => Option(x)

  val leftIdentity = pure(42).flatMap(f) == f(42) // for every x, for every f
  val rightIdentity = anOption.flatMap(pure) == anOption // for every Option
  val associativity = anOption.flatMap(f).flatMap(g) == anOption.flatMap(f(_).flatMap(g)) // Option(86)

  leftIdentity && rightIdentity && associativity

// MONADS: chain dependent computations

// a Monad that describes computations (but doesn't run them on construction)
// calling unsafeRun() performs the computation
case class IO[A](unsafeRun: () => A):
  def map[B](f: A => B): IO[B] =
    IO(() => f(unsafeRun()))
  def flatMap[B](f: A => IO[B]): IO[B] =
    IO(() => f(unsafeRun()).unsafeRun())

object IO:
  @targetName("pure")
  def apply[A](value: => A): IO[A] = new IO(() => value)

lazy val ioStory: Boolean =
  val aPossibleMonad = IO(42)

  val f = (x: Int) => IO(x + 1)
  val g = (x: Int) => IO(x * 2)
  val pure = (x: Int) => IO(x)

  def compareContents[A](a: IO[A], b: IO[A]) = a.unsafeRun() == b.unsafeRun()

  val leftIdentity = compareContents(pure(42).flatMap(f), f(42))
  val rightIdentity = compareContents(aPossibleMonad.flatMap(pure), aPossibleMonad)
  val associativity = compareContents(aPossibleMonad.flatMap(f).flatMap(g), aPossibleMonad.flatMap(f(_).flatMap(g)))

  leftIdentity && rightIdentity && associativity

def ioExamples(): Unit =
  val aPossiblyMonad = IO:
    println("a PM")
    42

  val anotherPM = IO:
    println("another PM")
    "Scala"

  val aForComprehension = for
    num <- aPossiblyMonad
    lang <- anotherPM
  yield s"$num - $lang"

  println(aForComprehension.unsafeRun())

  val fs = (x: Int) => IO:
    println("incrementing")
    x + 1

  val gs = (x: Int) =>
    println("doubling")
    x * 2


@main
def monads(): Unit =
  println(s"List has properties: $listStory")
  println(s"Option has properties: $optionStory")
  println(s"PossiblyMonad has properties: $ioStory")
  println()
  ioExamples()

