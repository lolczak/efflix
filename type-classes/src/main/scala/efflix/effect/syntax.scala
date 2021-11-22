package efflix.effect

object syntax {

  /**
    * Implicit conversion to [[MonadErrorOps]]
    *
    * @param fa an effect
    * @return
    */
  implicit def toErrorChannelOps[F[+_, +_]: MonadError, E, A](
    fa: F[E, A]
  ): MonadErrorOps[F, E, A] =
    new MonadErrorOps[F, E, A](fa)

}
