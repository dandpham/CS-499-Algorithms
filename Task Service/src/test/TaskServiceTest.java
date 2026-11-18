/**
 * Name: Dan Pham
 * Class: CS-320
 * Date: 2/14/2025
 * Description: 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;
import TaskService.TaskService;

import org.junit.jupiter.api.Test;

class TaskServiceTest {
	private TaskService taskService;
	
	@Test
	void testAddTask() {
		//Add task
		taskService = new TaskService();
		taskService.addTask("New Task", "Task Description");
		assertEquals(1, taskService.getTasks().size());
	}
	
	@Test
	void testDeleteTask() {
		//Delete task
		taskService = new TaskService();
		taskService.addTask("Delete Task", "Task to be deleted");
		String id = taskService.getTasks().keySet().iterator().next();
		taskService.deleteTask(id);
		assertFalse(taskService.getTasks().containsKey(id));
	}

	@Test 
	void testUpdateName() {
		//Update name
		taskService = new TaskService();
		taskService.addTask("Former Name", "Name to be updated");
		String id = taskService.getTasks().keySet().iterator().next();
		taskService.updateTaskName(id, "Updated Name");
		assertEquals("Updated Name", taskService.getTask(id).getName());
	}
	
	@Test
	void testUpdateDescription() {
		//Update description 
		taskService = new TaskService();
		taskService.addTask("Task", "Former Description");
		String id = taskService.getTasks().keySet().iterator().next();
		taskService.updateTaskDescription(id, "Updated Description");
		assertEquals("Updated Description", taskService.getTask(id).getDescription());
	}
}
