package com.rockthejvm.part2afp

@main
def curryingPAFs(): Unit =
  // currying
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // y => 3 + y
  val eight = add3(5) // 8
  val eight_v2 = superAdder(3)(5) // 8

  // curried methods
  def curriedAdder(x: Int)(y: Int): Int = x + y

  // method != function values
  // eta-expansion: how compiler turns methods into function values
  val add4 = curriedAdder(4) // partial-application of curriedAdder
  val nine = add4(5) // 9

  def increment(x: Int) = x + 1
  val aList = List(1, 2, 3)
  val anIncrementedList = aList.map(increment) // another eta-expansion

  // underscores are powerful
  def concat(a: String, b: String, c: String): String = a + b + c
  val insertName = concat(
    "Hello, my name is ",
    _: String,
    ". I'm going to show you a cool trick."
  ) // x => concat("Hello...", x, ". I'm going to...")
  val danielGreeting = insertName("Daniel")

  val fillInTheBlanks =
    concat(_: String, "Daniel", _: String) // (x, y) => concat(x, "Daniel", y)
  val danielsGreeting_v2 = fillInTheBlanks("Hi, ", "! How are you?")

  val simpleAddFunc = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int): Int = x + y
  def curriedMethod(x: Int)(y: Int): Int = x + y

  val add7_v1 = simpleAddFunc(7, _)
  val add7_v2 = (x: Int) => simpleAddFunc(7, x)
  val add7_v3 = simpleAddFunc.curried(7)
  val add7_v4 = simpleAddMethod(7, _)
  val add7_v5 = (x: Int) => simpleAddMethod(7, x)
  val add7_v6 = simpleAddMethod.curried(7)
  val add7_v7 = (x: Int) => simpleAddMethod(7, x)
  val add7_v8 = curriedMethod(7)
  val add7_v9 = (x: Int) => curriedMethod(7)(x)

  def formatNum(fmt: String)(num: Double) = fmt.format(num)
  val formattedNums = for
    num <- List(1.0, 4.994, 1.0 / 3, Math.PI, 1.3e-12, Math.E)
    fmt <- List("%4.2f", "%8.6f", "%3.1f", "%16.14f")
  yield formatNum(fmt)(num)
  println(formattedNums)

  // methods vs functions + by-name vs 0-lambdas
  def byName(n: => Int) = n + 1
  def byLambda(f: () => Int) = f() + 1
  def method: Int = 42
  def parenMethod(): Int = 42

  byName(42) // ok
  byName(method) // eta-expanded? NO - method is INVOKED here
  byName(parenMethod()) // simple, 43
  // byName(parenMethod) // doesn't work in Scala 3
  byName((() => 42)()) // ok
  // byName(() => 42) // doesn't work

  // byLambda(43) // not ok
  // byLambda(method) // not ok, method can't be eta-expanded here
  byLambda(parenMethod) // eta-expansion makes this work
  byLambda(() => 42) // ok
  byLambda(() => parenMethod()) // ok, this is what byLambda(parenMethod) gets re-written to