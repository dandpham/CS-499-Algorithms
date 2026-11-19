# CS 320 Services: Algorithms and Data Structures Enhancement

Enhanced from my CS 320 Contact, Task, and Appointment services for CS 499.

## Design Decisions

- Kept each service's HashMap for O(1) lookups by ID, and added a second,
  ordered structure (TreeMap or TreeSet) for features that need sorting,
  ranges, or prefix search. The trade-off is extra memory and keeping both
  structures in sync on every add, update, and delete.
- Appointment: switched from java.util.Date to java.time.LocalDateTime and
  added a duration. LocalDateTime is immutable, so getStart() can no longer
  let outside code change an appointment, which fixes a code review finding.
- AppointmentService: added a TreeMap keyed by start time next to the
  HashMap. Conflict checks only compare the two neighboring appointments
  (floorEntry and ceilingEntry), dropping the check from O(n) to O(log n).
  Date-range lookups use subMap in O(log n + k).
- Rescheduling skips the appointment being moved during the conflict check,
  so it can move into its own old time slot. Nothing changes until every
  check passes, so a failed reschedule leaves the original unchanged.
- Moved the "no past dates" rule from Appointment to AppointmentService,
  which reads the time from a Clock. Tests use a fixed clock, so they give
  the same results every run.
- Fixed a bug in the original AppointmentTest, where the long-description
  test also passed a null date, so it was testing the date check instead.
