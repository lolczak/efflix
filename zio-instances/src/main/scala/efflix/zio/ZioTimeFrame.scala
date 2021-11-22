package efflix.zio

import efflix.effect.TimeFrame
import zio.{ZEnv, ZIO}
import zio.duration.Duration

import scala.concurrent.duration.FiniteDuration

object ZioTimeFrame extends TimeFrame[ZIO[ZEnv, +*, +*]] {

  /**
    * Returns an effect that will timeout `fa` effect.
    *
    * @param fa      an effectful computation
    * @param e       a timeout error
    * @param timeout a timeout
    * @return
    */
  override def failOnTimeout[E, E1 >: E, A](fa: ZIO[ZEnv, E, A])(e: E1)(
    timeout: FiniteDuration
  ): ZIO[ZEnv, E1, A] =
    fa.disconnect.timeoutFail(e)(Duration.fromScala(timeout))

  override def stopOnTimeout[E, A](fa: ZIO[ZEnv, E, A])(
    timeout: FiniteDuration
  ): ZIO[ZEnv, E, Option[A]] =
    fa.disconnect.timeout(Duration.fromScala(timeout))

  override def sleep(duration: FiniteDuration): ZIO[ZEnv, Nothing, Unit] =
    ZIO.sleep(Duration.fromScala(duration))

}
