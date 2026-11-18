/**
 * Name: Dan Pham
 * Class: CS-320
 * Date: 2/14/2025
 * Description: 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import TaskService.Task;

class TaskTest {

	@Test
	void testTaskCreation() {
		//Creates a new task with valid data
		Task task = new Task("1234567890", "Test Task Name", "Insert test description here.");
		assertEquals("1234567890", task.getTaskId());
		assertEquals("Test Task Name", task.getName());
		assertEquals("Insert test description here.", task.getDescription());
	}

	@Test
	void testInvalidTaskId() {
		//Creates new task with invalid task id
		assertThrows(IllegalArgumentException.class, () -> new Task(null, "Test", "Description")); //Null task name
		assertThrows(IllegalArgumentException.class, () -> new Task("12345678900", "Test", "Description")); //Exceeds 10 character max
	}
	
	@Test
	void testInvalidName() {
		//Creates new task with invalid name
		assertThrows(IllegalArgumentException.class, () -> new Task("1234567890", null, "Description")); //Null name
		assertThrows(IllegalArgumentException.class, () -> new Task("1234567890", "Really Really Really Long Name", "Description")); //Name exceeds 20 character max
	}
	
	@Test
	void testInvalidDescription() {
		//Creates new task with invalid description 
		assertThrows(IllegalArgumentException.class, () -> new Task("1234567890", "Test", null)); //Null description
		assertThrows(IllegalArgumentException.class, () -> new Task("1234567890", "Test", "Really Really Really Really Really Long Description")); //Description exeeds 50 character max
	}
}
