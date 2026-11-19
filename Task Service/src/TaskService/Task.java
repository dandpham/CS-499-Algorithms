/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: Task represents a task with a unique ID, name, description,
 * priority level, and due date.
 */
package TaskService;

import java.time.LocalDate;

public final class Task {

	/** Priority levels, listed from most to least urgent. */
	public enum Priority {
		HIGH, MEDIUM, LOW
	}

	public static final int MAX_ID_LENGTH = 10;
	public static final int MAX_NAME_LENGTH = 20;
	public static final int MAX_DESCRIPTION_LENGTH = 50;

	// Unique task ID that cannot be updated
	private final String id;

	private String name;
	private String description;
	private Priority priority;
	private LocalDate dueDate;

	/**
	 * @param id          Unique ID (non-null, max 10 characters, cannot be updated)
	 * @param name        Name (non-null, max 20 characters)
	 * @param description Description (non-null, max 50 characters)
	 * @param priority    Priority level (non-null)
	 * @param dueDate     Due date (non-null)
	 */
	public Task(String id, String name, String description, Priority priority, LocalDate dueDate) {
		if (id == null || id.length() > MAX_ID_LENGTH) {
			throw new IllegalArgumentException("Task ID cannot be null or exceed " + MAX_ID_LENGTH + " characters.");
		}
		this.id = id;
		this.name = validName(name);
		this.description = validDescription(description);
		this.priority = validPriority(priority);
		this.dueDate = validDueDate(dueDate);
	}

	// Getters
	public String getTaskId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Priority getPriority() {
		return priority;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	// Setters (each one validates using the same rules as the constructor)
	public void setName(String name) {
		this.name = validName(name);
	}

	public void setDescription(String description) {
		this.description = validDescription(description);
	}

	/**
	 * Package-private on purpose: TaskService keeps tasks in a TreeSet sorted by
	 * priority and due date. If outside code changed either value directly, the
	 * set would be out of order and could lose track of the task. Only the
	 * service may change them, so it can remove and re-add the task.
	 */
	void setPriority(Priority priority) {
		this.priority = validPriority(priority);
	}

	/** Package-private for the same reason as setPriority(). */
	void setDueDate(LocalDate dueDate) {
		this.dueDate = validDueDate(dueDate);
	}

	// Validation rules, written once and shared by the constructor and setters

	static String validName(String name) {
		if (name == null || name.length() > MAX_NAME_LENGTH) {
			throw new IllegalArgumentException("Name cannot be null or exceed " + MAX_NAME_LENGTH + " characters.");
		}
		return name;
	}

	static String validDescription(String description) {
		if (description == null || description.length() > MAX_DESCRIPTION_LENGTH) {
			throw new IllegalArgumentException(
					"Description cannot be null or exceed " + MAX_DESCRIPTION_LENGTH + " characters.");
		}
		return description;
	}

	static Priority validPriority(Priority priority) {
		if (priority == null) {
			throw new IllegalArgumentException("Priority cannot be null.");
		}
		return priority;
	}

	static LocalDate validDueDate(LocalDate dueDate) {
		if (dueDate == null) {
			throw new IllegalArgumentException("Due date cannot be null.");
		}
		return dueDate;
	}

	@Override
	public String toString() {
		return id + " (" + name + ", " + priority + ", due " + dueDate + ")";
	}
}