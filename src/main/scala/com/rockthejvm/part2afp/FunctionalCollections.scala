package com.rockthejvm.part2afp

@main
def functionalCollections(): Unit =
  // Sets are functions A => Boolean
  val aSet = Set("I", "love", "Scala")
  val setContainsScala = aSet("Scala") // true

  // Sequences are partial functions Int => A
  val aSeq = Seq(1, 2, 3, 4)
  val anElement = aSeq(2) // 3
  // val aNonExistentElement = aSeq(100) // throws OOBException

  // Maps are partial functions A => B
  val phoneBook = Map(
    "Alice" -> 123456,
    "Bob" -> 987654
  )
  val aliceNumber = phoneBook("Alice")
  // val danielPhone = phoneBook("Daniel") // throws NoSuchElementException