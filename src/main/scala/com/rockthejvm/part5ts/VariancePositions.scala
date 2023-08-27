package com.rockthejvm.part5ts

object VariancePositions:
  class Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // type bounds
  class Cage[A <: Animal] // A must be a subtype of Animal
  // val aCage = Cage[String]() // doesn't work, String not subtype of Animal
  val aRealCage = Cage[Dog]()

  class WeirdContainer[A >: Animal] // A must be a supertype of animal

  // variance positions

  /*
    // types of val fields are in COVARIANT position, so this won't work
    class Vet[-T](val favoriteAnimal: T)

    // example why this doesn't work
    val garfield = Cat()
    val theVet: Vet[Animal] = Vet[Animal](garfield)
    val dogVet: Vet[Dog] = theVet // Vet is contravariant
    val aDog: Dog = dogVet.favoriteAnimal // should be guaranteed to be a Dog, but is a Cat
  */

  // types of var fields are also in COVARIANT position
  // (same reason)

  /*
    // types of var fields are in CONTRAVARIANT position, so this won't work
    class MutableOption[+T](var contents: T)

    // example why this doesn't work
    val maybeAnimal: MutableOption[Animal] = MutableOption[Dog](Dog())
    maybeAnimal.contents = Cat() // type-conflict!
   */

  /*
    // types of method arguments are in CONTRAVARIANT position, so this won't work
    class MyList[+A]:
      def add(el: A): MyList[A] = ???

    // example why this won't work:
    val animals: MyList[Animal] = MyList[Cat]()
    val biggerListOfAnimals: MyList[Animal] = animals.add(Dog()) // type conflict!
   */

  class Vet[-T]:
    def heal(animal: T): Boolean = true

  /*
    // method return types are in COVARIANT position
    abstract class Vet2[-T]:
      def rescueAnimal(): T

    // example why this doesn't work
    val vet: new Vet2[Animal]:
      override def rescueAnimal(): Animal = Cat()

    val lassiesVet: Vet2[Dog] = vet
    val rescueDog: Dog = lassiesVet.rescueAnimal() // type conflict!
   */

  // solving variance position problems
  abstract class LList[+A]:
    def head: A
    def tail: LList[A]
    def add[B >: A](el: B): LList[B] // widen the type

  class Vehicle
  class Car extends Vehicle
  class SuperCar extends Car

  class RepairShop[-A <: Vehicle]:
    def repair[B <: A](vehicle: B): B = vehicle // narrow the return type

  val myRepairShop: RepairShop[Car] = RepairShop[Vehicle]()

  val myBeatUpCar: Car = Car()
  val freshCar: Car = myRepairShop.repair(myBeatUpCar)

  val damagedFerrari: SuperCar = SuperCar()
  val fixedFerrari: SuperCar = myRepairShop.repair(damagedFerrari)


  def main(args: Array[String]): Unit =
    ()
