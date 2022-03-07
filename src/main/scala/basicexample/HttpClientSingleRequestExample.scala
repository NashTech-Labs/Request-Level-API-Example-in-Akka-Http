package basicexample

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.{Failure, Success}

object HttpClientSingleRequestExample extends App {
  implicit val system: ActorSystem = ActorSystem("HttpClientSingleRequestExample")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // required for the future onComplete in the end
  implicit val executionContext = system.dispatcher

  val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://www.google.com"))

  responseFuture.onComplete {
    case Success(response) =>
      response.discardEntityBytes() // very important
      println(s"The request was successful and returned: $response")
    case Failure(exception) =>
      println(s"The request failed with: $exception")
  }
}
