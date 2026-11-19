/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: JUnit test cases for the Task class
 */
package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import TaskService.Task;
import TaskService.Task.Priority;

class TaskTest {

	private static final LocalDate DUE = LocalDate.of(2026, 12, 15);

	private Task validTask() {
		return new Task("1234567890", "Test Task Name", "Insert test description here.", Priority.MEDIUM, DUE);
	}

	@Test
	void testTaskCreation() {
		Task task = validTask();
		assertEquals("1234567890", task.getTaskId());
		assertEquals("Test Task Name", task.getName());
		assertEquals("Insert test description here.", task.getDescription());
		assertEquals(Priority.MEDIUM, task.getPriority());
		assertEquals(DUE, task.getDueDate());
	}

	@Test
	void testMaxLengthValuesAllowed() {
		// Boundary: 10-character ID, 20-character name, 50-character description
		assertDoesNotThrow(() -> new Task("1234567890", "12345678901234567890",
				"12345678901234567890123456789012345678901234567890", Priority.LOW, DUE));
	}

	@Test
	void testInvalidTaskId() {
		assertThrows(IllegalArgumentException.class, () -> new Task(null, "Test", "Description", Priority.LOW, DUE));
		assertThrows(IllegalArgumentException.class, () -> new Task("12345678900", "Test", "Description", Priority.LOW, DUE));
	}

	@Test
	void testInvalidName() {
		assertThrows(IllegalArgumentException.class, () -> new Task("1", null, "Description", Priority.LOW, DUE));
		assertThrows(IllegalArgumentException.class,
				() -> new Task("1", "Really Really Really Long Name", "Description", Priority.LOW, DUE));
	}

	@Test
	void testInvalidDescription() {
		assertThrows(IllegalArgumentException.class, () -> new Task("1", "Test", null, Priority.LOW, DUE));
		assertThrows(IllegalArgumentException.class, () -> new Task("1", "Test",
				"Really Really Really Really Really Long Description", Priority.LOW, DUE));
	}

	@Test
	void testInvalidPriority() {
		assertThrows(IllegalArgumentException.class, () -> new Task("1", "Test", "Description", null, DUE));
	}

	@Test
	void testInvalidDueDate() {
		assertThrows(IllegalArgumentException.class, () -> new Task("1", "Test", "Description", Priority.LOW, null));
	}

	@Test
	void testSetName() {
		Task task = validTask();
		task.setName("New Name");
		assertEquals("New Name", task.getName());
		assertThrows(IllegalArgumentException.class, () -> task.setName(null));
		assertThrows(IllegalArgumentException.class, () -> task.setName("Really Really Really Long Name"));
	}

	@Test
	void testSetNameErrorMessage() {
		// The original setName() reported a Task ID error by mistake
		IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
				() -> validTask().setName(null));
		assertEquals("Name cannot be null or exceed 20 characters.", error.getMessage());
	}

	@Test
	void testSetDescription() {
		Task task = validTask();
		task.setDescription("New description");
		assertEquals("New description", task.getDescription());
		assertThrows(IllegalArgumentException.class, () -> task.setDescription(null));
		assertThrows(IllegalArgumentException.class,
				() -> task.setDescription("Really Really Really Really Really Long Description"));
	}
}