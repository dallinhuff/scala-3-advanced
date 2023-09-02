package com.rockthejvm.part5ts

object LiteralUnionIntersectionTypes:

  // 1 - literal types
  val aNumber = 3
  val three: 3 = 3

  def passNumber(n: Int): Unit = println(n)
  passNumber(45)
  passNumber(three)

  def passStrict(n: 3): Unit = println(n)
  passStrict(3)
  passStrict(three)
  // passStrict(45) // type mismatch

  val pi: 3.14 = 3.14
  val truth: true = true
  val favLang: "Scala" = "Scala"

  def doSomethingWithYourLife(meaning: Option[42]): Unit =
    meaning.foreach(println)

  // 2 - union types
  val truthOr42: Boolean | Int = 43

  def ambivalentMethod(arg: String | Int): String =
    arg match
      case _: String => "a string"
      case _: Int => "an int"

  ambivalentMethod(42)
  ambivalentMethod("42")

  // union types w/ nulls
  type Maybe[T] = T | Null
  def handleMaybe(someVal: Maybe[String]): Int =
    if someVal != null then someVal.length else 0 // flow typing

  // 3 - intersection types
  class Animal
  trait Carnivore
  class Crocodile extends Animal with Carnivore

  trait Gadget:
    def use(): Unit

  trait Camera extends Gadget:
    def takePicture(): Unit = println("smile!")
    override def use(): Unit = println("snap!")

  trait Phone extends Gadget:
    def makeCall(): Unit = println("calling...")
    override def use(): Unit = println("ring!")

  def useSmartDevice(sp: Camera & Phone): Unit =
    sp.takePicture()
    sp.makeCall()
    sp.use()

  class SmartPhone extends Camera with Phone

  val carnivoreAnimal: Animal & Carnivore = Crocodile()

  def main(args: Array[String]): Unit = useSmartDevice(SmartPhone())

  // intersection types & covariance
  trait HostConfig
  trait HostController:
    def get: Option[HostConfig]

  trait PortConfig
  trait PortController:
    def get: Option[PortConfig]

  // option is co-variant, so this works
  def getConfigs(controller: HostController & PortController): Option[HostConfig & PortConfig] =
    controller.get
