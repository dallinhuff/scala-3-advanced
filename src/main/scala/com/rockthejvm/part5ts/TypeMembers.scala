package com.rockthejvm.part5ts

object TypeMembers:
  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection:
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal // abstract type member with a type bound
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalAlias = Cat
    type NestedOption = List[Option[Option[Int]]]

  // using type members
  val ac: AnimalCollection = AnimalCollection()
  val anAnimal: ac.AnimalType = ???
  //val cat: ac.BoundedAnimal = Cat() // BoundedAnimal might be Dog
  val aDog: ac.SuperBoundedAnimal = Dog()
  val aCat: ac.AnimalAlias = Cat()

  // establish relationships between types
  // alternative to generics

  class LList[T]:
    def add(el: T): LList[T] = ???

  class MyList:
    type T
    def add(el: T): MyList = ???

  // .type
  type CatType = aCat.type
  val newCat: CatType = aCat