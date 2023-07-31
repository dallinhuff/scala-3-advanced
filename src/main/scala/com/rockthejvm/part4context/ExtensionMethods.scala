package com.rockthejvm.part4context

import scala.annotation.tailrec

object ExtensionMethods:

  case class Person(name: String):
    def greet: String = s"Hi, my name is $name"

  extension (string: String)
    def greetAsPerson: String = Person(string).greet

  val danielGreeting: String = "Daniel".greetAsPerson

  // generic extension methods
  extension [A](list: List[A])
    def ends: (A, A) = (list.head, list.last)

  val aList: List[Int] = List(1, 2, 3, 4)
  val aListEnds: (Int, Int) = aList.ends

  // reason: make APIs very expressive
  // reason: enhance certain types with new capabilities
  trait Semigroup[A]:
    def combine(x: A, y: A): A

  extension [A](list: List[A])
    def combineAll(using combinator: Semigroup[A]) =
      list.reduce(combinator.combine)

  given intCombinator: Semigroup[Int] = (x: Int, y: Int) => x + y
  val firstSum: Int = aList.combineAll

  val someStrings: List[String] = List("I", "love", "Scala")
  // val stringSum: String = someStrings.combineAll // doesn't compile, no given Combinator[String]

  // grouping extensions
  object GroupedExtensions:
    extension [A](list: List[A])
      def ends: (A, A) = (list.head, list.last)
      def combineAll(using combinator: Semigroup[A]) =
        list.reduce(combinator.combine)

    val sum: Int = List(1, 2, 3).combineAll

  extension (num: Int)
    def isPrime: Boolean =
      @tailrec
      def isPrimeAux(potDiv: Int): Boolean =
        if potDiv > num / 2 then true
        else if num % potDiv == 0 then false
        else isPrimeAux(potDiv + 1)

      assert(num >= 0)
      if num == 0 || num == 1 then false
      else isPrimeAux(2)

  // "library code", cannot change
  sealed abstract class Tree[A]
  case class Leaf[A](value: A) extends Tree[A]
  case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

  extension [A](tree: Tree[A])
    def map[B](f: A => B): Tree[B] =
      tree match
        case Leaf(value) => Leaf(f(value))
        case Branch(left, right) => Branch(left.map(f), right.map(f))

    def forall(pred: A => Boolean): Boolean =
      tree match
        case Leaf(value) => pred(value)
        case Branch(left, right) => left.forall(pred) && right.forall(pred)

    def sum(using comb: Semigroup[A]): A =
      tree match
        case Leaf(value) => value
        case Branch(left, right) => comb.combine(left.sum, right.sum)

  def main(args: Array[String]): Unit =
    println(danielGreeting)
    println(2003.isPrime)

    val aTree = Branch(
      Branch(Leaf(3), Leaf(4)),
      Leaf(2)
    )

    val mappedTree = aTree.map(_ + 1)
    val allPrime = mappedTree.forall(_.isPrime)
    val sumTree = mappedTree.sum

    println(mappedTree)
    println(allPrime)
    println(sumTree)
