package efflix.effect

import java.util.concurrent.Executor

private[effect] object ImmediateExecutor extends Executor {
  override def execute(command: Runnable): Unit = command.run()
}
