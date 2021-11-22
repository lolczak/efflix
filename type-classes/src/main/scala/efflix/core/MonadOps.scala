package efflix.core

import shapeless.=:!=

class MonadOps[F[+_, +_]: Monad, E, A](fa: F[E, A]) {

  def flatMap[E1 >: E, B](f: A => F[E1, B]): F[E1, B] =
    Monad[F].flatMap[E, E1, A, B](fa)(f)

  def map[B](f: A => B): F[E, B] = flatMap(a => Monad[F].pure(f(a)))

  def >>=[E1 >: E, B](f: A => F[E1, B]): F[E1, B] = flatMap(f)

  def *>[E1 >: E, B](f: F[E1, B]): F[E1, B] = flatMap(_ => f)

  def >>[E1 >: E, B](f: F[E1, B]): F[E1, B] = flatMap(_ => f)

  def foldM[E2, B](success: A => F[E2, B], failure: E => F[E2, B])(
    implicit ev: E =:!= Nothing
  ): F[E2, B] =
    Monad[F].foldM(fa)(success, failure)

}
