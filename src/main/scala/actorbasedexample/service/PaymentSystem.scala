package actorbasedexample.service

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json._

import scala.concurrent.duration._
import scala.language.postfixOps

case class CreditCard(serialNumber: String, securityCode: String, accountHolderName: String)

object PaymentSystemDomain {
  case class PaymentRequest(creditCard: CreditCard, receiver: String, amount: Double)

  case object PaymentRequestAccepted

  case object PaymentRequestRejected
}

trait PaymentJsonProtocol extends DefaultJsonProtocol {
  implicit val creditCardFormat = jsonFormat3(CreditCard)
  implicit val paymentRequestFormat = jsonFormat3(PaymentSystemDomain.PaymentRequest)
}

class PaymentValidator extends Actor with ActorLogging {

  import PaymentSystemDomain._

  override def receive: Receive = {
    case PaymentRequest(CreditCard(serialNumber, _, accountHolderName), receiver, amount) =>
      log.info(s"$accountHolderName has received a payment request for INR $amount")
      if (serialNumber == "0000-0000-0000-0000") {
        log.info(s"Payment request rejected due to invalid card details!")
        sender() ! PaymentRequestRejected
      }
      else {
        log.info(s"Payment request accepted!")
        sender() ! PaymentRequestAccepted
      }
  }
}

// a simple microservice for processing payment requests
object PaymentSystem extends App with PaymentJsonProtocol with SprayJsonSupport {

  implicit val system: ActorSystem = ActorSystem("PaymentSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import PaymentSystemDomain._
  import system.dispatcher

  val paymentValidator = system.actorOf(Props[PaymentValidator], "paymentValidator")
  implicit val timeout: Timeout = Timeout(2 seconds)

  val paymentRoute =
    path("api" / "payments") {
      post {
        entity(as[PaymentRequest]) { paymentRequest =>
          val validationResponseFuture = (paymentValidator ? paymentRequest).map {
            case PaymentRequestRejected => StatusCodes.Forbidden
            case PaymentRequestAccepted => StatusCodes.OK
            case _ => StatusCodes.BadRequest
          }

          complete(validationResponseFuture)
        }
      }
    }

  Http().bindAndHandle(paymentRoute, "localhost", 8081)

}
