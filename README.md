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
- ContactService: added a TreeMap keyed by lowercase last name next to the
  HashMap. Prefix search ("Ph" finds Pham and Phelps) uses subMap in
  O(log n + k) instead of scanning every contact in O(n).
- Each last name maps to a set of contacts, so several people can share a
  last name without overwriting each other in the index. Sets are ordered
  by contact ID, which never changes, so their order can't go stale.
- Made Contact.setLastName() package-private, so only ContactService can
  change a last name and the index can't point to the wrong name.
- Changed ContactService to throw exceptions like the other two services,
  instead of returning true or false. updateContact() now validates every
  value first and changes nothing if any is invalid; the original silently
  skipped invalid values and still reported success.
- Wrote each Contact validation rule once and shared it between the
  constructor and setters, with named constants instead of magic numbers.
- Fixed two problems in the original ContactServiceTest: testGetContact had
  no assertion, and the file mixed JUnit 4 and JUnit 5 imports.
- TaskService: added a priority level (HIGH, MEDIUM, LOW) and a due date to
  each task, and a TreeSet sorted by priority, then due date, then ID, next
  to the HashMap. getNextTask() and completeNextTask() take O(log n)
  instead of scanning every task in O(n).
- Chose a TreeSet over a PriorityQueue: a PriorityQueue removes a specific
  task in O(n), but a TreeSet does it in O(log n). Deleting a task and
  changing its priority both need that.
- The ID is the last tie-breaker in the sort order. A TreeSet drops items
  that compare as equal, so without it, two tasks with the same priority
  and due date would lose one.
- Changing a priority or due date removes the task from the TreeSet,
  changes it, and adds it back. Made setPriority() and setDueDate()
  package-private so only TaskService can change them.
- addTask() now checks that the generated ID isn't already in use and
  returns the new ID. The original could overwrite a task on a collision.
- getTasks() now returns a read-only view. The original returned the
  internal map, so outside code could change tasks without the service.
- Fixed the wrong error message in Task.setName(), removed unused imports,
  renamed the Tasks field to tasks, and replaced magic numbers with
  named constants.

## Big O Comparison

n = number of stored objects, k = number of results returned.

| Operation | Original | Enhanced | Structure used |
|---|---|---|---|
| Find by ID | O(1) | O(1) | HashMap (unchanged) |
| Add or delete | O(1) | O(log n) | HashMap + tree |
| Check a new appointment for conflicts | Not checked | O(log n) | TreeMap floorEntry / ceilingEntry |
| Appointments in a date range | O(n) scan | O(log n + k) | TreeMap subMap |
| Next upcoming appointment | O(n) scan | O(log n) | TreeMap ceilingEntry |
| Search contacts by last name prefix | O(n) scan | O(log n + k) | TreeMap subMap |
| List contacts sorted by last name | O(n log n) sort | O(n) | TreeMap (already sorted) |
| Most urgent task | Not supported | O(log n) | TreeSet first |
| Change a task's priority | Not supported | O(log n) | TreeSet remove + add |

Trade-offs:
- Memory: each object is referenced by two structures instead of one.
- Writes: add and delete went from O(1) to O(log n), because both
  structures must be updated. Every search that used to scan all objects
  now jumps straight to its answer, which is worth it because the
  services are searched far more often than they are changed.
- Complexity: the two structures must stay in sync. Setters that affect
  sort order are package-private, and tests confirm that deletes and
  updates change both structures.
- TreeSet over PriorityQueue for tasks: a PriorityQueue removes a
  specific task in O(n); a TreeSet does it in O(log n).

Tests: 30 originally (7 Appointment, 15 Contact, 8 Task), now 94
(32 Appointment, 31 Contact, 31 Task), all JUnit 5.
