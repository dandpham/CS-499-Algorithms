/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: JUnit test cases for the Appointment class
 */
package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import appointmentservice.Appointment;

class AppointmentTest {

	private static final LocalDateTime START = LocalDateTime.of(2026, 12, 15, 9, 0);
	private static final Duration ONE_HOUR = Duration.ofHours(1);

	@Test
	void testValidAppointmentCreation() {
		Appointment appointment = new Appointment("1234567890", START, ONE_HOUR, "Test Description.");
		assertEquals("1234567890", appointment.getAppointmentId());
		assertEquals(START, appointment.getStart());
		assertEquals(ONE_HOUR, appointment.getDuration());
		assertEquals("Test Description.", appointment.getDescription());
	}

	@Test
	void testEndIsStartPlusDuration() {
		Appointment appointment = new Appointment("1", START, Duration.ofMinutes(90), "Test");
		assertEquals(LocalDateTime.of(2026, 12, 15, 10, 30), appointment.getEnd());
	}

	@Test
	void testInvalidAppointmentId() {
		assertThrows(IllegalArgumentException.class, () -> new Appointment(null, START, ONE_HOUR, "Test")); // Null ID
		assertThrows(IllegalArgumentException.class, () -> new Appointment("12345678901", START, ONE_HOUR, "Test")); // 11 characters
	}

	@Test
	void testNullStart() {
		assertThrows(IllegalArgumentException.class, () -> new Appointment("1", null, ONE_HOUR, "Test"));
	}

	@Test
	void testInvalidDuration() {
		assertThrows(IllegalArgumentException.class, () -> new Appointment("1", START, null, "Test")); // Null
		assertThrows(IllegalArgumentException.class, () -> new Appointment("1", START, Duration.ZERO, "Test")); // Zero
		assertThrows(IllegalArgumentException.class, () -> new Appointment("1", START, Duration.ofMinutes(-30), "Test")); // Negative
		assertThrows(IllegalArgumentException.class, () -> new Appointment("1", START, Duration.ofHours(25), "Test")); // Over 24 hours
	}

	@Test
	void testMaxDurationAllowed() {
		// Boundary: exactly 24 hours is allowed
		assertDoesNotThrow(() -> new Appointment("1", START, Duration.ofHours(24), "Test"));
	}

	@Test
	void testInvalidDescription() {
		assertThrows(IllegalArgumentException.class, () -> new Appointment("1", START, ONE_HOUR, null)); // Null
		assertThrows(IllegalArgumentException.class,
				() -> new Appointment("1", START, ONE_HOUR, "Really Really Really Really Really Long Description")); // 51 characters
	}

	@Test
	void testMaxLengthValuesAllowed() {
		// Boundary: a 10-character ID and a 50-character description are allowed
		assertDoesNotThrow(() -> new Appointment("1234567890", START, ONE_HOUR,
				"12345678901234567890123456789012345678901234567890"));
	}
}
