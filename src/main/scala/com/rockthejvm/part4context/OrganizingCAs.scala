package com.rockthejvm.part4context

object OrganizingCAs:

  val aList: List[Int] = List(2, 3, 1, 4)
  val anOrderedList: List[Int] = aList.sorted

  // compiler fetches givens/EMs
  // 1 - local scope
  given reverseOrdering: Ordering[Int] with
    override def compare(x: Int, y: Int): Int = y - x

  // 2 - imported scope
  case class Person(name: String, age: Int)
  val people: List[Person] = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 67)
  )

  object PersonGivens:
    given ageOrdering: Ordering[Person] with
      override def compare(x: Person, y: Person): Int = y.age - x.age

  // a - import explicitly
  // import PersonGivens.ageOrdering
  // b - import a given for a particular type
  // import PersonGivens.{given Ordering[Person]}
  // c - import all givens
  import PersonGivens.given

  // 3 - companion objects of types in method signature
  object Person:
    given byNameOrdering: Ordering[Person] with
      override def compare(x: Person, y: Person): Int = x.name.compareTo(y.name)

  val sortedPeople: List[Person] = people.sorted

  // good practice tips
  // 1 - add default given(s) to companion objects
  // 2 - add other (less dominant) givens to separate objects that can be imported
  // 3 - if no dominant/default, add all givens to separate objects and import as needed

  // same principles apply to extension methods

  // Exercises
  case class Purchase(nUnits: Int, unitPrice: Double):
    lazy val totalPrice: Double = nUnits * unitPrice

  object Purchase:
    given totalPriceOrdering: Ordering[Purchase] =
      Ordering.fromLessThan(_.totalPrice < _.totalPrice)

  object UnitCountOrdering:
    given orderByUnitCount: Ordering[Purchase] =
      Ordering.fromLessThan(_.nUnits > _.nUnits)

  object UnitPriceOrdering:
    given orderByUnitPrice: Ordering[Purchase] =
      Ordering.fromLessThan(_.unitPrice < _.unitPrice)

  def main(args: Array[String]): Unit =
    println(anOrderedList)
    println(sortedPeople)
