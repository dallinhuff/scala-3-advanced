package com.rockthejvm.practice

import scala.annotation.tailrec

abstract class LzList[A]:
  def isEmpty: Boolean
  def head: A
  def tail: LzList[A]

  // utilities
  def #::(el: A): LzList[A] // prepending
  infix def ++(other: => LzList[A]): LzList[A] // concat

  // classics
  def foreach(f: A => Unit): Unit =
    @tailrec
    def foreachTailRec(lz: LzList[A]): Unit =
      if lz.isEmpty then ()
      else
        f(lz.head)
        foreachTailRec(lz.tail)
    foreachTailRec(this)

  def map[B](f: A => B): LzList[B]
  def flatMap[B](f: A => LzList[B]): LzList[B]
  def filter(pred: A => Boolean): LzList[A]
  def withFilter(pred: A => Boolean): LzList[A] = filter(pred)

  def take(n: Int): LzList[A]
  def takeAsList(n: Int): List[A] = take(n).toList
  def toList: List[A] =
    @tailrec
    def toListAux(remaining: LzList[A], acc: List[A]): List[A] =
      if remaining.isEmpty then acc.reverse
      else toListAux(remaining.tail, remaining.head +: acc)
    toListAux(this, List())

case class LzEmpty[A]() extends LzList[A]:
  override def isEmpty: Boolean = true
  override def head: A = throw new NoSuchElementException
  override def tail: LzList[A] = throw new NoSuchElementException

  override def #::(el: A): LzList[A] = new LzCons(el, this)
  override infix def ++(other: => LzList[A]): LzList[A] = other

  override def map[B](f: A => B): LzList[B] = LzEmpty()
  override def flatMap[B](f: A => LzList[B]): LzList[B] = LzEmpty()
  override def filter(pred: A => Boolean): LzList[A] = this

  override def take(n: Int): LzList[A] =
    if n == 0 then this
    else throw new IndexOutOfBoundsException(s"cannot take $n elements from empty lazy list")

class LzCons[A](hd: => A, tl: => LzList[A]) extends LzList[A]:
  override def isEmpty: Boolean = false
  override lazy val head: A = hd
  override lazy val tail: LzList[A] = tl

  override def #::(el: A): LzList[A] = new LzCons(el, this)

  override infix def ++(other: => LzList[A]): LzList[A] =
    new LzCons(head, tail ++ other)

  override def map[B](f: A => B): LzList[B] =
    new LzCons(f(head), tail.map(f))

  override def flatMap[B](f: A => LzList[B]): LzList[B] =
    f(head) ++ tail.flatMap(f)

  override def filter(pred: A => Boolean): LzList[A] =
    if pred(head) then new LzCons(head, tail.filter(pred))
    else tail.filter(pred)

  override def take(n: Int): LzList[A] =
    if n <= 0 then LzEmpty()
    else if n == 1 then new LzCons(head, LzEmpty())
    else new LzCons(head, tail.take(n - 1))

object LzList:
  def empty[A]: LzList[A] = LzEmpty[A]()

  def generate[A](start: A)(generator: A => A): LzList[A] =
    new LzCons(start, LzList.generate(generator(start))(generator))

  def from[A](list: List[A]): LzList[A] =
    list.reverse.foldLeft(empty)((acc, curr) => new LzCons(curr, acc))

  def apply[A](values: A*): LzList[A] = from(values.toList)

  def fibonacci: LzList[BigInt] =
    def fibo(first: BigInt, second: BigInt): LzList[BigInt] =
      new LzCons(first, fibo(second, first + second))
    fibo(1, 2)

  def eratosthenes: LzList[Int] =
    def isPrime(n: Int) =
      def isPrimeTailRec(divisor: Int): Boolean =
        if divisor < 2 then true
        else if n % divisor == 0 then false
        else isPrimeTailRec(divisor - 1)
      isPrimeTailRec(n / 2)

    def sieve(nums: LzList[Int]): LzList[Int] =
      if nums.isEmpty then nums
      else if !isPrime(nums.head) then sieve(nums.tail)
      else new LzCons(nums.head, sieve(nums.tail.filter(_ % nums.head != 0)))

    sieve(generate(2)(_ + 1))

object LzListPlayground:
  def main(args: Array[String]): Unit =
    val naturals = LzList.generate(1)(_ + 1) // INFINITE list of natural numbers
    println(naturals.head)
    println(naturals.tail.head)
    println(naturals.tail.tail.head)

    val first50k = naturals.take(50000)
    first50k.foreach(println)
    val first50kList = first50k.toList
    println(first50kList)

    println(naturals.map(_ * 2).takeAsList(100))
    println(naturals.flatMap(x => LzList(x, x + 1)).takeAsList(100))
    println(naturals.filter(_ < 10).takeAsList(9))
    // println(naturals.filter(_ < 10).takeAsList(10)) // crashes with SO error

    val combinationsLazy = for
      num <- LzList(1, 2, 3)
      str <- LzList("black", "white")
    yield s"$num-$str"
    println(combinationsLazy.toList)

    val lazyFibs = LzList.fibonacci
    println(lazyFibs.takeAsList(100))

    val lazyPrimes = LzList.eratosthenes
    println(lazyPrimes.takeAsList(100))
