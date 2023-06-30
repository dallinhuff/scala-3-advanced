package com.rockthejvm.part2afp

object LazyEval:

  val x: Int =
    println("Hello x")
    42

  // lazy delays the evaluation until the first use
  // evaluation occurs once
  lazy val y: Int =
    println("Hello y")
    43

  // call-by-need = call-by-name + lazy vals

  def byNameMethod(n: => Int): Int = n + n + n + 1

  def retrieveMagicVal(): Int =
    println("waiting...")
    Thread.sleep(1000)
    42

  def demoByName(): Unit =
    println(byNameMethod(retrieveMagicVal()))

  def byNeedMethod(n: => Int): Int =
    lazy val lazyN = n // memoization
    lazyN + lazyN + lazyN + 1

  def demoByNeed(): Unit =
    println(byNeedMethod(retrieveMagicVal()))

  // withFilter

  def lessThan30(n: Int): Boolean =
    println(s"$n is less than 30?")
    n < 30

  def greaterThan20(n: Int): Boolean =
    println(s"$n is greater than 20?")
    n > 20

  val numbers: List[Int] = List(1, 25, 40, 5, 23)

  def demoFilter(): Unit =
    val lt30 = numbers.filter(lessThan30)
    val gt20 = lt30.filter(greaterThan20)
    println(gt20)

  def demoWithFilter(): Unit =
    val lt30 = numbers.withFilter(lessThan30)
    val gt20 = lt30.withFilter(greaterThan20)
    println(gt20.map(identity))

  def demoForComp(): Unit =
    val forComp = for
      n <- numbers if lessThan30(n) && greaterThan20(n)
    yield n

  def main(args: Array[String]): Unit =
    println("starting main")
    println(x)
    println(y)
    println(y)
    println("by name")
    demoByName()
    println("by need")
    demoByNeed()
    println("filter")
    demoFilter()
    println("withFilter")
    demoWithFilter()
    println("forComp")
    demoForComp()
