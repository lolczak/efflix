package efflix.effect

import shapeless.=:!=

class MonadErrorOps[F[+_, +_]: MonadError, E, A](fa: F[E, A]) {

  def recover[B >: A](recovery: PartialFunction[E, B]): F[E, B] =
    MonadError[F].recover[E, A, B](fa)(recovery)

  def recoverWith[B >: A, E1 >: E](recovery: PartialFunction[E, F[E1, B]]): F[E1, B] =
    MonadError[F].recoverWith[E, A, B, E1](fa)(recovery)

  def recoverFromDeath(recovery: PartialFunction[Throwable, Either[E, A]]): F[E, A] =
    MonadError[F].recoverFromDeath(fa)(recovery)

  def recoverFromDeathWith(recovery: PartialFunction[Throwable, F[E, A]]): F[E, A] =
    MonadError[F].recoverFromDeathWith(fa)(recovery)

  def mapError[E1](f: E => E1)(implicit ev: E =:!= Nothing): F[E1, A] =
    MonadError[F].mapError(fa)(f)

  def flatMapError[E1](f: E => F[Nothing, E1])(implicit ev: E =:!= Nothing): F[E1, A] =
    MonadError[F].flatMapError(fa)(f)

  def onError(cleanUp: PartialFunction[E, F[Nothing, Unit]]): F[E, A] =
    MonadError[F].onError(fa)(cleanUp)

  def cleanUp(cleanUp: E => F[E, Unit]): F[E, A] =
    MonadError[F].cleanUp(fa)(cleanUp)

  def onDie(cleanUp: PartialFunction[Throwable, F[Nothing, Unit]]): F[E, A] =
    MonadError[F].onDie(fa)(cleanUp)

}
