package efflix.zio

import efflix.effect.Bracket
import zio.{ZEnv, ZIO}

object ZioBracket extends Bracket[ZIO[ZEnv, +*, +*]] {

  override def bracket[E, A, B](
    acquire: ZIO[ZEnv, E, A],
    use: A => ZIO[ZEnv, E, B],
    release: A => ZIO[ZEnv, Nothing, Unit]
  ): ZIO[ZEnv, E, B] =
    ZIO.bracket(
      acquire = acquire,
      release = release,
      use     = use
    )

}
