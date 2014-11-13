package codeine.executer;

import org.junit.Test;
import org.mockito.Mockito;

public class SerialTasksTest {

	@Test
	public void test1() {
		Task task = Mockito.mock(Task.class);
		SerialTasks tested = new SerialTasks(task);
		tested.run();
		Mockito.verify(task).run();
	}
	@Test
	public void test2() {
		Task task = Mockito.mock(Task.class);
		Task task2 = Mockito.mock(Task.class);
		SerialTasks tested = new SerialTasks(task, task2);
		tested.run();
		Mockito.verify(task).run();
	}
	@Test
	public void testNoTasks() {
		SerialTasks tested = new SerialTasks();
		tested.run();
	}

}
