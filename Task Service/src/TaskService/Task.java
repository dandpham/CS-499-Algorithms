/**
 * Name: Dan Pham
 * Class: CS-320
 * Date: 2/14/2025
 * Description: Task class represents a task with a unique ID, name, and description. 
 */
package TaskService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Task {
	//Unique task ID that cannot be updated
	private final String id;
	
	//Task information (name, description)
	private String name;
	private String description;
	
	/**
	 * 
	 * @param id Unique ID (non-null, max 10 characters, cannot be updated)
	 * @param name Name (non-null, max 20 characters)
	 * @param description Description (non-null, max 50 characters)
	 */
	public Task(String id, String name, String description) {
		if (id == null || id.length() > 10) {
			throw new IllegalArgumentException("Task ID cannot be null or exceed 10 characters.");
		}
		if (name == null || name.length() > 20) {
			throw new IllegalArgumentException("Name cannot be null or exceed 20 characters.");
		}
		if (description == null || description.length() > 50) {
			throw new IllegalArgumentException("Description cannot be null or exceed 50 characters.");
		}
		this.id = id;
		this.name = name;
		this.description = description;
	} 
	
	//Getters
	public String getTaskId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	//Setters
	public void setName(String name) {
		if (name == null || name.length() > 20) {
			throw new IllegalArgumentException("Task ID cannot be null or exceed 10 characters.");
		}
		this.name = name;
		}
	
	public void setDescription(String description) {
		if (description  == null || description.length() > 50) {
			throw new IllegalArgumentException("Description cannot be null or exceed 50 characters.");
		}
		this.description = description;
	}

	
}
