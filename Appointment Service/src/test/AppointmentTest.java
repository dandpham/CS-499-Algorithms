/**
 * Name: Dan Pham
 * Course: CS-320
 * Date: 2/14/2025
 * Description: JUnit test cases for Appointment class
 */
package test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

import appointmentservice.Appointment;

class AppointmentTest {

	@Test
	void testValidAppointmentCreation() {
		Calendar futureDate = Calendar.getInstance();
		futureDate.add(Calendar.DATE, 1);
		Appointment appointment = new Appointment("1234567890", futureDate.getTime(), "Test Description.");
		assertEquals("1234567890", appointment.getAppointmentId());
		assertEquals("Test Description.", appointment.getDescription());
	}
	
	@Test
	void testInvalidAppointmentId() {
		Calendar futureDate = Calendar.getInstance();
		futureDate.add(Calendar.DATE, 1);
		assertThrows(IllegalArgumentException.class, () -> new Appointment(null, futureDate.getTime(), "Test Description.")); //Null ID
		assertThrows(IllegalArgumentException.class, () -> new Appointment("12345678901", futureDate.getTime(), "Test Description.")); //Over 10 character max
	}
	
	@Test
	void testInvalidAppointmentDate() {
		assertThrows(IllegalArgumentException.class, () -> new Appointment("1234567890", null, "Test Description.")); //Null date
		Calendar pastDate = Calendar.getInstance();
		pastDate.add(Calendar.DATE, -1);
		assertThrows(IllegalArgumentException.class, () -> new Appointment("1234567890", pastDate.getTime(), "Test Description.")); //Past date
	}

	@Test
	void testInvalidDescription () {
		Calendar futureDate = Calendar.getInstance();
		futureDate.add(Calendar.DATE, 1);
		assertThrows(IllegalArgumentException.class, () -> new Appointment("1234567890", futureDate.getTime(), null)); //Null description
		assertThrows(IllegalArgumentException.class, () -> new Appointment("1234567890", null, "Really Really Really Really Really Long Description")); //Over 50 character max
	}
	
	@Test 
	void testGetAppointmentDate() {
		Calendar futureDate = Calendar.getInstance();
		futureDate.add(Calendar.DATE, 1);
		Date expectedDate = futureDate.getTime();
		Appointment appointment = new Appointment("1234567890", expectedDate, "Test Description.");
		assertEquals(expectedDate, appointment.getAppointmentDate());
	}
}
