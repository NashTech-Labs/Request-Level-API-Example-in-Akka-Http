package actorbasedexample.bootstrap

import actorbasedexample.service.{CreditCard, PaymentJsonProtocol}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import spray.json._


object PaymentRequestUsingRequestLevelAPI extends App with PaymentJsonProtocol {

  implicit val system: ActorSystem = ActorSystem("RequestLevelAPIExample")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import actorbasedexample.service.PaymentSystemDomain._

  val creditCards = List(
    CreditCard("1745-4548-5133-1221", "125", "Prateek"),
    CreditCard("4527-8748-5183-1221", "103", "Aman"),
    CreditCard("0000-0000-0000-0000", "321", "Rohit")
  )

  val paymentRequests = creditCards.map(creditCard => PaymentRequest(creditCard, "Prakhar", 5000))
  val serverHttpRequests = paymentRequests.map(paymentRequest =>
    HttpRequest(
      HttpMethods.POST,
      uri = "http://localhost:8081/api/payments",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        paymentRequest.toJson.prettyPrint
      )
    )
  )

  Source(serverHttpRequests)
    .mapAsync(10)(request => Http().singleRequest(request))
    .runForeach(println)

}
