package com.rockthejvm.part3async

import scala.collection.parallel.*
import scala.collection.parallel.CollectionConverters.*
import scala.collection.parallel.immutable.ParVector

object ParallelCollections:
  val aList: List[Int] = (1 to 10000).toList
  val anIncrementedList: List[Int] = aList.map(_ + 1)
  val parList: ParSeq[Int] = aList.par
  val aParallelizedIncrementedList: ParSeq[Int] = parList.map(_ + 1)

  val aParVector: ParVector[Int] = ParVector(1, 2, 3, 4, 5)

  def measure[A](expr: => A): Long =
    val time = System.currentTimeMillis()
    expr
    System.currentTimeMillis() - time

  def compareListTransformation(): Unit =
    val list = (1 to 30000000).toList
    println("list creation done")

    val serialTime = measure(list.map(_ + 1))
    println(s"serial time: $serialTime")

    val parTime = measure(list.par.map(_ + 1))
    println(s"parallel time: $parTime")

  def demoUndefinedOrder(): Unit =
    val aList = (1 to 1000).toList
    val reduction = aList.reduce(_ - _) // non-associative reduce method
    val parallelReduction = aList.par.reduce(_ - _)

    println(s"sequential reduction: $reduction")
    println(s"parallel reduction: $parallelReduction")

  def demoDefinedOrder(): Unit =
    val strings = "I love parallel collections but I must be careful".split(" ").toList

    val concat = strings.reduce(_ + " " + _)
    val parallelConcat = strings.par.reduce(_ + " " + _)

    println(s"sequential reduction: $concat")
    println(s"parallel reduction: $parallelConcat")

  def demoRaceConditions(): Unit =
    def race(): Int =
      var sum = 0
      (1 to 1000).toList.par.foreach(elem => sum += elem)
      sum

    val sums = (1 to 200).map(_ => race()).toSet
    println(s"number of different results: ${sums.size}")

  def main(args: Array[String]): Unit =
    // compareListTransformation()
    demoUndefinedOrder()
    demoDefinedOrder()
    demoRaceConditions()

