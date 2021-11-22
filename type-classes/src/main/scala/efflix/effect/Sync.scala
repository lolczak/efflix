package efflix.effect

import java.io.IOException

/**
  * A class for synchronous effects that block threads.
  *
  * @tparam F an effectful context
  */
trait Sync[F[+_, +_]] {

  /**
    * Lifts a total synchronous effect into a pure `F` context.
    *
    * @param effect an effect
    * @return
    */
  def effect[A](effect: => A): F[Nothing, A]

  /**
    * Lifts a blocking operation into a pure `F` context.
    *
    * @param effect an effect
    * @return
    */
  def blockingOp[A](effect: => A): F[Throwable, A]

  /**
    * Lifts a blocking IO operation into a pure `F` context.
    *
    * @param effect an effect
    * @return
    */
  def blockingIO[A](effect: => A): F[IOException, A]

}

object Sync {

  def apply[F[+_, +_]](implicit sync: Sync[F]): Sync[F] = sync

}
