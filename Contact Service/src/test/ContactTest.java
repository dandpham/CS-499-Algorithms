/**
 * Name: Dan Pham
 * Course: CS-320
 * Date: 2/14/2025
 * Description: Contact class represents an individual contact with a unique ID, first name,
 * last name, phone number, and address. 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import contactservice.Contact;

public class ContactTest {

	@Test
	public void testSuccessfulCreation() {
		//Creates a new contact with valid data
		Contact contact = new Contact("1234567890", "John", "Doe", "1234567890", "123 Main Street");
		assertEquals("1234567890", contact.getContactId());
		assertEquals("John", contact.getFirstName());
		assertEquals("Doe", contact.getLastName());
		assertEquals("1234567890", contact.getPhone());
		assertEquals("123 Main Street", contact.getAddress());
	}

	@Test
	public void testInvalidContactId() {
		//Creates a new contact with invalid contact ID
		assertThrows(IllegalArgumentException.class, () -> new Contact(null, "John", "Doe", "1234567890", "123 Main Street"));
	}
	
	@Test
	public void testInvalidFirstName() {
		//Creates a new contact with invalid first name
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", null, "Doe", "1234567890", "123 Main Street"));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "Serendipity", "Doe", "1234567890", "123 Main Street"));
	}
	
	@Test
	public void testInvalidLastName() {
		//Creates a new contact with invalid last name
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", null, "1234567890", "123 Main Street"));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Higginbuthum", "1234567890", "123 Main Street"));
	}
	
	@Test
	public void testInvalidPhone() {
		//Creates a new contact with invalid phone number
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", null, "123 Main Street"));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", "12345678901", "123 Main Street"));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", "12345abcde", "123 Main Street"));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", "12345", "123 Main Street"));
	}
	
	@Test
	public void testInvalidAddress() {
		//Creates a new contact with invalid address
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", "1234567890", null));
		assertThrows(IllegalArgumentException.class, () -> new Contact("12345", "John", "Doe", "1234567890", "1234567890 Garden Grove Boulevard"));
		
	}
}
