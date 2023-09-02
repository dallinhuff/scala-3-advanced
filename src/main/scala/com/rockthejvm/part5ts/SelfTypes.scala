package com.rockthejvm.part5ts

object SelfTypes:
  trait Instrumentalist:
    def play(): Unit

  trait Singer { self: Instrumentalist =>
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist:
    override def sing(): Unit = ???
    override def play(): Unit = ???

    // doesn't work, illegal inheritance
    //class Vocalist extends Singer:
      //override def sing(): Unit = ???

  val jamesHetfield: Singer with Instrumentalist = new Singer with Instrumentalist:
    override def sing(): Unit = ???
    override def play(): Unit = ???

  class Guitarist extends Instrumentalist:
    override def play(): Unit = println("some guitar solo")

  val ericClapton: Guitarist with Singer = new Guitarist with Singer:
    override def sing(): Unit = println("Layla")

  // self types vs inheritance
  class A
  class B extends A // B "is an" A

  trait T
  trait S {self: T => } // S "requires a" T (or S must also be a T)

  // self-types for dependency injection = "cake pattern"

  // normal/runtime dependency injection
  abstract class Component
  class ComponentA extends Component
  class ComponentB extends Component

  class DependentComponent(val component: Component)

  // cake pattern
  trait ComponentLayer1:
    def actionLayer1(x: Int): String

  trait ComponentLayer2 { self: ComponentLayer1 =>
    def actionLayer2(x: Int): String
  }

  trait Application { self: ComponentLayer1 with ComponentLayer2 =>
    // the main API
  }

  // example

  trait Picture extends ComponentLayer1
  trait Stats extends ComponentLayer1

  trait ProfilePage extends ComponentLayer2 with Picture
  trait Analytics extends ComponentLayer2 with Stats

  trait AnalyticsApp extends Application with Analytics

  // cyclical inheritance (does not work)
  // class X extends Y
  // class Y extends X

  // cyclical dependencies (works)
  trait X {self: Y => }
  trait Y {self: X => }