package com.rockthejvm.part5ts

object PathDependentTypes:

  class Outer:
    class Inner
    object InnerObject
    type InnerType
    def process(arg: Inner): Unit = println(arg)
    def processGeneral(arg: Outer#Inner): Unit = println(arg)

  val anOuter: Outer = Outer()
  val anInner: anOuter.Inner = anOuter.Inner() // path dependent type

  val outerA: Outer = Outer()
  val outerB: Outer = Outer()

  //val inner2: outerA.Inner = outerB.Inner() // won't compile

  val innerA: outerA.Inner = outerA.Inner()
  val innerB: outerB.Inner = outerB.Inner()

  // outerA.process(innerB) // type mismatch
  anOuter.process(anInner)
  outerA.processGeneral(innerA)
  outerA.processGeneral(innerB)

  // methods with dependent types: return a different COMPILE-TIME type
  trait Record:
    type Key
    def defaultValue: Key

  class StringRecord extends Record:
    override type Key = String
    override def defaultValue: String = ""

  class IntRecord extends Record:
    override type Key = Int
    override def defaultValue: Int = 0

  // user method
  def getDefaultIdentifier(record: Record): record.Key = record.defaultValue
  val aString: String = getDefaultIdentifier(StringRecord())
  val anInt: Int = getDefaultIdentifier(IntRecord())

  // functions with dependent types
  val getDefaultIdentifierFunc: Record => Record#Key = getDefaultIdentifier