package efflix.effect

trait Bracket[F[+_, +_]] {

  def bracket[E, A, B](acquire: F[E, A], use: A => F[E, B], release: A => F[Nothing, Unit]): F[E, B]

}

object Bracket {

  def apply[F[+_, +_]](implicit bracket: Bracket[F]): Bracket[F] = bracket

}
