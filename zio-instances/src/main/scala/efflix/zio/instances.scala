package efflix.zio

import efflix.core.Monad
import efflix.effect.{Bracket, MonadError, Multitasking, Sync, TimeFrame}
import zio._

object instances {

  implicit def zioFlatMap[R]: Monad[ZIO[R, +*, +*]] =
    zioFlatMapInstance
      .asInstanceOf[Monad[ZIO[R, +*, +*]]]

  final private[this] val zioFlatMapInstance: Monad[ZIO[Any, +*, +*]] =
    new ZioMonad[Any]

  implicit val zioSync: Sync[ZIO[ZEnv, +*, +*]] = ZioSync

  implicit val zioMultitasking: Multitasking[ZIO[ZEnv, +*, +*]] = ZioMultitasking

  implicit val zioTimeFrame: TimeFrame[ZIO[ZEnv, +*, +*]] = ZioTimeFrame

  implicit val zioBracket: Bracket[ZIO[ZEnv, +*, +*]] = ZioBracket

  implicit def zioAsync[R]: ZioAsync[R] = new ZioAsync[R]

  implicit def zioMonadError[R]: MonadError[ZIO[R, +*, +*]] =
    new ZioMonadError[R]

  implicit def zioExec(implicit runtime: zio.Runtime[ZEnv]): ZioEnvExec =
    new ZioEnvExec(runtime)

}
