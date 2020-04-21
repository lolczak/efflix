package efflix.zio

import efflix.core.CovariantFlatMap
import efflix.effect.{ErrorChannel, Sync}
import zio._

object instances {

  implicit def zioFlatMap[R]: CovariantFlatMap[ZIO[R, +*, +*]] =
    zioFlatMapInstance
      .asInstanceOf[CovariantFlatMap[ZIO[R, +*, +*]]]

  final private[this] val zioFlatMapInstance: CovariantFlatMap[ZIO[Any, +*, +*]] =
    new ZioCovariantFlatMap[Any]

  implicit val zioSync: Sync[ZIO[ZEnv, +*, +*]] = ZioSync

  implicit def zioAsync[R]: ZioAsync[R] = new ZioAsync[R]

  implicit def zioExcept[R]: ErrorChannel[ZIO[R, +*, +*]] =
    new ZioErrorChannel[R]

  implicit def zioExec(implicit runtime: zio.Runtime[ZEnv]): ZioEnvExec =
    new ZioEnvExec(runtime)

}
