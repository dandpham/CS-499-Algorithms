/**
 * Name: Dan Pham
 * Course: CS-320
 * Date: 2/14/2025
 * Description: AppointmentService class to manage appointments to include adding appointments with a unique appointment ID
 * and being able to delete appointments per appointment ID.
 */
package appointmentservice;

import java.util.HashMap;
import java.util.Map;
import appointmentservice.Appointment;

public class AppointmentService {
	private final Map<String, Appointment> appointments = new HashMap<>(); //Storage for appointments 
	
	//Method to add an appointment
	public void addAppointment(Appointment appointment) {
		if (appointment == null || appointments.containsKey(appointment.getAppointmentId())) {
			throw new IllegalArgumentException("Appointment already exists or is null.");
		}
		appointments.put(appointment.getAppointmentId(), appointment);
	}
	
	//Method to delete an appointment 
	public void deleteAppointment(String appointmentId) {
		if (!appointments.containsKey(appointmentId)) {
			throw new IllegalArgumentException("Appointment cannot be found.");
		}
		appointments.remove(appointmentId);
	}
	
	//Method to retrieve an appointment
	public Appointment getAppointment(String appointmentId) {
		return appointments.get(appointmentId);
	}

}
