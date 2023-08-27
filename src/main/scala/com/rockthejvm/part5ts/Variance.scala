package com.rockthejvm.part5ts

object Variance:
  class Animal
  class Dog(name: String) extends Animal

  // variance question: if Dog extends Animal, should a List of Dogs be considered a List of Animals?

  // for List, YES (co-variance)
  val lassie = Dog("Lassie")
  val hachi = Dog("Hachi")
  val laika = Dog("Laika")

  val anAnimal: Animal = lassie // ok because Dog <: Animal

  val myDogs: List[Animal] = List(lassie, hachi, laika) // ok, List is COVARIANT

  class MyList[+A] // MyList is COVARIANT in A
  val aListOfAnimals: MyList[Animal] = MyList[Dog]()

  // if NO, then type is INVARIANT
  trait Semigroup[A]:
    def combine(x: A, y: A): A

  // java generics
  // val aJavaList: java.util.ArrayList[Animal] = new java.util.ArrayList[Dog]()

  // NOOO, CONTRAVARIANT
  // if Dog <: Animal, then Vet[Animal] <: Dog
  trait Vet[-A]:
    def heal(animal: A): Boolean

  // if myVet can treat/heal ANY Animal, then it can treat Dog
  val myVet: Vet[Dog] = new Vet[Animal]:
    override def heal(animal: Animal): Boolean = true

  // RULE of THUMB
  // if your type produces/retrieves a value, COVARIANT
  // if your type acts on or consumes a value, CONTRAVARIANT
  // otherwise, INVARIANT

  // EXERCISES

  // 1 - what variance modifiers should these have?
  class RandomGenerator[+A]
  class MyOption[+A]
  class JsonSerializer[-A]
  trait MyFunction[-A, +B]

  // 2
  abstract class LList[+A]:
    def head: A
    def tail: LList[A]

  // Nothing <: anything, so EmptyList <: LList[Int], LList[String], etc.
  case object EmptyList extends LList[Nothing]:
    override def head = throw new NoSuchElementException()
    override def tail = throw new NoSuchElementException()

  case class Cons[+A](override val head: A, override val tail: LList[A]) extends LList[A]

