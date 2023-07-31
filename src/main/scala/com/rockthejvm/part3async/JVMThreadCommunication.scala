package com.rockthejvm.part3async

import scala.collection.mutable
import scala.util.Random

// example: the producer-consumer problem
class SimpleContainer:
  private var value: Int = 0

  def isEmpty: Boolean = value == 0

  def set(newVal: Int): Unit = value = newVal

  def get: Int =
    val result = value
    value = 0
    result
end SimpleContainer

// part 1: 1 producer, 1 consumer
object ProdConsV1:
  def start(): Unit =
    val container = SimpleContainer()

    val consumer = Thread: () =>
      println("[consumer] waiting...")

      // busy waiting: a poor implementation
      // blocks thread until while loop broken
      while container.isEmpty
      do println("[consumer] waiting for a value...")

      println(s"[consumer] I have consumed a value: ${container.get}")

    val producer = Thread: () =>
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println(s"[producer] I am producing after long work, the value: $value")
      container.set(value)

    consumer.start()
    producer.start()

end ProdConsV1

// wait + notify
object ProdConsV2:
  def start(): Unit =
    val container = SimpleContainer()

    val consumer = Thread: () =>
      println("[consumer] waiting...")

      container.synchronized: // block all other threads trying to "lock" this object
        // thread safe code
        if container.isEmpty
        then container.wait() // release lock & suspend thread
        // reacquire lock & continue execution

      println(s"[consumer] I have consumed a value: ${container.get}")

    val producer = Thread: () =>
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42

      container.synchronized:
        println(s"[producer] I am producing after LONG work, value: $value")
        container.set(value)
        container.notify() // awaken ONE suspended thread on this object

    consumer.start()
    producer.start()

end ProdConsV2

// insert a larger container
// producer -> [ _ _ _ ] -> consumer
object ProdConsV3:
  def start(capacity: Int, producerSpeed: Int = 500, consumerSpeed: Int = 500): Unit =
    val buffer = mutable.Queue[Int]()

    val consumer = Thread: () =>
      val random = Random(System.nanoTime())

      while true do
        buffer.synchronized:
          if buffer.isEmpty then
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          // buffer must not be empty
          val x = buffer.dequeue()
          println(s"[consumer] I've just consumed: $x")
          buffer.notify() // wake up producer if asleep

        Thread.sleep(random.nextInt(consumerSpeed))

    val producer = Thread: () =>
      val random = Random(System.nanoTime())
      var counter = 0

      while true do
        buffer.synchronized:
          if buffer.size == capacity then
            println("[producer] buffer full, waiting...")
            buffer.wait()
          // buffer is not empty
          val newElem = counter
          counter += 1
          println(s"[producer] I'm producing $newElem")
          buffer.enqueue(newElem)
          buffer.notify() // wakes up the consumer if sleeping

        Thread.sleep(random.nextInt(producerSpeed))

    consumer.start()
    producer.start()

end ProdConsV3

// multiple producers, multiple consumers
object ProdConsV4:

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread:
    override def run(): Unit =
      val random = new Random(System.nanoTime())

      while true do
        buffer.synchronized:
          while buffer.isEmpty do
            println(s"[consumer $id] buffer empty, waiting...")
            buffer.wait()

          val newValue = buffer.dequeue()
          println(s"[consumer $id] consumed $newValue")
          buffer.notifyAll()

        Thread.sleep(random.nextInt(500))

  end Consumer

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread:
    override def run(): Unit =
      val random = new Random(System.nanoTime())
      var currentCount = 0

      while true do
        buffer.synchronized:
          while buffer.size == capacity do // buffer full
            println(s"[producer $id] buffer is full, waiting...")
            buffer.wait()

          println(s"[producer $id] producing $currentCount")
          buffer.enqueue(currentCount)
          buffer.notifyAll()
          currentCount += 1

        Thread.sleep(random.nextInt(500))

  end Producer

  def start(nProducers: Int, nConsumers: Int, containerCapacity: Int): Unit =
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val producers = (1 to nProducers).map(id => new Producer(id, buffer, containerCapacity))
    val consumers = (1 to nConsumers).map(id => new Consumer(id, buffer))

    producers.foreach(_.start())
    consumers.foreach(_.start())


@main def jvmThreadCommunication(): Unit =
  // ProdConsV1.start()
  // ProdConsV2.start()
  // ProdConsV3.start(4)
  ProdConsV4.start(3, 3, 5)
