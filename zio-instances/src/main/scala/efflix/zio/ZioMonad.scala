package efflix.zio

import efflix.core.Monad
import shapeless.=:!=
import zio.ZIO

/**
  * Instance of [[Monad]] class for ZIO.
  */
private class ZioMonad[R] extends Monad[ZIO[R, +*, +*]] {

  /** @inheritdoc **/
  override def pure[A](value: => A): ZIO[R, Nothing, A] = ZIO.succeed(value)

  /** @inheritdoc **/
  override def flatMap[E1, E2 >: E1, A, B](
    fa: ZIO[R, E1, A]
  )(f: A => ZIO[R, E2, B]): ZIO[R, E2, B] =
    fa.flatMap(f)

  override def tailRecM[E, A, B](init: A)(f: A => ZIO[R, E, Either[A, B]]): ZIO[R, E, B] =
    ZIO.effectSuspendTotal(f(init)).flatMap {
      case Left(next)    => tailRecM(next)(f)
      case Right(result) => ZIO.succeed(result)
    }

  override def foldM[E1, E2, A, B](fa: ZIO[R, E1, A])(
    success: A => ZIO[R, E2, B],
    failure: E1 => ZIO[R, E2, B]
  )(implicit ev: E1 =:!= Nothing): ZIO[R, E2, B] =
    fa.foldM(failure, success)

}
