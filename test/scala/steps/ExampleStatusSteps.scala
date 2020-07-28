package scala.steps

import play.api.Play.current
import play.api.libs.ws.WS

import _root_.steps.ExampleApiSteps

class ExampleStatusSteps extends ExampleApiSteps {

  When("""^The the application's status is requested$""") { () =>
    complete {
      WS.url(s"$baseUrl/status").get()
    }
  }

  Then("""^the status is OK$""") { () =>
    response.status mustBe 200
  }
}
