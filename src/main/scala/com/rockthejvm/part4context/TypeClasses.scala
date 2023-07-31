package com.rockthejvm.part4context

object TypeClasses:

  // version 1 - OOP way
  trait HtmlWritable:
    def toHtml: String

  case class User(name: String, age: Int, email: String) extends HtmlWritable:
    override def toHtml: String = s"<div>$name ($age yo) <a href=$email/></div>"

  val bob: User = User("Bob", 43, "bob@rockthejvm.com")
  val bobToHtml: String = bob.toHtml
  // same for other structures we want to serialize

  // drawbacks:
  // can only use for data types WE write
  // can only provide ONE implementation

  // version 2 - pattern matching
  object HtmlSerializerPM:
    def serializeToHtml(value: Any): String =
      value match
        case User(name, age, email) => s"<div>$name ($age yo) <a href=$email/></div>"
        case _ => throw new IllegalArgumentException("data structure not supported")

  // drawbacks
  // lost type safety of api
  // need to modify single piece of code every time
  // till only ONE implementation

  // version 3 - type class

  // part 1, type class definition
  trait HtmlSerializer[T]:
    def serialize(value: T): String

  // part 2, type class instances
  given userSerializer: HtmlSerializer[User] with
    override def serialize(user: User): String =
      val User(name, age, email) = user
      s"<div>$name ($age yo) <a href=$email/></div>"

  val bobToHtml_v2: String = userSerializer.serialize(bob)

  // benefits
  // can define serializers for anything
  // multiple serializers for same type

  import java.util.Date
  given dateSerializer: HtmlSerializer[Date] with
    override def serialize(date: Date): String = s"<div>${date.toString}</div>"

  object OtherSerializer:
    given otherUserSerializer: HtmlSerializer[User] with
      override def serialize(user: User): String = s"<div>${user.name}</div>"

  // part 3 - using the type class (user-facing API)
  object HtmlSerializer:
    def serialize[T : HtmlSerializer](value: T): String =
      summon[HtmlSerializer[T]].serialize(value)

    def apply[T: HtmlSerializer]: HtmlSerializer[T] = summon[HtmlSerializer[T]]

  val bobToHtml_v3: String = HtmlSerializer.serialize(bob)
  val bobToHtml_v4: String = HtmlSerializer[User].serialize(bob)

  // part 4 - using extension methods
  object HtmlSyntax:
    extension [T](value: T)
      def html(using serializer: HtmlSerializer[T]): String =
        serializer.serialize(value)

  import HtmlSyntax.*
  val bobToHtml_v5: String = bob.html

  def main(args: Array[String]): Unit =
    println(
      List(
        bobToHtml_v2,
        bobToHtml_v3,
        bobToHtml_v4,
        bobToHtml_v5
      ).forall(_ == bobToHtml)
    )
