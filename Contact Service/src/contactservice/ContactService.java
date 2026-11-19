/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: ContactService manages contacts. It keeps two structures:
 *   - a HashMap keyed by ID, for O(1) lookups by ID
 *   - a TreeMap keyed by lowercase last name, which keeps names sorted so that
 *     prefix searches take O(log n + k) instead of scanning every contact
 * Several contacts can share a last name, so each TreeMap entry holds a set.
 * Both structures always hold the same contacts.
 */
package contactservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ContactService {

	// Fast lookup by ID: O(1) on average
	private final Map<String, Contact> contactsById = new HashMap<>();

	// Sorted by lowercase last name (a red-black tree). Each name maps to every
	// contact with that name, ordered by contact ID. The ID is used for ordering
	// because it never changes, so the set's order can't go out of date.
	private final TreeMap<String, Set<Contact>> contactsByLastName = new TreeMap<>();

	private static final Comparator<Contact> BY_ID = Comparator.comparing(Contact::getContactId);

	/**
	 * Add a new contact if its ID is unique.
	 * Time: O(1) for the HashMap plus O(log n) for the TreeMap.
	 */
	public void addContact(Contact contact) {
		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be null.");
		}
		if (contactsById.containsKey(contact.getContactId())) {
			throw new IllegalArgumentException("Contact ID already exists.");
		}
		contactsById.put(contact.getContactId(), contact);
		addToIndex(contact);
	}

	/**
	 * Delete a contact from both structures.
	 * Time: O(1) for the HashMap plus O(log n) for the TreeMap.
	 */
	public void deleteContact(String contactId) {
		Contact removed = contactsById.remove(contactId);
		if (removed == null) {
			throw new IllegalArgumentException("Contact cannot be found.");
		}
		removeFromIndex(removed);
	}

	/**
	 * Find a contact by ID. Time: O(1) on average.
	 * @return the contact, or null if not found
	 */
	public Contact getContact(String contactId) {
		return contactsById.get(contactId);
	}

	/**
	 * Update every field except the ID. All new values are checked first, so if
	 * any value is invalid, nothing changes. (The original version skipped
	 * invalid values without saying so and still reported success.)
	 * If the last name changes, the contact moves to its new place in the index.
	 * Time: O(log n).
	 */
	public void updateContact(String contactId, String firstName, String lastName,
			String phone, String address) {
		Contact contact = contactsById.get(contactId);
		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be found.");
		}

		// Validate everything up front by building a throwaway contact
		new Contact(contactId, firstName, lastName, phone, address);

		boolean lastNameChanged = !contact.getLastName().equalsIgnoreCase(lastName);
		if (lastNameChanged) {
			removeFromIndex(contact);
		}

		contact.setFirstName(firstName);
		contact.setLastName(lastName);
		contact.setPhone(phone);
		contact.setAddress(address);

		if (lastNameChanged) {
			addToIndex(contact);
		}
	}

	/**
	 * Find contacts whose last name starts with the given text, ignoring
	 * capitalization. For example, "ph" finds Pham and Phelps.
	 * Results are sorted by last name.
	 *
	 * Every name starting with the prefix sorts between the prefix itself and
	 * the prefix followed by the highest possible character, so subMap jumps
	 * straight to that section of the tree.
	 * Time: O(log n + k), where k is the number of matches. The original
	 * design would need to scan every contact: O(n).
	 */
	public List<Contact> searchByLastName(String prefix) {
		if (prefix == null || prefix.trim().isEmpty()) {
			throw new IllegalArgumentException("Search text is required.");
		}
		String start = prefix.trim().toLowerCase(Locale.US);
		String end = start + Character.MAX_VALUE;

		List<Contact> matches = new ArrayList<>();
		for (Set<Contact> sameName : contactsByLastName.subMap(start, true, end, false).values()) {
			matches.addAll(sameName);
		}
		return Collections.unmodifiableList(matches);
	}

	/**
	 * List every contact, sorted by last name.
	 * Time: O(n), since the TreeMap is already in order and needs no sorting.
	 */
	public List<Contact> getContactsSortedByLastName() {
		List<Contact> all = new ArrayList<>();
		for (Set<Contact> sameName : contactsByLastName.values()) {
			all.addAll(sameName);
		}
		return Collections.unmodifiableList(all);
	}

	/** @return the number of contacts */
	public int size() {
		return contactsById.size();
	}

	// Index helpers: keep the TreeMap in sync with the HashMap

	private static String indexKey(Contact contact) {
		return contact.getLastName().toLowerCase(Locale.US);
	}

	private void addToIndex(Contact contact) {
		contactsByLastName
				.computeIfAbsent(indexKey(contact), key -> new TreeSet<>(BY_ID))
				.add(contact);
	}

	private void removeFromIndex(Contact contact) {
		String key = indexKey(contact);
		Set<Contact> sameName = contactsByLastName.get(key);
		if (sameName != null) {
			sameName.remove(contact);
			if (sameName.isEmpty()) {
				contactsByLastName.remove(key); // Don't leave empty entries behind
			}
		}
	}
}