package com.rockthejvm.practice

import scala.annotation.tailrec

abstract class FSet[A] extends (A => Boolean):
  // main api
  def contains(el: A): Boolean
  def apply(el: A): Boolean = contains(el)

  infix def +(el: A): FSet[A]
  infix def ++(anotherSet: FSet[A]): FSet[A]
  infix def -(el: A): FSet[A]
  infix def --(anotherSet: FSet[A]): FSet[A]
  infix def &(anotherSet: FSet[A]): FSet[A]
  def unary_! : FSet[A] = new PBSet[A](x => !contains(x))

  // "classics"
  def map[B](f: A => B): FSet[B]
  def flatMap[B](f: A => FSet[B]): FSet[B]
  def filter(predicate: A => Boolean): FSet[A]
  def forEach(f: A => Unit): Unit
end FSet

class PBSet[A](property: A => Boolean) extends FSet[A]:
  override def contains(el: A): Boolean = property(el)

  override infix def +(el: A): FSet[A] =
    new PBSet[A](x => x == el || contains(x))
  override infix def ++(anotherSet: FSet[A]): FSet[A] =
    new PBSet[A](x => property(x) || anotherSet(x))
  override infix def -(el: A): FSet[A] = filter(_ != el)
  override infix def --(anotherSet: FSet[A]): FSet[A] = filter(!anotherSet)
  override infix def &(anotherSet: FSet[A]): FSet[A] = filter(anotherSet)

  override def map[B](f: A => B): FSet[B] = politelyFail()
  override def flatMap[B](f: A => FSet[B]): FSet[B] = politelyFail()
  override def filter(predicate: A => Boolean): FSet[A] =
    new PBSet[A](x => property(x) && predicate(x))
  override def forEach(f: A => Unit): Unit = politelyFail()

  private def politelyFail() = throw new RuntimeException("idk how to do that")
end PBSet

case class Empty[A]() extends PBSet[A](_ => false):
  override infix def +(el: A): FSet[A] = Cons(el, this)
  override infix def ++(anotherSet: FSet[A]): FSet[A] = anotherSet
  override def map[B](f: A => B): FSet[B] = Empty()
  override def flatMap[B](f: A => FSet[B]): FSet[B] = Empty()
  override def forEach(f: A => Unit): Unit = ()

case class Cons[A](head: A, tail: FSet[A]) extends FSet[A]:
  override def contains(el: A): Boolean =
    head == el || tail.contains(el)

  override infix def +(el: A): FSet[A] =
    if contains(el) then this
    else Cons(el, this)
  override infix def ++(anotherSet: FSet[A]): FSet[A] =
    tail ++ anotherSet + head

  override infix def -(el: A): FSet[A] =
    if head == el then tail
    else tail - el + head
  override infix def --(anotherSet: FSet[A]): FSet[A] =
    filter(!anotherSet(_))
  override infix def &(anotherSet: FSet[A]): FSet[A] =
    filter(anotherSet)

  override def map[B](f: A => B): FSet[B] =
    tail.map(f) + f(head)
  override def flatMap[B](f: A => FSet[B]): FSet[B] =
    tail.flatMap(f) ++ f(head)
  override def filter(predicate: A => Boolean): FSet[A] =
    val filteredTail = tail.filter(predicate)
    if predicate(head) then Cons(head, filteredTail)
    else filteredTail
  override def forEach(f: A => Unit): Unit =
    f(head)
    tail.forEach(f)
end Cons

object FSet:
  def apply[A](values: A*): FSet[A] =
    @tailrec
    def buildSet(valuesSeq: Seq[A], acc: FSet[A]): FSet[A] =
      if valuesSeq.isEmpty then acc
      else buildSet(valuesSeq.tail, acc + valuesSeq.head)
    buildSet(values, Empty())

object FunctionalSetPlayground:
  def main(args: Array[String]): Unit =
    val first5 = FSet(1, 2, 3, 4, 5)
    val someNums = FSet(4, 5, 6, 7, 8)
    assert(first5.contains(5))
    assert(!first5(6))
    assert((first5 + 10)(10))
    assert(first5.map(_ * 2)(10))
    assert(first5.map(_ % 2)(1))
    assert(!first5.flatMap(x => FSet(x, x + 1))(7))
    assert(!(first5 - 3)(3))
    assert(!(first5 -- someNums)(4))
    val intersect = first5 & someNums
    List(4, 5).foreach(x => assert(intersect(x)))

    val naturals = new PBSet[Int](_ => true)
    assert(naturals(99))
    val notNaturals = !naturals
    assert(!notNaturals(0))
    assert((notNaturals + 1 + 2 + 3).contains(3))

