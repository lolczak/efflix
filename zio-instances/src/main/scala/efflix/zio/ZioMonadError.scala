package efflix.zio

import efflix.effect.MonadError
import shapeless.=:!=
import zio.{CanFail, ZIO}

import scala.annotation.unused

/**
  * Instance of [[MonadError]] class for ZIO.
  */
class ZioMonadError[R] extends MonadError[ZIO[R, +*, +*]] {

  implicit private def canFailEv[E](implicit @unused ev: E =:!= Nothing): CanFail[E] = CanFail

  /** @inheritdoc **/
  override def recover[E, A, B >: A](
    fa: ZIO[R, E, A]
  )(recovery: PartialFunction[E, B]): ZIO[R, E, B] =
    recoverWith[E, A, B, E](fa)(recovery.andThen(ZIO.succeed(_)))

  /** @inheritdoc **/
  override def recoverWith[E, A, B >: A, E1 >: E](
    fa: ZIO[R, E, A]
  )(recovery: PartialFunction[E, ZIO[R, E1, B]]): ZIO[R, E1, B] =
    fa.foldM(failure = { error =>
      if (recovery.isDefinedAt(error)) recovery(error)
      else ZIO.fail(error)
    }, success = ZIO.succeed(_))

  /** @inheritdoc **/
  override def liftEither[E, A](either: Either[E, A]): ZIO[R, E, A] = ZIO.fromEither(either)

  override def liftToEither[E, A](fa: ZIO[R, E, A])(
    implicit ev: E =:!= Nothing
  ): ZIO[R, Nothing, Either[E, A]] = fa.either

  /** @inheritdoc **/
  override def mapError[E, A, E1](
    fa: ZIO[R, E, A]
  )(f: E => E1)(implicit ev: E =:!= Nothing): ZIO[R, E1, A] =
    fa.mapError(f)

  /** @inheritdoc **/
  override def flatMapError[E, A, E1](fa: ZIO[R, E, A])(f: E => ZIO[R, Nothing, E1])(
    implicit ev: E =:!= Nothing
  ): ZIO[R, E1, A] = fa.flatMapError(f)

  /** @inheritdoc **/
  override def fail[E](error: => E): ZIO[R, E, Nothing] = ZIO.fail(error)

  override def die(throwable: Throwable): ZIO[R, Nothing, Nothing] = ZIO.die(throwable)

  /** @inheritdoc **/
  override def onError[E, A](
    fa: ZIO[R, E, A]
  )(cleanUp: PartialFunction[E, ZIO[R, Nothing, Unit]]): ZIO[R, E, A] =
    fa.onError { cause =>
      if (cause.failed) {
        val failure = cause.failureOption.get
        if (cleanUp.isDefinedAt(failure)) cleanUp(failure)
        else ZIO.unit
      } else {
        ZIO.unit
      }
    }

  override def cleanUp[E, A](fa: ZIO[R, E, A])(cleanUp: E => ZIO[R, E, Unit]): ZIO[R, E, A] =
    fa.foldM(
      failure = { failure =>
        recoverWith(cleanUp(failure)) { _ =>
          ZIO.fail(failure)
        }.flatMap(_ => ZIO.fail(failure))
      },
      success = (result) => ZIO.succeed(result)
    )

  /** @inheritdoc **/
  override def onDie[E, A](
    fa: ZIO[R, E, A]
  )(cleanUp: PartialFunction[Throwable, ZIO[R, Nothing, Unit]]): ZIO[R, E, A] =
    fa.onError { cause =>
      if (cause.died) {
        val throwable = cause.dieOption.get
        if (cleanUp.isDefinedAt(throwable)) cleanUp(throwable)
        else ZIO.unit
      } else {
        ZIO.unit
      }
    }

  /**
    * Recovers from a death of a fiber.
    *
    * @param fa
    * @param recovery
    * @tparam E
    * @tparam A
    * @return
    */
  override def recoverFromDeathWith[E, A](fa: ZIO[R, E, A])(
    recovery: PartialFunction[Throwable, ZIO[R, E, A]]
  ): ZIO[R, E, A] =
    fa.foldCauseM(
      failure = { cause =>
        if (cause.died && recovery.isDefinedAt(cause.dieOption.get)) {
          recovery(cause.dieOption.get)
        } else {
          cause.failureOrCause match {
            case Left(failure)   => ZIO.fail(failure)
            case Right(causeDuo) => ZIO.die(causeDuo.dieOption.get)
          }
        }
      },
      success = { value =>
        ZIO.succeed(value)
      }
    )

  /**
    * Recovers from a death of a fiber.
    *
    * @param fa
    * @param recovery
    * @tparam E
    * @tparam A
    * @return
    */
  override def recoverFromDeath[E, A](fa: ZIO[R, E, A])(
    recovery: PartialFunction[Throwable, Either[E, A]]
  ): ZIO[R, E, A] =
    recoverFromDeathWith[E, A](fa) { th: Throwable =>
      if (recovery.isDefinedAt(th)) {
        recovery(th) match {
          case Left(failure) => ZIO.fail(failure)
          case Right(result) => ZIO.succeed(result)
        }
      } else {
        ZIO.die(th)
      }
    }
}
