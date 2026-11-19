/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: AppointmentService manages appointments. It keeps two structures:
 *   - a HashMap keyed by ID, for O(1) lookups by ID
 *   - a TreeMap keyed by start time, which keeps appointments sorted so that
 *     conflict checks and date-range lookups take O(log n) instead of O(n)
 * Both structures always hold the same appointments.
 */
package appointmentservice;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AppointmentService {

	// Fast lookup by ID: O(1) on average
	private final Map<String, Appointment> appointmentsById = new HashMap<>();

	// Sorted by start time (a red-black tree): O(log n) search, insert, and delete.
	// No two appointments overlap, so no two share a start time, which makes
	// start time a safe unique key.
	private final TreeMap<LocalDateTime, Appointment> appointmentsByStart = new TreeMap<>();

	// Supplies the current time. Tests pass a fixed clock so results are repeatable.
	private final Clock clock;

	public AppointmentService() {
		this(Clock.systemDefaultZone());
	}

	public AppointmentService(Clock clock) {
		this.clock = clock;
	}

	/**
	 * Add an appointment if its ID is unique, it starts in the future,
	 * and it doesn't overlap an existing appointment.
	 * Time: O(log n) for the conflict check plus O(log n) to insert.
	 */
	public void addAppointment(Appointment appointment) {
		if (appointment == null) {
			throw new IllegalArgumentException("Appointment cannot be null.");
		}
		if (appointmentsById.containsKey(appointment.getAppointmentId())) {
			throw new IllegalArgumentException("Appointment ID already exists.");
		}
		requireFuture(appointment.getStart());
		requireNoConflict(appointment.getStart(), appointment.getEnd(), null);

		appointmentsById.put(appointment.getAppointmentId(), appointment);
		appointmentsByStart.put(appointment.getStart(), appointment);
	}

	/**
	 * Delete an appointment from both structures.
	 * Time: O(1) for the HashMap plus O(log n) for the TreeMap.
	 */
	public void deleteAppointment(String appointmentId) {
		Appointment removed = appointmentsById.remove(appointmentId);
		if (removed == null) {
			throw new IllegalArgumentException("Appointment cannot be found.");
		}
		appointmentsByStart.remove(removed.getStart());
	}

	/**
	 * Find an appointment by ID. Time: O(1) on average.
	 * @return the appointment, or null if not found
	 */
	public Appointment getAppointment(String appointmentId) {
		return appointmentsById.get(appointmentId);
	}

	/**
	 * Move an appointment to a new time and length, keeping its ID and description.
	 * The appointment is ignored when checking for conflicts, so it can move to a
	 * time that overlaps its own old slot (for example, 30 minutes later).
	 * If anything is invalid, nothing changes.
	 * Time: O(log n).
	 */
	public void rescheduleAppointment(String appointmentId, LocalDateTime newStart, Duration newDuration) {
		Appointment existing = appointmentsById.get(appointmentId);
		if (existing == null) {
			throw new IllegalArgumentException("Appointment cannot be found.");
		}

		// Build the updated appointment first; its constructor validates the new values
		Appointment updated = new Appointment(appointmentId, newStart, newDuration, existing.getDescription());
		requireFuture(updated.getStart());
		requireNoConflict(updated.getStart(), updated.getEnd(), appointmentId);

		// Only change the structures after every check has passed
		appointmentsByStart.remove(existing.getStart());
		appointmentsById.put(appointmentId, updated);
		appointmentsByStart.put(updated.getStart(), updated);
	}

	/**
	 * List appointments that start within [rangeStart, rangeEnd), in time order.
	 * Time: O(log n + k), where k is the number of appointments returned.
	 */
	public List<Appointment> getAppointmentsBetween(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
		if (rangeStart == null || rangeEnd == null || rangeEnd.isBefore(rangeStart)) {
			throw new IllegalArgumentException("Invalid date range.");
		}
		return Collections.unmodifiableList(new ArrayList<>(
				appointmentsByStart.subMap(rangeStart, true, rangeEnd, false).values()));
	}

	/**
	 * Find the next appointment that hasn't started yet.
	 * Time: O(log n).
	 * @return the next appointment, or null if there are none
	 */
	public Appointment getNextAppointment() {
		Map.Entry<LocalDateTime, Appointment> next = appointmentsByStart.ceilingEntry(LocalDateTime.now(clock));
		return next == null ? null : next.getValue();
	}

	/** @return the number of appointments */
	public int size() {
		return appointmentsById.size();
	}

	/**
	 * Throw if an appointment would start in the past
	 */
	private void requireFuture(LocalDateTime start) {
		if (start.isBefore(LocalDateTime.now(clock))) {
			throw new IllegalArgumentException("Appointment cannot start in the past.");
		}
	}

	/**
	 * Throw if [start, end) overlaps an existing appointment.
	 *
	 * Because stored appointments never overlap and are sorted by start time,
	 * only two can possibly overlap a new one: the latest appointment starting
	 * at or before it (which might run past its start), and the earliest
	 * appointment starting at or after it (which might begin before it ends).
	 * Every other appointment ends before the first of these begins, or starts
	 * after the second one does. So two O(log n) lookups replace an O(n) scan.
	 *
	 * Appointments that touch (one ends exactly when the next starts) do not overlap.
	 *
	 * @param ignoreId an appointment to skip, used when rescheduling; null for none
	 */
	private void requireNoConflict(LocalDateTime start, LocalDateTime end, String ignoreId) {
		// Closest appointment starting at or before the new start
		Map.Entry<LocalDateTime, Appointment> before = appointmentsByStart.floorEntry(start);
		if (before != null && before.getValue().getAppointmentId().equals(ignoreId)) {
			before = appointmentsByStart.lowerEntry(before.getKey());
		}

		// Closest appointment starting at or after the new start
		Map.Entry<LocalDateTime, Appointment> after = appointmentsByStart.ceilingEntry(start);
		if (after != null && after.getValue().getAppointmentId().equals(ignoreId)) {
			after = appointmentsByStart.higherEntry(after.getKey());
		}

		if (before != null && before.getValue().getEnd().isAfter(start)) {
			throw new IllegalArgumentException("Conflicts with appointment " + before.getValue().getAppointmentId() + ".");
		}
		if (after != null && after.getValue().getStart().isBefore(end)) {
			throw new IllegalArgumentException("Conflicts with appointment " + after.getValue().getAppointmentId() + ".");
		}
	}
}