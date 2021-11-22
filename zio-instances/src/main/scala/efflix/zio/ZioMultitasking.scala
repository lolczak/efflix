package efflix.zio

import efflix.effect.Multitasking
import zio.Fiber.Status.{Done, Finishing, Running, Suspended}
import zio.{Fiber, ZEnv, ZIO}

object ZioMultitasking extends Multitasking[ZIO[ZEnv, +*, +*]] {

  override def fork[E, A](fa: ZIO[ZEnv, E, A]): ZIO[ZEnv, Nothing, ZioProcRef[E, A]] =
    fa.fork.map(fiber => ZioProcRef(fiber))

  override def daemonize[E, A](
    fa: ZIO[ZEnv, E, A]
  ): ZIO[ZEnv, Nothing, Multitasking.ProcRef[ZIO[ZEnv, +*, +*], E, A]] =
    fa.forkDaemon.map(fiber => ZioProcRef(fiber))

  case class ZioProcRef[E, A](fiber: Fiber.Runtime[E, A])
      extends Multitasking.ProcRef[ZIO[ZEnv, +*, +*], E, A] {

    override def getId(): AnyRef = fiber.id

    override def getState(): ZIO[Any, Nothing, Multitasking.ProcState] =
      fiber.status.map {
        case Done                     => Multitasking.Done
        case Finishing(_)             => Multitasking.Finishing
        case Running(_)               => Multitasking.Running
        case Suspended(_, _, _, _, _) => Multitasking.Suspended
      }

    override def join(): ZIO[ZEnv, E, A] = fiber.join

  }

}
