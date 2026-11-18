/**
 * Name: Dan Pham
 * Class: CS-320
 * Date: 2/14/2025
 * Description: TaskService class manages task objects by storing them. 
 * Allows, tasks to be added, deleted, and updated based on their unique ID. 
 */
package TaskService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskService {
	// A hashmap to store tasks with their unique contact ID as the key
	private final Map<String, Task> Tasks = new HashMap<>();
	
	//Adds a mew task if the ID is unique 	 
	public void addTask(String name, String description) {
		String id = UUID.randomUUID().toString().substring(0, 10);
		Task task = new Task(id, name, description);
		Tasks.put(id, task);
	}
	
	//Deletes task 
	public void deleteTask(String id) {
		if (!Tasks.containsKey(id)) {
			throw new IllegalArgumentException("Task ID not found.");
		}
		Tasks.remove(id);
	}
	
	//Updates task name
	public void updateTaskName(String id, String name) {
		if (!Tasks.containsKey(id)) {
			throw new IllegalArgumentException("Task ID not found.");
		}
		Tasks.get(id).setName(name);
	}
	
	//Updates task description
	public void updateTaskDescription(String id, String description) {
		if (!Tasks.containsKey(id)) {
			throw new IllegalArgumentException("Task ID not found.");
		}
		Tasks.get(id).setDescription(description);
	}
	
	public Task getTask(String id) {
		return Tasks.get(id);
	}
	public Map<String, Task> getTasks() {
		return Tasks;
	}
}

