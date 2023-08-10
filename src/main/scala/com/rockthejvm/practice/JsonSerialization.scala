package com.rockthejvm.practice

import java.util.Date

object JsonSerialization:

  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
    1 - intermediate data
    2 - type class to convert
    3 - serialize
  */

  sealed trait JsonValue:
    def stringify: String

  final case class JsonString(value: String) extends JsonValue:
    override def stringify: String = "\"" + value + "\""

  final case class JsonNumber(value: Int) extends JsonValue:
    override def stringify: String = value.toString

  final case class JsonArray(values: List[JsonValue]) extends JsonValue:
    override def stringify: String = values.map(_.stringify).mkString("[", ",", "]")

  final case class JsonObject(values: Map[String, JsonValue]) extends JsonValue:
    override def stringify: String =
      values
        .map:
            case (key, value) => "\"" + key + "\":" + value.stringify
        .mkString("{", ",", "}")

  val data: JsonObject = JsonObject(Map(
    "user" -> JsonString("Daniel"),
    "posts" -> JsonArray(List(
      JsonString("Scala is awesome!"),
      JsonNumber(42)
    ))
  ))

  // type-class pattern

  // 1 type class definition
  trait JsonConverter[T]:
    def convert(value: T): JsonValue

  // 2 type class instances
  given stringConverter: JsonConverter[String] with
    override def convert(value: String): JsonValue = JsonString(value)

  given intConverter: JsonConverter[Int] with
    override def convert(value: Int): JsonValue = JsonNumber(value)

  given dateConverter: JsonConverter[Date] with
    override def convert(value: Date): JsonValue = JsonString(value.toString)

  given userConverter: JsonConverter[User] with
    override def convert(value: User): JsonValue = JsonObject(Map(
      "name" -> JsonConverter[String].convert(value.name),
      "age" -> JsonConverter[Int].convert(value.age),
      "email" -> JsonConverter[String].convert(value.email)
    ))

  given postConverter: JsonConverter[Post] with
    override def convert(value: Post): JsonValue = JsonObject(Map(
      "content" -> JsonConverter[String].convert(value.content),
      "createdAt" -> JsonConverter[Date].convert(value.createdAt)
    ))

  given feedConverter: JsonConverter[Feed] with
    override def convert(value: Feed): JsonValue = JsonObject(Map(
      "user" -> JsonConverter[User].convert(value.user),
      "posts" -> JsonArray(value.posts.map(JsonConverter[Post].convert))
    ))

  // 3 user-facing api
  object JsonConverter:
    def convert[T](value: T)(using converter: JsonConverter[T]): JsonValue =
      converter.convert(value)

    def apply[T](using instance: JsonConverter[T]): JsonConverter[T] = instance

  // example
  val now: Date = Date(System.currentTimeMillis())
  val john: User = User("John", 34, "john@rockthejvm.com")
  val feed: Feed = Feed(john, List(
    Post("hello, I'm learning type classes", now),
    Post("look at this cute puppy!", now)
  ))

  // 4 extension methods
  object JsonSyntax:
    extension [T](value: T)
      def toIntermediate(using converter: JsonConverter[T]): JsonValue =
        converter.convert(value)
      def toJson(using converter: JsonConverter[T]): String =
        toIntermediate.stringify

  def main(args: Array[String]): Unit =
    // without syntax/extension methods
    println(data.stringify)
    println(JsonConverter.convert(feed).stringify)

    // with extension methods to make API clean
    import JsonSyntax.*
    print(feed.toJson)