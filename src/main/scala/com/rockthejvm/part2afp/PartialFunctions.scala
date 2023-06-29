package com.rockthejvm.part2afp

@main
def partialFunctions(): Unit =
  val aFunction = (x: Int) => x + 1

  val aFussyFunction = (x: Int) =>
    if x == 1 then 42
    else if x == 2 then 56
    else if x == 5 then 999
    else throw new RuntimeException("no suitable cases possible")

  // a partial function: only applicable to a subset of the Int type
  val aFussyFunction_v2 = (x: Int) => x match
    case 1 => 42
    case 2 => 56
    case 5 => 999

  // same as aFussyFunction_v2
  val aPartialFunction: PartialFunction[Int, Int] =
    case 1 => 42
    case 2 => 56
    case 5 => 999

  val canCallOn37 = aPartialFunction.isDefinedAt(37)
  val liftedPF = aPartialFunction.lift // Int => Option[Int]

  val anotherPF: PartialFunction[Int, Int] =
    case 45 => 86

  val pfChain = aPartialFunction.orElse(anotherPF)

  println(liftedPF(5))
  println(liftedPF(37))
  println(pfChain(45))

  // HOFs accept PFs as arguments (because PartialFunction extends Function)
  val aList = List(1, 2, 3, 4)
  val aChangedList = aList.map {
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case 4 => 67
    case _ => 0
  }

  case class Person(name: String, age: Int)

  val someKids = List(
    Person("Alice", 3),
    Person("Bobbie", 5),
    Person("Jane", 4)
  )

  val kidsGrowingUp = someKids.map {
    case Person(name, age) => Person(name, age + 1)
  }
