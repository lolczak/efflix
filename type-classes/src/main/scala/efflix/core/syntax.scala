package efflix.core

import efflix.effect.MonadError

object syntax {

  /**
    * Implicit conversion to [[MonadOps]]
    *
    * @param fa an effect
    * @return
    */
  implicit def toMonadOps[F[+_, +_]: Monad, E, A](fa: F[E, A]): MonadOps[F, E, A] =
    new MonadOps[F, E, A](fa)

  implicit def toWithFilterOps[F[+_, +_]: Monad: MonadError, E, A](
    fa: F[E, A]
  ): WithFilterOps[F, E, A] =
    new WithFilterOps[F, E, A](fa)

}
