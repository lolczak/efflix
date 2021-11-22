package efflix.zio

import java.io.IOException

import efflix.effect.Sync
import zio.{blocking, _}

/**
  * Instance of [[Sync]] class for ZIO.
  */
object ZioSync extends Sync[ZIO[ZEnv, +*, +*]] {

  /** @inheritdoc **/
  override def effect[A](effect: => A): ZIO[ZEnv, Nothing, A] = ZIO.effectTotal(effect)

  /** @inheritdoc **/
  override def blockingOp[A](effect: => A): ZIO[ZEnv, Throwable, A] =
    blocking.effectBlocking(effect)

  /** @inheritdoc **/
  override def blockingIO[A](effect: => A): ZIO[ZEnv, IOException, A] =
    blocking.effectBlockingIO(effect)

}
