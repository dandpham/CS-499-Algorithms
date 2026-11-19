/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: JUnit test cases for TaskService, including priority ordering
 * and keeping the priority order in sync when tasks change
 */
package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import TaskService.Task;
import TaskService.Task.Priority;
import TaskService.TaskService;

class TaskServiceTest {

	private static final LocalDate DAY_1 = LocalDate.of(2026, 12, 1);
	private static final LocalDate DAY_2 = LocalDate.of(2026, 12, 2);
	private static final LocalDate DAY_3 = LocalDate.of(2026, 12, 3);

	private TaskService service;

	@BeforeEach
	void setUp() {
		service = new TaskService();
	}

	/** Helper: a valid task with the given ID, priority, and due date */
	private Task task(String id, Priority priority, LocalDate dueDate) {
		return new Task(id, "Task " + id, "Description", priority, dueDate);
	}

	// ===== Add, get, and delete =====

	@Test
	void testAddTaskGeneratesId() {
		String id = service.addTask("New Task", "Task Description", Priority.MEDIUM, DAY_1);
		assertNotNull(id);
		assertTrue(id.length() <= Task.MAX_ID_LENGTH);
		assertEquals("New Task", service.getTask(id).getName());
		assertEquals(1, service.size());
	}

	@Test
	void testGeneratedIdsAreUnique() {
		for (int i = 0; i < 1000; i++) {
			service.addTask("Task", "Description", Priority.LOW, DAY_1);
		}
		assertEquals(1000, service.size());
	}

	@Test
	void testAddExistingTask() {
		Task task = task("1", Priority.HIGH, DAY_1);
		service.addTask(task);
		assertSame(task, service.getTask("1"));
	}

	@Test
	void testAddNullTask() {
		assertThrows(IllegalArgumentException.class, () -> service.addTask((Task) null));
	}

	@Test
	void testAddDuplicateId() {
		service.addTask(task("1", Priority.HIGH, DAY_1));
		assertThrows(IllegalArgumentException.class, () -> service.addTask(task("1", Priority.LOW, DAY_2)));
	}

	@Test
	void testAddInvalidTaskAddsNothing() {
		assertThrows(IllegalArgumentException.class, () -> service.addTask(null, "Description", Priority.LOW, DAY_1));
		assertEquals(0, service.size());
	}

	@Test
	void testDeleteTask() {
		String id = service.addTask("Delete Task", "Task to be deleted", Priority.LOW, DAY_1);
		service.deleteTask(id);
		assertFalse(service.getTasks().containsKey(id));
		assertNull(service.getNextTask()); // Removed from the TreeSet too
	}

	@Test
	void testDeleteMissingTask() {
		assertThrows(IllegalArgumentException.class, () -> service.deleteTask("missing"));
	}

	@Test
	void testGetTasksIsReadOnly() {
		// The original returned the internal map, so callers could bypass the service
		service.addTask(task("1", Priority.LOW, DAY_1));
		assertThrows(UnsupportedOperationException.class, () -> service.getTasks().clear());
		assertEquals(1, service.size());
	}

	// ===== Update =====

	@Test
	void testUpdateName() {
		String id = service.addTask("Former Name", "Name to be updated", Priority.LOW, DAY_1);
		service.updateTaskName(id, "Updated Name");
		assertEquals("Updated Name", service.getTask(id).getName());
	}

	@Test
	void testUpdateDescription() {
		String id = service.addTask("Task", "Former Description", Priority.LOW, DAY_1);
		service.updateTaskDescription(id, "Updated Description");
		assertEquals("Updated Description", service.getTask(id).getDescription());
	}

	@Test
	void testUpdateMissingTask() {
		assertThrows(IllegalArgumentException.class, () -> service.updateTaskName("missing", "Name"));
		assertThrows(IllegalArgumentException.class, () -> service.updateTaskPriority("missing", Priority.HIGH));
	}

	// ===== Priority order =====

	@Test
	void testNextTaskIsHighestPriority() {
		assertNull(service.getNextTask()); // None yet
		service.addTask(task("low", Priority.LOW, DAY_1));
		service.addTask(task("high", Priority.HIGH, DAY_3));
		service.addTask(task("medium", Priority.MEDIUM, DAY_2));
		assertEquals("high", service.getNextTask().getTaskId());
	}

	@Test
	void testSamePriorityEarliestDueDateFirst() {
		service.addTask(task("later", Priority.HIGH, DAY_3));
		service.addTask(task("sooner", Priority.HIGH, DAY_1));
		assertEquals("sooner", service.getNextTask().getTaskId());
	}

	@Test
	void testSamePriorityAndDateKeepsBoth() {
		// The ID tie-breaker means neither task is treated as a duplicate
		service.addTask(task("a", Priority.HIGH, DAY_1));
		service.addTask(task("b", Priority.HIGH, DAY_1));
		assertEquals(2, service.getTasksByUrgency().size());
	}

	@Test
	void testTasksByUrgencyOrder() {
		service.addTask(task("3", Priority.LOW, DAY_1));
		service.addTask(task("2", Priority.MEDIUM, DAY_2));
		service.addTask(task("1b", Priority.HIGH, DAY_2));
		service.addTask(task("1a", Priority.HIGH, DAY_1));

		List<Task> ordered = service.getTasksByUrgency();

		assertEquals("1a", ordered.get(0).getTaskId());
		assertEquals("1b", ordered.get(1).getTaskId());
		assertEquals("2", ordered.get(2).getTaskId());
		assertEquals("3", ordered.get(3).getTaskId());
	}

	@Test
	void testCompleteNextTask() {
		service.addTask(task("high", Priority.HIGH, DAY_1));
		service.addTask(task("low", Priority.LOW, DAY_1));

		assertEquals("high", service.completeNextTask().getTaskId());
		assertNull(service.getTask("high")); // Removed from the HashMap too
		assertEquals("low", service.getNextTask().getTaskId());
		assertEquals("low", service.completeNextTask().getTaskId());
		assertNull(service.completeNextTask()); // Nothing left
	}

	@Test
	void testUpdatePriorityMovesTask() {
		service.addTask(task("a", Priority.LOW, DAY_1));
		service.addTask(task("b", Priority.MEDIUM, DAY_1));
		service.updateTaskPriority("a", Priority.HIGH);

		assertEquals("a", service.getNextTask().getTaskId());
		assertEquals(2, service.getTasksByUrgency().size()); // No leftover copy
	}

	@Test
	void testUpdateDueDateMovesTask() {
		service.addTask(task("a", Priority.HIGH, DAY_3));
		service.addTask(task("b", Priority.HIGH, DAY_2));
		service.updateTaskDueDate("a", DAY_1);

		assertEquals("a", service.getNextTask().getTaskId());
		// Deleting still works after the move, proving the set can find the task
		service.deleteTask("a");
		assertEquals("b", service.getNextTask().getTaskId());
	}

	@Test
	void testInvalidPriorityOrDateChangesNothing() {
		service.addTask(task("a", Priority.HIGH, DAY_1));
		assertThrows(IllegalArgumentException.class, () -> service.updateTaskPriority("a", null));
		assertThrows(IllegalArgumentException.class, () -> service.updateTaskDueDate("a", null));

		assertEquals(Priority.HIGH, service.getTask("a").getPriority());
		assertEquals(DAY_1, service.getTask("a").getDueDate());
		assertEquals("a", service.getNextTask().getTaskId());
	}

	@Test
	void testNextTaskWithManyTasks() {
		// 1,000 low-priority tasks, then one high-priority task
		for (int i = 0; i < 1000; i++) {
			service.addTask(task("T" + i, Priority.LOW, DAY_1));
		}
		service.addTask(task("urgent", Priority.HIGH, DAY_3));
		assertEquals("urgent", service.getNextTask().getTaskId());
		assertEquals(1001, service.size());
	}
}