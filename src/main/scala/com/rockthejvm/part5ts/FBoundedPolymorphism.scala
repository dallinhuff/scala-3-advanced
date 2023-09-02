package com.rockthejvm.part5ts

import com.rockthejvm.part5ts.FBoundedPolymorphism.FBP.{Animal, Dog}

object FBoundedPolymorphism:

  object NaiveSolution:
    trait Animal:
      def breed: List[Animal]

    class Cat extends Animal:
      override def breed: List[Animal] = List(Cat(), Dog()) // problem! lose type safety with generics

    class Dog extends Animal:
      override def breed: List[Dog] = List(Dog(), Dog()) // solved by manually writing the proper type signature

  object FBP:
    trait Animal[A <: Animal[A]]: // recursive type: f-bounded polymorphism
      def breed: List[Animal[A]]

    class Cat extends Animal[Cat]:
      override def breed: List[Animal[Cat]] = List(Cat(), Cat())

    class Dog extends Animal[Dog]:
      override def breed: List[Animal[Dog]] = List(Dog(), Dog(), Dog())

    // example: some ORM libraries
    trait Entity[E <: Entity[E]]

    // example: comparison/sorting
    class Person extends Comparable[Person]:
      override def compareTo(o: Person): Int = ???

    // "mess up" fbp
    class Crocodile extends Animal[Dog]:
      override def breed: List[Animal[Dog]] = ???

  object FBPSelf:
    trait Animal[A <: Animal[A]] { self: A =>
      def breed: List[Animal[A]]
    }

    class Cat extends Animal[Cat]:
      override def breed: List[Animal[Cat]] = List(Cat(), Cat())

    class Dog extends Animal[Dog]:
      override def breed: List[Animal[Dog]] = List(Dog(), Dog(), Dog())

    // class Crocodile extends Animal[Dog] // doesn't work, doesn't satisfy self type requirement

    trait Fish extends Animal[Fish]
    class Cod extends Fish:
      override def breed: List[Animal[Fish]] = List(Cod(), Cod(), Cod())
    class Shark extends Fish:
      override def breed: List[Animal[Fish]] = List(Shark(), Cod()) // problem!

    trait FishL2[A <: FishL2[A]] extends Animal[FishL2[A]] { self: A => }
    class Tuna extends FishL2[Tuna]:
      override def breed: List[Animal[FishL2[Tuna]]] = List(Tuna(), Tuna())
    class Swordfish extends FishL2[Swordfish]:
      override def breed: List[Animal[FishL2[Swordfish]]] = List(Swordfish(), Swordfish())


