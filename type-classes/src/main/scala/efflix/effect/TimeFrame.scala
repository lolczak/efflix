package efflix.effect

import scala.concurrent.duration.FiniteDuration

trait TimeFrame[F[+_, +_]] {

  /**
    * Returns an effect that will timeout `fa` effect.
    *
    * @param fa an effectful computation
    * @param e a timeout error
    * @param timeout a timeout
    * @return
    */
  def failOnTimeout[E, E1 >: E, A](fa: F[E, A])(e: E1)(
    timeout: FiniteDuration
  ): F[E1, A]

  def stopOnTimeout[E, A](fa: F[E, A])(timeout: FiniteDuration): F[E, Option[A]]

  def sleep(duration: FiniteDuration): F[Nothing, Unit]

}

object TimeFrame {

  def apply[F[+_, +_]](implicit timeFrame: TimeFrame[F]): TimeFrame[F] = timeFrame

}
