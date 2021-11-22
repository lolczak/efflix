package efflix.core

import efflix.effect.MonadError

class WithFilterOps[F[+_, +_]: Monad: MonadError, E, A](fa: F[E, A]) {

  def withFilter(predicate: A => Boolean): F[E, A] =
    Monad[F].flatMap(fa) { value =>
      if (predicate(value)) {
        Monad[F].pure(value)
      } else {
        MonadError[F].die(new NoSuchElementException("Predicate does not hold for " + value))
      }
    }

}
