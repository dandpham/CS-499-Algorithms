/**
 * Name: Dan Pham
 * Course: CS-320
 * Date: 2/14/2025
 * Description: JUnit test cases for AppointmentService class
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.Calendar;
import appointmentservice.Appointment;
import appointmentservice.AppointmentService;

class AppointmentServiceTest {
	private AppointmentService service;
	
	
	@Test
	void testAddAppointment() {
		service = new AppointmentService();
		Calendar futureDate = Calendar.getInstance();
		futureDate.add(Calendar.DATE,  1);
		Appointment appointment = new Appointment("12345", futureDate.getTime(), "Test Description.");
		service.addAppointment(appointment);
		assertEquals(appointment, service.getAppointment("12345"));
	}

	@Test
	void testDeleteAppointment() {
		service = new AppointmentService();
		Calendar futureDate = Calendar.getInstance();
		futureDate.add(Calendar.DATE, 1);
		Appointment appointment = new Appointment("1234567890", futureDate.getTime(), "Test Description.");
		service.addAppointment(appointment);
		service.deleteAppointment("1234567890");
		assertNull(service.getAppointment("1234567890"));
	}

}
