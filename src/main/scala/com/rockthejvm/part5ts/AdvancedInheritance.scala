package com.rockthejvm.part5ts

object AdvancedInheritance:

  // 1 - composite types can be used on their own
  trait Writer[T]:
    def write(value: T): Unit

  trait Stream[T]:
    def foreach(f: T => Unit): Unit

  trait Closeable:
    def close(status: Int): Unit

  // don't need to define a new class MyStream[T] extends Writer[T] with Stream[T] with Closeable
  def processStream[T](stream: Writer[T] with Stream[T] with Closeable): Unit =
    stream.foreach(println)
    stream.close(0)

  // 2 - diamond problem

  trait Animal:
    def name: String

  trait Lion extends Animal:
    override def name: String = "Lion"

  trait Tiger extends Animal:
    override def name: String = "Tiger"

  // the "last override" gets picked
  class Liger extends Lion with Tiger

  def demoLiger(): Unit =
    val liger = Liger()
    println(liger.name)

  // 3 - the "super" problem

  trait Cold:
    def print(): Unit = println("cold")

  trait Green extends Cold:
    override def print(): Unit =
      println("green")
      super.print()

  trait Blue extends Cold:
    override def print(): Unit =
      println("blue")
      super.print()

  class Red:
    def print(): Unit = println("red")

  class White extends Red with Green with Blue:
    override def print(): Unit =
      println("white")
      super.print()

  def demoColors(): Unit =
    val white = White()
    white.print()

  def main(args: Array[String]): Unit =
    demoLiger()
    demoColors()
