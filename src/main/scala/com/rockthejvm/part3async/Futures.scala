package com.rockthejvm.part3async

import java.util.concurrent.Executors
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.concurrent.duration.*
import scala.util.{Failure, Random, Success, Try}

// thread pool (Java-specific)
val executor = Executors.newFixedThreadPool(4)
// thread pool (Scala-specific)
given executionContext: ExecutionContext = ExecutionContext.fromExecutorService(executor)

@main def futures(): Unit =

  // Futures: an async computation that will return at some point
  def demoFuture(): Unit =

    def calcMeaningOfLife(): Int =
      // simulate long computation time
      Thread.sleep(1000)
      42

    val aFuture = Future(calcMeaningOfLife())

    val aFutureInstantResult: Option[Try[Int]] = aFuture.value
    println(aFutureInstantResult)

    // callbacks
    aFuture.onComplete: // evaluated on SOME other thread
      case Success(value) => println(s"I completed the meaning of life: $value")
      case Failure(ex) => println(s"My async computation failed with: $ex")

  // functional composition
  def demoSocial(): Unit =
    case class Profile(id: String, name: String):
      def sendMessage(profile: Profile, msg: String): Unit =
        println(s"[$name] sending message to ${profile.name}: $msg")

    object SocialNetwork:
      // users "database"
      val names: Map[String, String] = Map(
        "rtjvm.id.1-daniel" -> "Daniel",
        "rtjvm.id.2-jane" -> "Jane",
        "rtjvm.id.3-mark" -> "Mark",
      )

      // friends "database"
      val friends: Map[String, String] = Map(
        "rtjvm.id.2-jane" -> "rtjvm.id.3-mark"
      )

      val random: Random = Random()

      // "API"
      def fetchProfile(id: String): Future[Profile] =
        Future:
          Thread.sleep(random.nextInt(300))
          Profile(id, names(id))

      def fetchBestFriend(profile: Profile): Future[Profile] =
        Future:
          Thread.sleep(random.nextInt(300))
          val bestFriendId = friends(profile.id)
          Profile(bestFriendId, names(bestFriendId))

      // problem: send message to best friend
      def sendMessageToBestFriend(id: String, msg: String): Unit =
      // callback hell
      // 1 - call fetchProfile
      // 2 - call fetchBestFriend
      // 3 - call profile.sendMessage(bestFriend, msg)
        SocialNetwork.fetchProfile(id).onComplete:
          case Success(profile) =>
            SocialNetwork.fetchBestFriend(profile).onComplete:
              case Success(friendProfile) =>
                profile.sendMessage(friendProfile, msg)
              case Failure(ex) =>
                ex.printStackTrace()
          case Failure(ex) => ex.printStackTrace()

      def sendMessageToBestFriend_v2(id: String, msg: String): Unit =
        for
          profile <- fetchProfile(id)
          friend <- fetchBestFriend(profile)
        do profile.sendMessage(friend, msg)

      // fallbacks
      val profileNoMatterWhat: Future[Profile] =
        fetchProfile("unknownId").recover:
          case e: Throwable => Profile("rtjvm.id.0-dummy", "Forever alone")
      val aFetchedProfile: Future[Profile] =
        fetchProfile("unknownId").recoverWith:
          case e: Throwable => fetchProfile("rtjvm.id.0-dummy")

      // if both futures fail, the exception from the first is returned
      val fallBackProfile: Future[Profile] =
        fetchProfile("unknownId").fallbackTo(aFetchedProfile)

    SocialNetwork.sendMessageToBestFriend("rtjvm.id.2-jane", "Hello, Mark!")

  /**
   * block for a future
   */
  def demoBlocking(): Unit =
    case class User(name: String)
    case class Transaction(sender: String, receiver: String, amount: Double, status: String)
    object BankingApp:
      // "API"
      def fetchUser(name: String): Future[User] =
        Future:
          Thread.sleep(500)
          User(name)

      def createTransaction(user: User, merchant: String, amount: Double): Future[Transaction] =
        Future:
          Thread.sleep(1)
          Transaction(user.name, merchant, amount, "Success")

      // "external API"
      def purchase(username: String, item: String, merchant: String, price: Double): String =
        // 1 fetch user
        // 2 create transaction
        // 3 WAIT for transaction to finish
        val transactionStatusFuture = for
          user <- fetchUser(username)
          transaction <- createTransaction(user, merchant, price)
        yield transaction.status

        // blocking call
        Await.result(transactionStatusFuture, 2.seconds)
    println("purchasing")
    println(BankingApp.purchase("daniel-234", "shoes", "merchant-987", 3.56))
    println("purchase complete")

  // Promises
  val promise = Promise[Int]()
  val futureInside = promise.future

  def demoPromises(): Unit =
    // thread 1: "consumer" - monitor the future for completion
    futureInside.onComplete:
      case Success(value) => println(s"[consumer] I've just been completed with $value")
      case Failure(ex) => ex.printStackTrace()

    // thread 2: "producer"
    val producerThread = Thread: () =>
      println("[producer] crunching some numbers...")
      Thread.sleep(1000)
      promise.success(42)
      println("[producer] I'm done")

    producerThread.start()

  def exercises(): Unit =
    def immediate[A](value: A): Future[A] = Future(value) // async completion asap
    def immediate_v2[A](value: A): Future[A] = Future.successful(value) // sync completion at time of call

    def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] = first.flatMap(_ => second)

    def first[A](f1: Future[A], f2: Future[A]): Future[A] =
      val promise = Promise[A]()
      f1.onComplete(promise.tryComplete)
      f2.onComplete(promise.tryComplete)
      promise.future

    def last[A](f1: Future[A], f2: Future[A]): Future[A] =
      val bothPromises = Promise[A]()
      val lastPromise = Promise[A]()
      def checkAndComplete(res: Try[A]): Unit =
        if !bothPromises.tryComplete(res) then lastPromise.complete(res)
      f1.onComplete(checkAndComplete)
      f2.onComplete(checkAndComplete)
      lastPromise.future

    def retryUntil[A](action: () => Future[A], pred: A => Boolean): Future[A] =
      action()
        .filter(pred)
        .recoverWith:
          case _ => retryUntil(action, pred)

    lazy val fast = Future:
      Thread.sleep(100)
      1
    lazy val slow = Future:
      Thread.sleep(200)
      2
    first(fast, slow).foreach(result => println(s"First: $result"))
    last(fast, slow).foreach(result => println(s"Last: $result"))

    val random = Random()
    val action = () => Future:
      Thread.sleep(100)
      val nextVal = random.nextInt(100)
      println(s"generated $nextVal")
      nextVal
    val pred = (x: Int) => x < 10
    retryUntil(action, pred).foreach(res => s"Settled at: $res")


//  demoFuture()
//  demoSocial()
//  demoBlocking()
//  demoPromises()
  exercises()
  Thread.sleep(2000)
  executor.shutdown()
