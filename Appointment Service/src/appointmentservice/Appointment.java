/**
 * Name: Dan Pham
 * Course: CS-320
 * Date: 2/14/2025
 * Description: Appointment class represents an individual appointment with a unique ID, date, and description
 */
package appointmentservice;

import java.util.Date;

public class Appointment {
	private final String appointmentId; 
	private final Date appointmentDate;
	private final String description;
	
	/**
	 * 
	 * @param appointmentId ID (Unique, non-null, max 10 characters, cannot be updated)
	 * @param appointmentDate Date (non-null, cannot be in the past)
	 * @param description (non-null, max 50 characters)
	 */
	
	//Constructor to initialize the appointment object 
	public Appointment(String appointmentId, Date appointmentDate, String description) {
		if (appointmentId == null || appointmentId.length() > 10) {
			throw new IllegalArgumentException("Invalid appointment ID.");
		}
		if (appointmentDate == null || appointmentDate.before(new Date())) {
			throw new IllegalArgumentException("Invalid appointment date.");
		}
		if (description == null || description.length() > 50) {
			throw new IllegalArgumentException("Invalid description.");
		}
		this.appointmentId = appointmentId;
		this.appointmentDate = appointmentDate;
		this.description = description;
	}
	
	//Getters
	public String getAppointmentId() {
		return appointmentId;
	}
	
	public Date getAppointmentDate() {
		return appointmentDate;
	}
	
	public String getDescription() {
		return description;
	}
	
}
