package efflix.zio

import efflix.effect.Exec
import zio._

import scala.concurrent.{Future, Promise}

/**
  * ZIO executor.
  *
  * @param runtime a ZIO runtime
  */
class ZioEnvExec(runtime: Runtime[ZEnv]) extends Exec[ZIO[ZEnv, *, *]] {

  override def exec[E, A](op: ZIO[ZEnv, E, A]): Future[Either[E, A]] = {
    val promise = Promise[Either[E, A]]()
    runtime.unsafeRunAsync(op) {
      _.fold(
        { cause =>
          cause.failureOption match {
            case Some(e) =>
              promise.success(Left(e))
            case None =>
              val error = cause
                .defects
                .headOption
                .getOrElse(new RuntimeException("ZIO failed"))
              promise.failure(error)
          }
        },
        r => promise.success(Right(r))
      )
    }
    promise.future
  }

  override def execSync[E, A](op: ZIO[zio.ZEnv, E, A]): Either[E, A] = {
    val result = runtime.unsafeRunSync(op)
    val causeMap: Cause[E] => Either[E, A] = (cause: Cause[E]) => {
      if (cause.died) {
        throw cause.defects.head
      } else if (cause.failed) {
        Left(cause.failures.head)
      } else {
        throw new RuntimeException(s"A fiber has been interrupted ${cause.interruptors}")
      }
    }
    result.fold[Either[E, A]](causeMap, Right(_))
  }
}
