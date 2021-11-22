package efflix.effect

import efflix.effect.Multitasking.ProcRef

trait Multitasking[F[+_, +_]] {

  def fork[E, A](fa: F[E, A]): F[Nothing, ProcRef[F, E, A]]

  def daemonize[E, A](fa: F[E, A]): F[Nothing, ProcRef[F, E, A]]

}

object Multitasking {

  def apply[F[+_, +_]](implicit instance: Multitasking[F]): Multitasking[F] = instance

  trait ProcRef[F[+_, +_], E, A] {

    def getId(): AnyRef

    def getState(): F[Nothing, ProcState]

    def join(): F[E, A]
  }

  sealed trait ProcState
  case object Done      extends ProcState
  case object Running   extends ProcState
  case object Finishing extends ProcState
  case object Suspended extends ProcState

}
