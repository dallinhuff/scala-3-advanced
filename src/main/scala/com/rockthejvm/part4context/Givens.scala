package com.rockthejvm.part4context

object Givens:

  val aList: List[Int] = List(4, 2, 3, 1)
  val descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  // list sorting - an application of givens
  def sorting(): Unit =
    val anOrderedList = aList.sorted
    val anInverseOrderedList = aList.sorted(descendingOrdering)

    println(anOrderedList)
    println(anInverseOrderedList)

  def givenSorting(): Unit =
    given magicOrdering: Ordering[Int] = descendingOrdering
    val anInverseOrderedListUsingGiven = aList.sorted

    println(anInverseOrderedListUsingGiven)

  def customSorting(): Unit =
    case class Person(name: String, age: Int)
    val people = List(Person("Alice", 29), Person("Jim", 23), Person("Sarah", 34))

    given personOrdering: Ordering[Person] = (x: Person, y: Person) => x.name.compareTo(y.name)
    val sortedPeople = people.sorted

    println(sortedPeople)

  // using clauses
  def demoUsing(): Unit =

    trait Combinator[A]:
      def combine(x: A, y: A): A

    def combineAll[A](list: List[A])(using combinator: Combinator[A]): A =
      list.reduce(combinator.combine)

    given intCombinator: Combinator[Int] with
      override def combine(x: Int, y: Int): Int = x + y

    val firstSum = combineAll(List(1, 2, 3, 4, 5))
    println(firstSum)

    // context bound
    def combineInGroupsOf3[A](list: List[A])(using Combinator[A]) =
      list.grouped(3).map(combineAll).toList

    def combineInGroupsOf3_v2[A : Combinator](list: List[A]) =
      list.grouped(3).map(combineAll).toList

    // synthesize new givens based on existing
    given listOrdering(using intOrdering: Ordering[Int]): Ordering[List[Int]] with
      override def compare(x: List[Int], y: List[Int]): Int =
        x.sum - y.sum

    val listOfLists = List(List(1, 2), List(1, 1), List(3, 4, 5))
    val nestedListsOrdered = listOfLists.sorted

    // with generics
    given orderingByCombinator[A : Ordering](using Combinator[A]): Ordering[List[A]] with
      override def compare(x: List[A], y: List[A]): Int =
        summon[Ordering[A]].compare(combineAll(x), combineAll(y))

    val myCombinator: Combinator[Int] = (x: Int, y: Int) => x * y
    val listProduct = combineAll(List(1, 2, 3, 4))(using myCombinator)

    def summonGiven[A](using theValue: A): A = theValue // or summon[A] from standard lib

    given optionOrdering[A : Ordering]: Ordering[Option[A]] with
      override def compare(x: Option[A], y: Option[A]): Int = (x, y) match
        case (Some(a), Some(b)) => summon[Ordering[A]].compare(a, b)
        case (Some(a), None) => 1
        case (None, Some(b)) => -1
        case (None, None) => 0


  def main(args: Array[String]): Unit =
    sorting()
    givenSorting()
    customSorting()
    demoUsing()
