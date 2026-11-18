# CS 320 Services: Algorithms and Data Structures Enhancement

Enhanced from my CS 320 Contact, Task, and Appointment services for CS 499.

## Design Decisions

- Kept each service's HashMap for O(1) lookups by ID, and added a second,
  ordered structure (TreeMap or TreeSet) for features that need sorting,
  ranges, or prefix search. The trade-off is extra memory and keeping both
  structures in sync on every add, update, and delete.
