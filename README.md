# Request-Level API Example in Akka-Http

- This template shows how to use the Request-Level Client-Side API in Akka-Http using some examples.
- The Request-Level API is the recommended and most convenient way of using Akka HTTP’s client-side functionality. It internally builds upon the Host-Level Client-Side API to provide you with a simple and easy-to-use way of retrieving HTTP responses from remote servers.

Most often, your HTTP client needs are very basic. You need the HTTP response for a certain request and don’t want to bother with setting up a full-blown streaming infrastructure.

For these cases Akka HTTP offers the ```Http().singleRequest(...)``` method, which turns an ```HttpRequest``` instance into ```Future[HttpResponse]```. Internally the request is dispatched across the (cached) host connection pool for the request’s effective URI.

In this template, I've added two examples that will help you to understand the use of Request-Level API:

### [HttpClientSingleRequestExample](src/main/scala/basicexample/HttpClientSingleRequestExample.scala):
It's a very basic example demonstrating the use of ```Http().singleRequest(...)``` method.
### [PaymentRequestUsingRequestLevelAPI](src/main/scala/actorbasedexample/bootstrap/PaymentRequestUsingRequestLevelAPI.scala):
For this example, I've created a simple actor based microservice([PaymentSystem](src/main/scala/actorbasedexample/service/PaymentSystem.scala)) for processing the payment requests. 

This example shows how to send the payment requests(after converting each of them to HttpRequest) to our ```PaymentSystem``` service using the request-level api and get the ```HttpResponse```for each request. 

To check how it works, first run the [PaymentSystem](src/main/scala/actorbasedexample/service/PaymentSystem.scala) app so that the Http server gets started. After that run the [PaymentRequestUsingRequestLevelAPI](src/main/scala/actorbasedexample/bootstrap/PaymentRequestUsingRequestLevelAPI.scala) example app, and then you'll see the ```HttpResponse``` printed on the console for each payment request.

## Prerequisites

- Scala Build Tool(SBT), version 1.6.1
- Scala, version 2.13.8
- Akka Streams, version 2.5.32
- Akka HTTP, version 10.1.10
- Akka HTTP Spray Json, version 10.1.10