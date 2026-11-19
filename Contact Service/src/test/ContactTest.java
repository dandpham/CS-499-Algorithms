/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: JUnit test cases for the Contact class
 */
package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import contactservice.Contact;

public class ContactTest {

	private Contact validContact() {
		return new Contact("12345", "John", "Doe", "1234567890", "123 Main Street");
	}

	@Test
	public void testSuccessfulCreation() {
		Contact contact = new Contact("1234567890", "John", "Doe", "1234567890", "123 Main Street");
		assertEquals("1234567890", contact.getContactId());
		assertEquals("John", contact.getFirstName());
		assertEquals("Doe", contact.getLastName());
		assertEquals("1234567890", contact.getPhone());
		assertEquals("123 Main Street", contact.getAddress());
	}

	@Test
	public void testMaxLengthValuesAllowed() {
		// Boundary: 10-character ID and names, and a 30-character address
		assertDoesNotThrow(() -> new Contact("1234567890", "Abcdefghij", "Klmnopqrst", "1234567890",
				"123456789012345678901234567890"));
	}

	@Test
	public void testInvalidContactId() {
		assertThrows(IllegalArgumentException.class, () -> new Contact(null, "John", "Doe", "1234567890", "123 Main Street"));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345678901", "John", "Doe", "1234567890", "123 Main Street"));
	}

	@Test
	public void testInvalidFirstName() {
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", null, "Doe", "1234567890", "123 Main Street"));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "Serendipity", "Doe", "1234567890", "123 Main Street"));
	}

	@Test
	public void testInvalidLastName() {
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", null, "1234567890", "123 Main Street"));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Higginbuthum", "1234567890", "123 Main Street"));
	}

	@Test
	public void testInvalidPhone() {
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", null, "123 Main Street"));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", "12345678901", "123 Main Street")); // 11 digits
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", "12345abcde", "123 Main Street")); // Letters
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", "12345", "123 Main Street"));      // 5 digits
	}

	@Test
	public void testInvalidAddress() {
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", "1234567890", null));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", "1234567890", "1234567890 Garden Grove Boulevard"));
	}

	@Test
	public void testSetFirstName() {
		Contact contact = validContact();
		contact.setFirstName("Tracy");
		assertEquals("Tracy", contact.getFirstName());
		assertThrows(IllegalArgumentException.class, () -> contact.setFirstName(null));
		assertThrows(IllegalArgumentException.class, () -> contact.setFirstName("Serendipity"));
	}

	@Test
	public void testSetPhone() {
		Contact contact = validContact();
		contact.setPhone("9875643210");
		assertEquals("9875643210", contact.getPhone());
		assertThrows(IllegalArgumentException.class, () -> contact.setPhone(null));
		assertThrows(IllegalArgumentException.class, () -> contact.setPhone("12354678901"));
		assertThrows(IllegalArgumentException.class, () -> contact.setPhone("123456"));
		assertThrows(IllegalArgumentException.class, () -> contact.setPhone("1235abcde"));
	}

	@Test
	public void testSetAddress() {
		Contact contact = validContact();
		contact.setAddress("101 Beach Blvd");
		assertEquals("101 Beach Blvd", contact.getAddress());
		assertThrows(IllegalArgumentException.class, () -> contact.setAddress(null));
		assertThrows(IllegalArgumentException.class, () -> contact.setAddress("1234567890 Garden Grove Boulevard"));
	}

	@Test
	public void testFailedSetKeepsOldValue() {
		Contact contact = validContact();
		assertThrows(IllegalArgumentException.class, () -> contact.setPhone("bad"));
		assertEquals("1234567890", contact.getPhone());
	}
}