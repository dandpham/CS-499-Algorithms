/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: JUnit test cases for AppointmentService, including conflict
 * detection, date-range lookups, and rescheduling
 */
package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import appointmentservice.Appointment;
import appointmentservice.AppointmentService;

class AppointmentServiceTest {

	// A fixed "current time" so the tests always give the same result
	private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 12, 9, 0);
	private static final ZoneId ZONE = ZoneOffset.UTC;
	private static final Clock FIXED_CLOCK = Clock.fixed(NOW.atZone(ZONE).toInstant(), ZONE);

	private static final Duration ONE_HOUR = Duration.ofHours(1);

	private AppointmentService service;

	@BeforeEach
	void setUp() {
		service = new AppointmentService(FIXED_CLOCK);
	}

	/** Helper: a one-hour appointment on Dec 15, 2026 at the given hour and minute */
	private Appointment at(String id, int hour, int minute) {
		return new Appointment(id, LocalDateTime.of(2026, 12, 15, hour, minute), ONE_HOUR, "Test");
	}

	// ===== Add, get, and delete =====

	@Test
	void testAddAndGetAppointment() {
		Appointment appointment = at("1", 9, 0);
		service.addAppointment(appointment);
		assertSame(appointment, service.getAppointment("1"));
		assertEquals(1, service.size());
	}

	@Test
	void testAddNullAppointment() {
		assertThrows(IllegalArgumentException.class, () -> service.addAppointment(null));
	}

	@Test
	void testAddDuplicateId() {
		service.addAppointment(at("1", 9, 0));
		assertThrows(IllegalArgumentException.class, () -> service.addAppointment(at("1", 14, 0)));
	}

	@Test
	void testAddPastAppointment() {
		Appointment past = new Appointment("1", NOW.minusDays(1), ONE_HOUR, "Test");
		assertThrows(IllegalArgumentException.class, () -> service.addAppointment(past));
	}

	@Test
	void testDeleteAppointment() {
		service.addAppointment(at("1", 9, 0));
		service.deleteAppointment("1");
		assertNull(service.getAppointment("1"));
		assertEquals(0, service.size());
	}

	@Test
	void testDeleteFreesTimeSlot() {
		// Proves the TreeMap entry was removed too, not just the HashMap entry
		service.addAppointment(at("1", 9, 0));
		service.deleteAppointment("1");
		assertDoesNotThrow(() -> service.addAppointment(at("2", 9, 0)));
	}

	@Test
	void testDeleteMissingAppointment() {
		assertThrows(IllegalArgumentException.class, () -> service.deleteAppointment("missing"));
	}

	// ===== Conflict detection =====

	@Test
	void testConflictStartsDuringExisting() {
		service.addAppointment(at("1", 9, 0)); // 9:00 to 10:00
		assertThrows(IllegalArgumentException.class, () -> service.addAppointment(at("2", 9, 30)));
	}

	@Test
	void testConflictEndsDuringExisting() {
		service.addAppointment(at("1", 9, 0)); // 9:00 to 10:00
		assertThrows(IllegalArgumentException.class, () -> service.addAppointment(at("2", 8, 30)));
	}

	@Test
	void testConflictSameStartTime() {
		service.addAppointment(at("1", 9, 0));
		assertThrows(IllegalArgumentException.class, () -> service.addAppointment(at("2", 9, 0)));
	}

	@Test
	void testConflictNewCoversExisting() {
		service.addAppointment(at("1", 10, 0)); // 10:00 to 11:00
		Appointment longOne = new Appointment("2", LocalDateTime.of(2026, 12, 15, 9, 0), Duration.ofHours(3), "Test");
		assertThrows(IllegalArgumentException.class, () -> service.addAppointment(longOne));
	}

	@Test
	void testBackToBackAppointmentsAllowed() {
		// Boundary: touching appointments do not overlap
		service.addAppointment(at("1", 10, 0)); // 10:00 to 11:00
		assertDoesNotThrow(() -> service.addAppointment(at("2", 9, 0)));  // Ends at 10:00
		assertDoesNotThrow(() -> service.addAppointment(at("3", 11, 0))); // Starts at 11:00
		assertEquals(3, service.size());
	}

	@Test
	void testConflictCheckWithManyAppointments() {
		// 1,000 back-to-back one-hour appointments, then a conflict near the end.
		// The check only looks at two neighbors, no matter how many appointments exist.
		LocalDateTime time = LocalDateTime.of(2027, 1, 1, 0, 0);
		for (int i = 0; i < 1000; i++) {
			service.addAppointment(new Appointment("A" + i, time.plusHours(i), ONE_HOUR, "Test"));
		}
		Appointment conflict = new Appointment("X", time.plusHours(998).plusMinutes(30), ONE_HOUR, "Test");
		assertThrows(IllegalArgumentException.class, () -> service.addAppointment(conflict));
		assertEquals(1000, service.size());
	}

	// ===== Date-range lookups =====

	@Test
	void testAppointmentsBetweenReturnsInTimeOrder() {
		service.addAppointment(at("late", 15, 0));
		service.addAppointment(at("early", 9, 0));
		service.addAppointment(at("middle", 12, 0));

		List<Appointment> result = service.getAppointmentsBetween(
				LocalDateTime.of(2026, 12, 15, 0, 0), LocalDateTime.of(2026, 12, 16, 0, 0));

		assertEquals(3, result.size());
		assertEquals("early", result.get(0).getAppointmentId());
		assertEquals("middle", result.get(1).getAppointmentId());
		assertEquals("late", result.get(2).getAppointmentId());
	}

	@Test
	void testAppointmentsBetweenExcludesOutsideRange() {
		service.addAppointment(at("in", 12, 0));
		service.addAppointment(new Appointment("before", LocalDateTime.of(2026, 12, 14, 12, 0), ONE_HOUR, "Test"));
		service.addAppointment(new Appointment("after", LocalDateTime.of(2026, 12, 16, 12, 0), ONE_HOUR, "Test"));

		List<Appointment> result = service.getAppointmentsBetween(
				LocalDateTime.of(2026, 12, 15, 0, 0), LocalDateTime.of(2026, 12, 16, 0, 0));

		assertEquals(1, result.size());
		assertEquals("in", result.get(0).getAppointmentId());
	}

	@Test
	void testAppointmentsBetweenEmpty() {
		assertTrue(service.getAppointmentsBetween(NOW, NOW.plusDays(1)).isEmpty());
	}

	@Test
	void testAppointmentsBetweenInvalidRange() {
		assertThrows(IllegalArgumentException.class, () -> service.getAppointmentsBetween(NOW.plusDays(1), NOW));
		assertThrows(IllegalArgumentException.class, () -> service.getAppointmentsBetween(null, NOW));
	}

	@Test
	void testNextAppointment() {
		assertNull(service.getNextAppointment()); // None yet
		service.addAppointment(at("later", 15, 0));
		service.addAppointment(at("sooner", 9, 0));
		assertEquals("sooner", service.getNextAppointment().getAppointmentId());
	}

	// ===== Rescheduling =====

	@Test
	void testRescheduleMovesAppointment() {
		service.addAppointment(at("1", 9, 0));
		service.rescheduleAppointment("1", LocalDateTime.of(2026, 12, 15, 14, 0), ONE_HOUR);

		assertEquals(LocalDateTime.of(2026, 12, 15, 14, 0), service.getAppointment("1").getStart());
		assertEquals("Test", service.getAppointment("1").getDescription());
		// The old 9:00 slot is free again
		assertDoesNotThrow(() -> service.addAppointment(at("2", 9, 0)));
	}

	@Test
	void testRescheduleOverlappingOwnOldTime() {
		// Moving 9:00-10:00 to 9:30-10:30 overlaps only itself, so it's allowed
		service.addAppointment(at("1", 9, 0));
		assertDoesNotThrow(() -> service.rescheduleAppointment("1", LocalDateTime.of(2026, 12, 15, 9, 30), ONE_HOUR));
		assertEquals(LocalDateTime.of(2026, 12, 15, 9, 30), service.getAppointment("1").getStart());
	}

	@Test
	void testRescheduleIntoConflictLeavesOriginalUnchanged() {
		service.addAppointment(at("1", 9, 0));
		service.addAppointment(at("2", 14, 0));

		assertThrows(IllegalArgumentException.class,
				() -> service.rescheduleAppointment("1", LocalDateTime.of(2026, 12, 15, 14, 30), ONE_HOUR));

		// Nothing changed
		assertEquals(LocalDateTime.of(2026, 12, 15, 9, 0), service.getAppointment("1").getStart());
		assertThrows(IllegalArgumentException.class, () -> service.addAppointment(at("3", 9, 0)));
	}

	@Test
	void testRescheduleIntoPast() {
		service.addAppointment(at("1", 9, 0));
		assertThrows(IllegalArgumentException.class,
				() -> service.rescheduleAppointment("1", NOW.minusHours(1), ONE_HOUR));
	}

	@Test
	void testRescheduleMissingAppointment() {
		assertThrows(IllegalArgumentException.class,
				() -> service.rescheduleAppointment("missing", NOW.plusDays(1), ONE_HOUR));
	}

	@Test
	void testRescheduleInvalidDuration() {
		service.addAppointment(at("1", 9, 0));
		assertThrows(IllegalArgumentException.class,
				() -> service.rescheduleAppointment("1", LocalDateTime.of(2026, 12, 15, 9, 0), Duration.ZERO));
	}
}