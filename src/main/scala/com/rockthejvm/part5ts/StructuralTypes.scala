package com.rockthejvm.part5ts

import reflect.Selectable.reflectiveSelectable
import scala.language.reflectiveCalls

object StructuralTypes:
  type SoundMaker = { // structural type
    def makeSound(): Unit
  }

  class Dog:
    def makeSound(): Unit = println("Bark!")

  class Car:
    def makeSound(): Unit = println("Vroom!")

  // compile-time duck typing
  val dog: SoundMaker = Dog()
  val car: SoundMaker = Car()

  abstract class Animal:
    def eat(): String

  type WalkingAnimal = Animal {
    def walk(): Int
  }

  // why: type-safe APIs for unrelated types that are structured the same
  type JavaCloseable = java.io.Closeable
  class CustomCloseable:
    def close(): Unit = println("ok ok I'm closing")
    def closeSilently(): Unit = println("not making a sound, I promise")

//  def closeResource(closeable: JavaCloseable | CustomCloseable): Unit =
//    // closeable.close() // not ok, because can't figure out similarity of types

  type UnifiedCloseable = {
    def close(): Unit
  }

  def closeResource(closeable: UnifiedCloseable): Unit = closeable.close()
  def closeResourceV2(closeable: {def close(): Unit}): Unit = closeable.close()

  def main(args: Array[String]): Unit =
    dog.makeSound() // through reflection (slow)
    car.makeSound()