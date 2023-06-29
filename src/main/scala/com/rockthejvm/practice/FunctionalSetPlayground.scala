package com.rockthejvm.practice

import scala.annotation.tailrec

abstract class FSet[A] extends (A => Boolean):
  // main api
  def contains(el: A): Boolean
  def apply(el: A): Boolean = contains(el)

  infix def +(el: A): FSet[A]
  infix def ++(anotherSet: FSet[A]): FSet[A]

  // "classics"
  def map[B](f: A => B): FSet[B]
  def flatMap[B](f: A => FSet[B]): FSet[B]
  def filter(predicate: A => Boolean): FSet[A]
  def forEach(f: A => Unit): Unit
end FSet

case class Empty[A]() extends FSet[A]:
  override def contains(el: A): Boolean = false

  override infix def +(el: A): FSet[A] = Cons(el, this)
  override infix def ++(anotherSet: FSet[A]): FSet[A] = anotherSet

  override def map[B](f: A => B): FSet[B] = Empty()
  override def flatMap[B](f: A => FSet[B]): FSet[B] = Empty()
  override def filter(predicate: A => Boolean): FSet[A] = this
  override def forEach(f: A => Unit): Unit = ()
end Empty

case class Cons[A](head: A, tail: FSet[A]) extends FSet[A]:
  override def contains(el: A): Boolean =
    head == el || tail.contains(el)

  override infix def +(el: A): FSet[A] =
    if contains(el) then this
    else Cons(el, this)
  override infix def ++(anotherSet: FSet[A]): FSet[A] =
    tail ++ anotherSet + head

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
    assert(first5.contains(5))
    assert(!first5(6))
    assert((first5 + 10)(10))
    assert(first5.map(_ * 2)(10))
    assert(first5.map(_ % 2)(1))
    assert(!first5.flatMap(x => FSet(x, x + 1))(7))
