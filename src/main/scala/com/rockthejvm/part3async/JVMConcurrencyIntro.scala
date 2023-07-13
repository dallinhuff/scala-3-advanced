package com.rockthejvm.part3async

import java.util.concurrent.Executors

def basicThreads(): Unit =
  val runnable = new Runnable:
    override def run(): Unit =
      println("waiting...")
      Thread.sleep(500)
      println("Running on some thread")

  // threads on the JVM
  val aThread = new Thread(runnable)

  aThread.start() // will run runnable on a thread
  // JVM thread == OS thread
  aThread.join() // block until thread finishes

// order of operations is NOT guaranteed
def orderOfExecution(): Unit =
  val threadHello = Thread: () =>
    (1 to 10).foreach(_ => println("hello"))
  val threadGoodbye = Thread: () =>
    (1 to 10).foreach(_ => println("goodbye"))
  threadHello.start()
  threadGoodbye.start()

// executors
def demoExecutors(): Unit =
  val threadPool = Executors.newFixedThreadPool(4)

  // submit a computation
  threadPool.execute(() => println("Something in the thread pool"))

  threadPool.execute: () =>
    Thread.sleep(1000)
    println("done after one second")

  threadPool.execute: () =>
    Thread.sleep(1000)
    println("almost done")
    Thread.sleep(1000)
    println("done after two seconds")

  threadPool.shutdown()
  // threadPool.execute(() => println("This should NOT appear")) // throws exception

@main def concurrencyIntro(): Unit =
  // basicThreads()
  // orderOfExecution()
  demoExecutors()
