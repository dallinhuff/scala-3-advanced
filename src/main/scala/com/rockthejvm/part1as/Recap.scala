package com.rockthejvm.part1as

import scala.annotation.tailrec

@main
def recap(): Unit =
  // values, types, expressions
  val aBool = false                       // values are immutable
  val anIfExpr = if aBool then 42 else 55 // expressions collapse to a value

  val aBlock =
    if aBool then 54
    else 78

  // unit = () == "void" in other languages
  val theUnit: Unit = println("a side effect")

  // functions
  def aFunction(x: Int): Int = x + 1

  // recursion: stack & tail
  @tailrec
  def factorial(n: Int, acc: Int = 1): Int =
    if n <= 0 then acc
    else factorial(n - 1, n * acc)

  // OOP
  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog

  trait Carnivore:
    infix def eat(animal: Animal): Unit

  class Crocodile extends Animal with Carnivore:
    override infix def eat(animal: Animal): Unit =
      println("I'm a croc, I eat everything")


  // method notation
  val aCrocodile = new Crocodile
  aCrocodile.eat(aDog)
  aCrocodile eat aDog // "operator"/infix notation

  // anonymous classes
  val aCarnivore = new Carnivore:
    override infix def eat(animal: Animal): Unit =
      println("I'm a carnivore!")

  // generics
  abstract class LList[A]:
    // type A is known inside the implementation here
  end LList

  // singletons and companion objects
  object LList

  // case classes
  case class Person(name: String, age: Int)

  // enums
  enum BasicColors:
    case RED, GREEN, BLUE

  // exceptions
  def throwSomething(): Int =
    throw new RuntimeException("gotcha!")

  val aPotentialFailure =
    try
      throwSomething()
    catch
      case e: Exception => "I caught you"
    finally
      println("some important logs")

  // functional programming
  val increment = (x: Int) => x + 1
  val two = increment(1)

  // hofs
  val incrementedList = List(1, 2, 3).map(increment)
  // map, flatMap, filter

  // for-comprehensions
  val pairs =
    for
      num <- List(1, 2, 3)
      char <- List('a', 'b')
    yield s"$char-$num"

  // collections: Seq, Array, List, Vector, Map, Tuple, Set

  // options, try
  val anOption = Option(42)

  // pattern-matching
  val x = 2
  val order = x match
    case 1 => "first"
    case 2 => "second"
    case _ => "not important"

  val bob = Person("Bob", 22)
  val greeting = bob match
    case Person(n, _) => s"Hi, my name is $n"