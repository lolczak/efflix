package efflix.effect

import java.util.concurrent

import efflix.core.Monad
import efflix.core.syntax._
import efflix.effect.syntax._

/**
  * Java-backed pure semaphore implementation.
  *
  * @param permits
  * @tparam F a monadic context
  */
class JvmSemaphore[F[+_, +_]: Sync: MonadError: Monad](permits: Int) extends Semaphore[F] {

  private val semaphore = new concurrent.Semaphore(permits, true)

  /** @inheritdoc **/
  override def acquire(): F[Nothing, Unit] = Sync[F].effect(semaphore.acquire())

  /** @inheritdoc **/
  override def release(): F[Nothing, Unit] = Sync[F].effect(semaphore.release())

  /** @inheritdoc **/
  override def withPermit[E, A](criticalSection: F[E, A]): F[E, A] =
    for {
      _      <- acquire()
      result <- criticalSection.onDie(_ => release()).onError(_ => release())
      _      <- release()
    } yield result

}
