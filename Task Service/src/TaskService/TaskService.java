/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: TaskService manages tasks. It keeps two structures:
 *   - a HashMap keyed by ID, for O(1) lookups by ID
 *   - a TreeSet sorted by priority, then due date, then ID, so the most
 *     urgent task is always first and can be found in O(log n)
 * Both structures always hold the same tasks.
 *
 * A TreeSet was chosen over a PriorityQueue because a PriorityQueue needs
 * O(n) to remove a specific task (for a delete or a priority change), while
 * a TreeSet does it in O(log n).
 */
package TaskService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import TaskService.Task.Priority;

public class TaskService {

	// Most urgent first: HIGH before MEDIUM before LOW, then the earliest due
	// date, then the ID as a final tie-breaker. The ID is unique, so no two
	// tasks ever compare as equal and the set never drops a task by mistake.
	private static final Comparator<Task> BY_URGENCY = Comparator
			.comparing(Task::getPriority)
			.thenComparing(Task::getDueDate)
			.thenComparing(Task::getTaskId);

	// Fast lookup by ID: O(1) on average
	private final Map<String, Task> tasks = new HashMap<>();

	// Sorted by urgency (a red-black tree)
	private final TreeSet<Task> tasksByUrgency = new TreeSet<>(BY_URGENCY);

	/**
	 * Create a task with a generated unique ID and add it.
	 * @return the new task's ID
	 * Time: O(log n).
	 */
	public String addTask(String name, String description, Priority priority, LocalDate dueDate) {
		String id;
		do {
			// The original version never checked whether the generated ID was
			// already in use, so a rare collision would overwrite a task.
			id = UUID.randomUUID().toString().substring(0, Task.MAX_ID_LENGTH);
		} while (tasks.containsKey(id));

		addTask(new Task(id, name, description, priority, dueDate));
		return id;
	}

	/**
	 * Add an existing task if its ID is unique.
	 * Time: O(1) for the HashMap plus O(log n) for the TreeSet.
	 */
	public void addTask(Task task) {
		if (task == null) {
			throw new IllegalArgumentException("Task cannot be null.");
		}
		if (tasks.containsKey(task.getTaskId())) {
			throw new IllegalArgumentException("Task ID already exists.");
		}
		tasks.put(task.getTaskId(), task);
		tasksByUrgency.add(task);
	}

	/**
	 * Delete a task from both structures.
	 * Time: O(1) for the HashMap plus O(log n) for the TreeSet.
	 */
	public void deleteTask(String id) {
		Task removed = tasks.remove(id);
		if (removed == null) {
			throw new IllegalArgumentException("Task ID not found.");
		}
		tasksByUrgency.remove(removed);
	}

	/** Update a task's name. Time: O(1). */
	public void updateTaskName(String id, String name) {
		findTask(id).setName(name);
	}

	/** Update a task's description. Time: O(1). */
	public void updateTaskDescription(String id, String description) {
		findTask(id).setDescription(description);
	}

	/**
	 * Change a task's priority. The task is removed from the TreeSet, changed,
	 * and added back, so it moves to its new place in the order.
	 * Time: O(log n).
	 */
	public void updateTaskPriority(String id, Priority priority) {
		Task task = findTask(id);
		Task.validPriority(priority); // Check first, so a bad value changes nothing
		tasksByUrgency.remove(task);
		task.setPriority(priority);
		tasksByUrgency.add(task);
	}

	/**
	 * Change a task's due date, moving it to its new place in the order.
	 * Time: O(log n).
	 */
	public void updateTaskDueDate(String id, LocalDate dueDate) {
		Task task = findTask(id);
		Task.validDueDate(dueDate);
		tasksByUrgency.remove(task);
		task.setDueDate(dueDate);
		tasksByUrgency.add(task);
	}

	/**
	 * Find a task by ID. Time: O(1) on average.
	 * @return the task, or null if not found
	 */
	public Task getTask(String id) {
		return tasks.get(id);
	}

	/**
	 * @return a read-only view of all tasks by ID. The original version
	 * returned the internal map itself, so outside code could add or remove
	 * tasks without going through the service.
	 */
	public Map<String, Task> getTasks() {
		return Collections.unmodifiableMap(tasks);
	}

	/**
	 * The most urgent task: highest priority, then earliest due date.
	 * Time: O(log n). Without the TreeSet, this would mean scanning every
	 * task: O(n).
	 * @return the most urgent task, or null if there are none
	 */
	public Task getNextTask() {
		return tasksByUrgency.isEmpty() ? null : tasksByUrgency.first();
	}

	/**
	 * Remove and return the most urgent task, as when it's finished.
	 * Time: O(log n).
	 * @return the completed task, or null if there are none
	 */
	public Task completeNextTask() {
		Task next = tasksByUrgency.pollFirst();
		if (next != null) {
			tasks.remove(next.getTaskId());
		}
		return next;
	}

	/**
	 * Every task, most urgent first.
	 * Time: O(n), since the TreeSet is already in order and needs no sorting.
	 */
	public List<Task> getTasksByUrgency() {
		return Collections.unmodifiableList(new ArrayList<>(tasksByUrgency));
	}

	/** @return the number of tasks */
	public int size() {
		return tasks.size();
	}

	// Look up a task, or throw if it doesn't exist
	private Task findTask(String id) {
		Task task = tasks.get(id);
		if (task == null) {
			throw new IllegalArgumentException("Task ID not found.");
		}
		return task;
	}
}