package net.westaystay

import play.api.Configuration

import java.time.{Duration, Instant}
import javax.inject.Inject
import scala.collection.immutable.Vector

class SlidingWindowRateLimiter @Inject()(config: Configuration) {
  @volatile private var requests: Vector[Instant] = Vector.empty[Instant]
  val maxRequestsPerMinute = config.get[Int]("maxRequestsPerMinute")
  private val Minute = 60

  def isRequestAllowed: Boolean = {
    val now = Instant.now()
    val oldRequests = requests.dropWhile(i => Duration.between(i, now).getSeconds > Minute)
    if (oldRequests.length < maxRequestsPerMinute) {
      requests = oldRequests.appended(now)
      true
    } else {
      requests = oldRequests
      false
    }
  }
}
