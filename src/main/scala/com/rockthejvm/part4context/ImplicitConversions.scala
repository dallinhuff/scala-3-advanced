package com.rockthejvm.part4context

// special import
import scala.language.implicitConversions

object ImplicitConversions:

  case class Person(name: String):
    def greet(): String = s"Hi! I'm $name, how are you?"

  val daniel: Person = Person("Daniel")
  val danielSaysHi: String = daniel.greet()

  // special conversion instance
  given string2Person: Conversion[String, Person] with
    override def apply(x: String): Person = Person(x)

  val danielSaysHi_v2: String = "Daniel".greet() // Person("Daniel").greet()

  def processPerson(person: Person): String =
    if person.name.startsWith("J") then "OK"
    else "NOT OK"

  val isJaneOk: String = processPerson("Jane") // processPerson(Person("Jane"))

  // useful for auto-boxing types or for using two types interchangeably



  def main(args: Array[String]): Unit = ()
