/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: JUnit test cases for ContactService, including last name
 * prefix search and keeping the last name index in sync
 */
package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import contactservice.Contact;
import contactservice.ContactService;

class ContactServiceTest {

	private ContactService service;

	@BeforeEach
	void setUp() {
		service = new ContactService();
	}

	/** Helper: a valid contact with the given ID and names */
	private Contact contact(String id, String firstName, String lastName) {
		return new Contact(id, firstName, lastName, "1234567890", "123 Main Street");
	}

	// ===== Add, get, and delete =====

	@Test
	void testAddAndGetContact() {
		Contact contact = contact("1", "John", "Doe");
		service.addContact(contact);
		assertSame(contact, service.getContact("1"));
		assertEquals(1, service.size());
	}

	@Test
	void testGetMissingContact() {
		assertNull(service.getContact("missing"));
	}

	@Test
	void testAddNullContact() {
		assertThrows(IllegalArgumentException.class, () -> service.addContact(null));
	}

	@Test
	void testAddDuplicateId() {
		service.addContact(contact("1", "John", "Doe"));
		assertThrows(IllegalArgumentException.class, () -> service.addContact(contact("1", "Jane", "Smith")));
	}

	@Test
	void testDeleteContact() {
		service.addContact(contact("1", "John", "Doe"));
		service.deleteContact("1");
		assertNull(service.getContact("1"));
		assertEquals(0, service.size());
	}

	@Test
	void testDeleteMissingContact() {
		assertThrows(IllegalArgumentException.class, () -> service.deleteContact("missing"));
	}

	@Test
	void testDeleteRemovesFromSearch() {
		// Proves the TreeMap index was updated too, not just the HashMap
		service.addContact(contact("1", "John", "Doe"));
		service.deleteContact("1");
		assertTrue(service.searchByLastName("Doe").isEmpty());
	}

	// ===== Update =====

	@Test
	void testUpdateContact() {
		service.addContact(contact("1", "John", "Doe"));
		service.updateContact("1", "Jane", "Doe", "9876543210", "321 Beach Blvd");

		Contact updated = service.getContact("1");
		assertEquals("Jane", updated.getFirstName());
		assertEquals("9876543210", updated.getPhone());
		assertEquals("321 Beach Blvd", updated.getAddress());
	}

	@Test
	void testUpdateMissingContact() {
		assertThrows(IllegalArgumentException.class,
				() -> service.updateContact("missing", "Jane", "Smith", "9876543210", "321 Beach Blvd"));
	}

	@Test
	void testUpdateWithInvalidValueChangesNothing() {
		// The original version skipped invalid values and still reported success.
		// Now one bad value rejects the whole update.
		service.addContact(contact("1", "John", "Doe"));

		assertThrows(IllegalArgumentException.class,
				() -> service.updateContact("1", "Jane", "Smith", "bad phone", "321 Beach Blvd"));

		Contact unchanged = service.getContact("1");
		assertEquals("John", unchanged.getFirstName());
		assertEquals("Doe", unchanged.getLastName());
		assertEquals(1, service.searchByLastName("Doe").size());
	}

	@Test
	void testUpdateLastNameMovesContactInIndex() {
		service.addContact(contact("1", "John", "Doe"));
		service.updateContact("1", "John", "Pham", "1234567890", "123 Main Street");

		assertTrue(service.searchByLastName("Doe").isEmpty());
		assertEquals("1", service.searchByLastName("Pham").get(0).getContactId());
	}

	// ===== Last name search =====

	@Test
	void testSearchByPrefixFindsAllMatches() {
		service.addContact(contact("1", "Dan", "Pham"));
		service.addContact(contact("2", "Mike", "Phelps"));
		service.addContact(contact("3", "Sara", "Smith"));

		List<Contact> result = service.searchByLastName("Ph");

		assertEquals(2, result.size());
		assertEquals("Pham", result.get(0).getLastName());   // Sorted by last name
		assertEquals("Phelps", result.get(1).getLastName());
	}

	@Test
	void testSearchIgnoresCapitalization() {
		service.addContact(contact("1", "Dan", "Pham"));
		assertEquals(1, service.searchByLastName("pH").size());
		assertEquals(1, service.searchByLastName("PHAM").size());
	}

	@Test
	void testSearchFullName() {
		service.addContact(contact("1", "Dan", "Pham"));
		service.addContact(contact("2", "Mike", "Phelps"));
		assertEquals(1, service.searchByLastName("Pham").size());
	}

	@Test
	void testSharedLastNameReturnsEveryone() {
		// Several contacts can share a last name; none may be overwritten
		service.addContact(contact("1", "John", "Smith"));
		service.addContact(contact("2", "Jane", "Smith"));
		service.addContact(contact("3", "Alex", "Smith"));

		assertEquals(3, service.searchByLastName("Smith").size());
	}

	@Test
	void testDeletingOneSharedNameKeepsTheOthers() {
		service.addContact(contact("1", "John", "Smith"));
		service.addContact(contact("2", "Jane", "Smith"));
		service.deleteContact("1");

		List<Contact> result = service.searchByLastName("Smith");
		assertEquals(1, result.size());
		assertEquals("2", result.get(0).getContactId());
	}

	@Test
	void testSearchNoMatches() {
		service.addContact(contact("1", "Dan", "Pham"));
		assertTrue(service.searchByLastName("Zz").isEmpty());
	}

	@Test
	void testSearchBlankOrNull() {
		assertThrows(IllegalArgumentException.class, () -> service.searchByLastName(null));
		assertThrows(IllegalArgumentException.class, () -> service.searchByLastName("   "));
	}

	@Test
	void testContactsSortedByLastName() {
		service.addContact(contact("1", "Sara", "Smith"));
		service.addContact(contact("2", "Al", "Adams"));
		service.addContact(contact("3", "Dan", "Pham"));

		List<Contact> sorted = service.getContactsSortedByLastName();

		assertEquals("Adams", sorted.get(0).getLastName());
		assertEquals("Pham", sorted.get(1).getLastName());
		assertEquals("Smith", sorted.get(2).getLastName());
	}

	@Test
	void testSearchWithManyContacts() {
		// 1,000 contacts; the search jumps straight to the matching section
		for (int i = 0; i < 1000; i++) {
			service.addContact(contact("C" + i, "Test", "Name" + i));
		}
		service.addContact(contact("X", "Dan", "Pham"));

		assertEquals(1, service.searchByLastName("Pha").size());
		assertEquals(1001, service.size());
	}
}