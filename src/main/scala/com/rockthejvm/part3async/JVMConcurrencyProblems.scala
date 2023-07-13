package com.rockthejvm.part3async

def runInParallel(): Unit =
  var x = 0

  val thread1 = Thread(() => x = 1)
  val thread2 = Thread(() => x = 2)

  thread1.start()
  thread2.start()
  Thread.sleep(1)
  println(x) // race condition

case class BankAccount(var balance: Int)

def buyItem(acct: BankAccount, item: String, price: Int): Unit =
  acct.balance -= price

def buyItemSafe(acct: BankAccount, item: String, price: Int): Unit =
  acct.synchronized: // does not allow multiple threads to run the critical section
    acct.balance -= price // critical section

def demoBankingProblem(): Unit =
  (1 to 10000).foreach: _ =>
    val acct = BankAccount(50000)
    val thread1 = Thread(() => buyItem(acct, "shoes", 3000))
    val thread2 = Thread(() => buyItem(acct, "iPhone", 4000))
    thread1.start()
    thread2.start()
    thread1.join()
    thread2.join()

    if acct.balance != 43000 then
      println(s"Aha! I've broken the bank: ${acct.balance}")

def threadInception(maxThreads: Int, i: Int = 1): Thread =
  Thread: () =>
    if i < maxThreads then
      val newThread = threadInception(maxThreads, i + 1)
      newThread.start()
      newThread.join()
    println(s"Hello from thread $i")

def minMaxX(): Unit =
  var x = 0
  val threads = (1 to 100).map(_ => Thread(() => x += 1))
  threads.foreach(_.start())
  threads.foreach(_.join())
  println(x)

def demoSleepFallacy(): Unit =
  var message = ""
  val awesomeThread = Thread: () =>
    Thread sleep 1000
    message = "Scala is awesome"

  message = "Scala sucks!"
  awesomeThread.start()
  // Thread.sleep(1001)
  awesomeThread.join()
  println(message)

@main def concurrencyProblems(): Unit =
  // (1 to 10).foreach(_ => runInParallel())
  // demoBankingProblem()
  threadInception(30).start()
  minMaxX()
  demoSleepFallacy()
