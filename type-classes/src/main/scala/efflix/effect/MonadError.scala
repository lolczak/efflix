package efflix.effect

import efflix.core.Monad
import shapeless.=:!=

import scala.util.control.NonFatal

/**
  * A class for effectful computations which may fail.
  *
  * @tparam F a monadic context
  */
trait MonadError[F[+_, +_]] {

  /**
    * Recovers from error matched by recovery function.
    *
    * @param fa an effectful computation
    * @param recovery a recovery function
    * @return
    */
  def recover[E, A, B >: A](fa: F[E, A])(recovery: PartialFunction[E, B]): F[E, B]

  /**
    * Recover from error by executing effectful computation.
    *
    * @param fa an effectful computation
    * @param recovery a recovery function
    * @return
    */
  def recoverWith[E, A, B >: A, E1 >: E](fa: F[E, A])(
    recovery: PartialFunction[E, F[E1, B]]
  ): F[E1, B]

  /**
    * Lifts either to the [[F]] monadic context
    *
    * @param either the either to lift
    * @return
    */
  def liftEither[E, A](either: Either[E, A]): F[E, A]

  def liftToEither[E, A](fa: F[E, A])(implicit ev: E =:!= Nothing): F[Nothing, Either[E, A]]

  /**
    * Maps error using the specified function.
    *
    * @param fa an effectful computation
    * @param f a mapping function
    * @return
    */
  def mapError[E, A, E1](fa: F[E, A])(f: E => E1)(implicit ev: E =:!= Nothing): F[E1, A]

  /**
    * FlatMaps error using the specified function.
    *
    * @param fa an effectful computation
    * @param f a mapping function
    * @return
    */
  def flatMapError[E, A, E1](fa: F[E, A])(f: E => F[Nothing, E1])(
    implicit ev: E =:!= Nothing
  ): F[E1, A]

  /**
    * Returns an effect that models failure with the specified error.
    *
    * @param error a returned error
    * @return
    */
  def fail[E](error: => E): F[E, Nothing]

  def die(throwable: Throwable): F[Nothing, Nothing]

  /**
    * Runs `cleanUp` effect if error is matched by partial function.
    *
    * @param fa an effectful computation
    * @param cleanUp a cleanup function
    * @return
    */
  def onError[E, A](fa: F[E, A])(cleanUp: PartialFunction[E, F[Nothing, Unit]]): F[E, A]

  def cleanUp[E, A](fa: F[E, A])(cleanUp: E => F[E, Unit]): F[E, A]

  /**
    * Runs `cleanUp` effect if effectful computation died due to exception.
    *
    * @param fa an effectful computation
    * @param cleanUp a cleanup function
    * @return
    */
  def onDie[E, A](fa: F[E, A])(cleanUp: PartialFunction[Throwable, F[Nothing, Unit]]): F[E, A]

  /**
    * Recovers from a death of a fiber.
    *
    * @param fa
    * @param recovery
    * @tparam E
    * @tparam A
    * @return
    */
  def recoverFromDeath[E, A](fa: F[E, A])(
    recovery: PartialFunction[Throwable, Either[E, A]]
  ): F[E, A]

  def recoverFromDeathWith[E, A](fa: F[E, A])(
    recovery: PartialFunction[Throwable, F[E, A]]
  ): F[E, A]

  def catchNonFatal[A](f: => A)(implicit M: Monad[F]): F[Throwable, A] =
    try {
      M.pure(f)
    } catch {
      case NonFatal(th) => fail(th)
    }

}

object MonadError {

  def apply[F[+_, +_]](implicit except: MonadError[F]): MonadError[F] = except

}
