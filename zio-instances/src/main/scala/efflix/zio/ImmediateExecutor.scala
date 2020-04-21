package efflix.zio

import java.util.concurrent.Executor

private object ImmediateExecutor extends Executor {
  override def execute(command: Runnable): Unit = command.run()
}
