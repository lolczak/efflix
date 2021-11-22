package efflix.core

import efflix.effect.MonadError
import shapeless.=:!=

/**
  * A class for covariant effects containing error channel used to chain
  * computations.
  *
  * @tparam F a effectful context
  */
trait Monad[F[+_, +_]] {

  /**
    * Lifts any value into a context F[Nothing, A].
    *
    * @param value a value to lift
    * @return
    */
  def pure[A](value: => A): F[Nothing, A]

  def unit: F[Nothing, Unit] = pure(())

  /**
    * Chains `fa` with `f` arrow.
    *
    * @param fa an effectful computation
    * @param f dependent effectful function
    * @return a composition of dependent effectful functions
    */
  def flatMap[E1, E2 >: E1, A, B](fa: F[E1, A])(f: A => F[E2, B]): F[E2, B]

  def ifM[E1, E2 >: E1, A](
    fa: F[E1, Boolean]
  )(ifTrue: => F[E2, A], ifFalse: => F[E2, A]): F[E2, A] =
    flatMap[E1, E2, Boolean, A](fa)(if (_) ifTrue else ifFalse)

  def tailRecM[E, A, B](init: A)(f: A => F[E, Either[A, B]]): F[E, B]

  def repeatM[E, A](
    repeat: F[E, A],
    whileTrue: Either[E, A] => Boolean,
    onNextIteration: F[Nothing, Unit] = this.pure(())
  )(implicit ec: MonadError[F]): F[E, A] = {
    sealed trait Phase
    case object Init                          extends Phase
    case class NextIter(result: Either[E, A]) extends Phase

    val iterations =
      tailRecM[Nothing, Phase, Either[E, A]](Init.asInstanceOf[Phase]) {
        case Init =>
          flatMap(MonadError[F].liftToEither(repeat)) { result =>
            pure(Left(NextIter(result)))
          }

        case NextIter(result) =>
          if (whileTrue(result)) {
            flatMap(onNextIteration) { _ =>
              flatMap(MonadError[F].liftToEither(repeat)) { result =>
                pure(Left(NextIter(result)))
              }
            }
          } else {
            pure(Right(result))
          }
      }

    flatMap(iterations) {
      case Left(err)     => MonadError[F].fail(err)
      case Right(result) => pure(result)
    }
  }

  def foldM[E1, E2, A, B](fa: F[E1, A])(success: A => F[E2, B], failure: E1 => F[E2, B])(
    implicit ev: E1 =:!= Nothing
  ): F[E2, B]

}

object Monad {

  def apply[F[+_, +_]](
    implicit covariantFlatMap: Monad[F]
  ): Monad[F] =
    covariantFlatMap

}
