package com.rockthejvm.part1as

@main
def advancedPatternMatching(): Unit =
  /**
   * PM:
   * constants
   * objects
   * wildcards
   * variables
   * infix patterns
   * lists
   * case classes
   */

  class Person(val name: String, val age: Int)
  object Person:
    def unapply(person: Person): Option[(String, Int)] =
      if person.age < 21 then None
      else Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      if age < 21 then Some("minor")
      else Some("legally allowed to drink")
  end Person

  val daniel = new Person("Daniel", 102)
  val danielPM = daniel match
    case Person(n, a) => s"Hi there, I'm $n" // Person.unapply(daniel) => Option((n, a))

  val danielLegalStatus = daniel.age match
    case Person(status) => s"Daniel's legal drinking status is: $status"

  println(danielPM)
  println(danielLegalStatus)

  // boolean patterns
  object Even:
    def unapply(arg: Int): Boolean = arg % 2 == 0

  object SingleDigit:
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10

  val n = 9
  val mathProperty = n match
    case Even() => "an even number"
    case SingleDigit() => "a one digit number"
    case _ => "no special property"
  println(mathProperty)

  // infix patterns
  infix case class Or[A, B](a: A, b: B)
  val anEither = Or(2, "two")
  val humanDescriptionEither = anEither match
    case number Or string => s"$number is written as $string"

  val aList = List(1, 2, 3)
  val listPM = aList match
    case 1 :: rest => "a list starting with 1"
    case _ => "a boring list"

  // decomposing sequences
  val varArg = aList match
    case List(1, _*) => "list starting with 1"
    case _ => "some other list"

  abstract class MyList[A]:
    def head: A = throw new NoSuchElementException
    def tail: MyList[A] = throw new NoSuchElementException

  case class Empty[A]() extends MyList[A]
  case class Cons[A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList:
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if list == Empty() then Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)

  val myList = Cons(1, Cons(2, Cons(3, Empty())))
  val varArgCustom = myList match
    case MyList(1, 2, _*) => "list starting with 1, 2"
    case _ => "some other list"

  // custom return type for unapply (almost always just use options)
  abstract class Wrapper[T]:
    def isEmpty: Boolean
    def get: T

  object PersonWrapper:
    def unapply(person: Person): Wrapper[String] =
      new Wrapper[String]:
        override def isEmpty: Boolean = false
        override def get: String = person.name

  val weirdPersonPM = daniel match
    case PersonWrapper(name) => s"Hey, my name is $name"

  println(weirdPersonPM)