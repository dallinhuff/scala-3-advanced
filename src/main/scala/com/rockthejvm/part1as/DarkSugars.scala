package com.rockthejvm.part1as

import scala.annotation.targetName
import scala.util.Try

@main
def darkSugars(): Unit =

  // 1: sugar for methods with one argument

  def singleArgMethod(arg: Int): Int = arg + 1

  val aMethodCall = singleArgMethod({
    // long code
    42
  })

  val anotherMethodCall = singleArgMethod {
    // long code
    42
  }
  
  val anotherMethodCall_v2 = singleArgMethod:
    // long code
    42

  // example: Try, Future
  val aTryInstance = Try {
    throw new RuntimeException("gotcha")
  }
  
  val aTryInstance_v2 = Try:
    throw new RuntimeException("gotcha")

  // hofs
  val anIncrementedList = List(1, 2, 3).map { x =>
    // long code
    x + 1
  }
  
  val anIncrementedList_v2 = List(1, 2, 3).map: x =>
    // long code
    x + 1


  // 2: single abstract method pattern (since Scala 2.12)

  trait Action:
    // can also have other implemented fields/methods, but
    // can only have one unimplemented/abstract method
    def act(x: Int): Int

  val anAction = new Action:
    override def act(x: Int): Int = x + 1

  val anotherAction: Action = (x: Int) => x + 1

  // example: Runnable
  val aThread = new Thread(new Runnable:
    override def run(): Unit = println("Hello from another thread!")
  )

  val aSweeterThread = new Thread(() => println("Hello from another thread!"))


  // 3: methods ending in a colon are right-associative

  val aList = List(1, 2, 3, 4)
  val aPrependedList = 0 :: aList // aList.::(0)
  val aBigList = 0 :: 1 :: 2 :: 3 :: List(4) // List(4).::(3).::(2).::(1).::(0)

  class MyStream[A]:
    @targetName("ArrowStream")
    infix def -->:(value: A): MyStream[A] = this

  val myStream = 0 -->: 1 -->: 2 -->: 3 -->: 4 -->: new MyStream[Int]


  // 4: multi-word identifiers

  class Talker(name: String):
    infix def `totally said`(gossip: String): Unit = println(s"$name said $gossip")

  val daniel = new Talker("Daniel")
  daniel `totally said` "I love Scala"

  // example: HTTP libraries
  object `Content-Type`:
    val `application/json` = "application/JSON"


  // 5: infix types

  @targetName("Arrow")
  infix class -->[A, B]
  val compositeType: Int --> String = new -->[Int, String]

  // 6: update

  val anArray = Array(1, 2, 3, 4)
  anArray.update(2, 45)
  anArray(2) = 45

  // 7: mutable fields (use _= in setter name)

  class Mutable:
    private var internal: Int = 0
    def member: Int = internal
    def member_=(value: Int): Unit =
      internal = value

  val mutableContainer = new Mutable
  mutableContainer.member = 42

  // 8: variable arguments

  def methodWithVarArgs(args: Int*) = args.length

  val callWithNoArgs = methodWithVarArgs()
  val callWithOneArg = methodWithVarArgs(78)
  val callWithFourArgs = methodWithVarArgs(78, 99, 11, 23)

  val aCollection = List(1, 2, 3, 4)
  val callWithDynArgs = methodWithVarArgs(aCollection*)
