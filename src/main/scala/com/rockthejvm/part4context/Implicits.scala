package com.rockthejvm.part4context

object Implicits:

  // given/using clauses - the ability to pass arguments implicitly via the compiler
  trait SemiGroup[A]:
    def combine(x: A, y: A): A

  // Scala 2 equivalent of using
  def combineAll[A](list: List[A])(implicit semiGroup: SemiGroup[A]): A =
    list.reduce(semiGroup.combine)

  // Scala 2 equivalent of given
  implicit val intSemiGroup: SemiGroup[Int] = (x: Int, y: Int) => x + y

  val sumOf10: Int = combineAll((1 to 10).toList)

  // Scala 2 equivalent of extension methods/zones
  implicit class MyRichInteger(number: Int) {
    def isEven: Boolean = number % 2 == 0
  }

  val twentyThreeIsEven: Boolean = 23.isEven

  // implicit conversions -- DANGEROUS
  case class Person(name: String) {
    def greet(): String = s"Hi, my name is $name"
  }

  implicit def stringToPerson(x: String): Person = Person(x)
  val danielSaysHi: String = "Daniel".greet()

  // safer use of implicit def, but Scala3 allows better ways to synthesize new given values
  implicit def semiGroupOfOption[A](implicit semiGroup: SemiGroup[A]): SemiGroup[Option[A]] =
    (x: Option[A], y: Option[A]) => for {
      valueX <- x
      valueY <- y
    } yield semiGroup.combine(valueX, valueY)

  def main(args: Array[String]): Unit = ()
