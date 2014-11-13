package codeine.executer;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public class SerialTasks implements Task {

	
	private List<Task> tasks = Lists.newArrayList();

	public SerialTasks(Task... task) {
		this.tasks.addAll(Arrays.asList(task));
	}

	@Override
	public void run() {
		for (Task task : tasks) {
			task.run();
		}
	}

}
