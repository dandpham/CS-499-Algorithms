/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: Appointment represents a scheduled appointment with a unique ID,
 * a start time, a length, and a description. Appointments are immutable: to
 * change the time, AppointmentService replaces the appointment with a new one.
 */
package appointmentservice;

import java.time.Duration;
import java.time.LocalDateTime;

public final class Appointment {

	public static final int MAX_ID_LENGTH = 10;
	public static final int MAX_DESCRIPTION_LENGTH = 50;
	public static final Duration MAX_DURATION = Duration.ofHours(24);

	private final String appointmentId;
	private final LocalDateTime start;
	private final Duration duration;
	private final String description;

	/**
	 * @param appointmentId Unique ID (non-null, max 10 characters, cannot be updated)
	 * @param start         Start date and time (non-null)
	 * @param duration      Length (positive, at most 24 hours)
	 * @param description   Description (non-null, max 50 characters)
	 */
	public Appointment(String appointmentId, LocalDateTime start, Duration duration, String description) {
		if (appointmentId == null || appointmentId.length() > MAX_ID_LENGTH) {
			throw new IllegalArgumentException("Invalid appointment ID.");
		}
		if (start == null) {
			throw new IllegalArgumentException("Invalid appointment start time.");
		}
		if (duration == null || duration.isNegative() || duration.isZero()
				|| duration.compareTo(MAX_DURATION) > 0) {
			throw new IllegalArgumentException("Invalid appointment length.");
		}
		if (description == null || description.length() > MAX_DESCRIPTION_LENGTH) {
			throw new IllegalArgumentException("Invalid description.");
		}
		this.appointmentId = appointmentId;
		this.start = start;
		this.duration = duration;
		this.description = description;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	/**
	 * LocalDateTime is immutable, so returning it can't let outside code
	 * change this appointment (unlike the original java.util.Date field).
	 */
	public LocalDateTime getStart() {
		return start;
	}

	public Duration getDuration() {
		return duration;
	}

	/** The moment the appointment ends: start plus duration */
	public LocalDateTime getEnd() {
		return start.plus(duration);
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return appointmentId + " (" + start + " to " + getEnd() + ")";
	}
}
