package com.rockthejvm.part5ts

object OpaqueTypes:
  object SocialNetwork:
    // some data structures - "domain"
    opaque type Name = String

    object Name:
      def apply(str: String): Name = str

    extension (name: Name)
      def length: Int = name.length

    // inside, Name <-> String
    def addFriend(person1: Name, person2: Name): Boolean =
      person1.length == person2.length

  // outside, Name and String are NOT related
  import SocialNetwork.*
  val daniel: Name = Name("Daniel")

  object Graphics:
    opaque type Color = Int // in hex
    opaque type ColorFilter <: Color = Int

    val Red: Color = 0xff0000
    val Green: Color = 0x00ff00
    val Blue: Color = 0x0000ff
    val halfTransparency: ColorFilter = 0x88

  import Graphics.*
  case class OverlayFilter(c: Color)
  val fadeLayer: OverlayFilter = OverlayFilter(halfTransparency)

  def main(args: Array[String]): Unit = ()
