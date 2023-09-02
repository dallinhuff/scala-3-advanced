package com.rockthejvm.part5ts

import scala.util.Try

object HigherKindedTypes:

  class HigherKindedType[F[_]]
  class HigherKindedType2[F[_], G[_], T]

  val hkExample: HigherKindedType[List] = HigherKindedType[List]()
  val hkExample2: HigherKindedType2[List, Option, String] = HigherKindedType2[List, Option, String]()

  // useful for abstract libraries, e.g., Cats
  // example: Functor
  val aList: List[Int] = List(1, 2, 3)
  val anOption: Option[Int] = Option(2)
  val aTry: Try[Int] = Try(42)

  val anIncrementedList: List[Int] = aList.map(_ + 1)
  val anIncrementedOption: Option[Int] = anOption.map(_ + 1)
  val anIncrementedTry: Try[Int] = aTry.map(_ + 1)

  // problem: "duplicated" APIs
  def do10xList(list: List[Int]): List[Int] = list.map(_ * 10)
  def do10xOption(option: Option[Int]): Option[Int] = option.map(_ * 10)
  def do10xTry(theTry: Try[Int]): Try[Int] = theTry.map(_ * 10)
  def combineList[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
    for
      a <- listA
      b <- listB
    yield (a, b)
  def combineOption[A, B](optionA: Option[A], optionB: Option[B]): Option[(A, B)] =
    for
      a <- optionA
      b <- optionB
    yield (a, b)
  def combineTry[A, B](tryA: Try[A], tryB: Try[B]): Try[(A, B)] =
    for
      a <- tryA
      b <- tryB
    yield (a, b)

  // step 1: type class definition
  trait Functor[F[_]]:
    def map[A, B](fa: F[A])(f: A => B): F[B]

  trait Monad[F[_]] extends Functor[F]:
    def flatMap[A, B](container: F[A])(f: A => F[B]): F[B]

  // step 2 - given type class instances
  given listMonad: Monad[List] with
    override def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
    override def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] = list.flatMap(f)
  given optionMonad: Monad[Option] with
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
    override def flatMap[A, B](opt: Option[A])(f: A => Option[B]): Option[B] = opt.flatMap(f)
  given tryMonad: Monad[Try] with
    override def map[A, B](fa: Try[A])(f: A => B): Try[B] = fa.map(f)
    override def flatMap[A, B](tr: Try[A])(f: A => Try[B]): Try[B] = tr.flatMap(f)

  // step 3: extension methods
  extension[F[_], A] (fa: F[A])(using functor: Functor[F])
    def map[B](f: A => B): F[B] = functor.map(fa)(f)

  extension[F[_], A] (containerA: F[A])(using combinable: Monad[F])
    def flatMap[B](f: A => F[B]): F[B] = combinable.flatMap(containerA)(f)

  // step 4: "user-facing" api
  def do10x[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    functor.map(container)(_ * 10)

  // better user-facing api thanks to extension methods
  def do10x_v2[F[_] : Functor](container: F[Int]): F[Int] =
    container.map(_ * 10)

  def combine[F[_] : Monad, A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    for
      a <- fa
      b <- fb
    yield (a, b)


  def main(args: Array[String]): Unit =
    println(do10x(aList))
    println(do10x(anOption))
    println(do10x(aTry))

    println(combine(List("a", "b", "c"), List(1, 2, 3)))
    println(combine(Option(4), Option("Scala")))
    println(combine(Try(42), Try("World")))
