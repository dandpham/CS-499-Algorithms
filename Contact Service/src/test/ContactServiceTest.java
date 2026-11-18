/**
 * Name: Dan Pham
 * Course: CS-320
 * Date: 2/14/2025
 * Description: This class contains unit tests for the ContactService class. It verifies the functions
 * adding, deleting, and updating contacts.  
 */
package test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import contactservice.Contact;
import contactservice.ContactService;

class ContactServiceTest {

	@Test
	public void testGetContact() {
		ContactService service = new ContactService();
		Contact contact = new Contact("12345", "John", "Doe", "1234567890", "123 Main Street");
		Contact retrievedContact = service.getContact("12345");
	}
	
	@Test
	public void testAddContact() {
		ContactService service = new ContactService();
		Contact contact = new Contact("12345", "John", "Doe", "1234567890", "123 Main Street");
		assertTrue(service.addContact(contact));
		assertFalse(service.addContact(contact)); //Duplicate unique contact ID
	}
	
	@Test
	public void testDeleteContact() {
		ContactService service = new ContactService();
		Contact contact = new Contact("12345", "John", "Doe", "1234567890", "123 Main Street");
		service.addContact(contact);
		assertTrue(service.deleteContact("12345"));
		assertFalse(service.deleteContact("12345")); //Already deleted
	}
	
	@Test
	public void testUpdateNonexistentContact() {
		ContactService service = new ContactService();
		assertFalse(service.updateContact("00001", "Jane", "Smith", "9876543210", "321 Beach Blvd"));
	}
	
	@Test
	public void testUpdateFirstName() {
		Contact contact = new Contact("12345", "John", "Doe", "1234567890", "123 Main Street");
		contact.setFirstName("Tracy");
		assertEquals("Tracy", contact.getFirstName());
		assertThrows(IllegalArgumentException.class, () -> contact.setFirstName(null));
		assertThrows(IllegalArgumentException.class, () -> contact.setFirstName("Serendipity"));
	}
	
	@Test
	public void testUpdateLastName() {
		Contact contact = new Contact("12345", "John", "Doe", "1234567890", "123 Main Street");
		contact.setLastName("Smith");
		assertEquals("Smith", contact.getLastName());
		assertThrows(IllegalArgumentException.class, () -> contact.setLastName(null));
		assertThrows(IllegalArgumentException.class, () -> contact.setLastName("Higginbuthum"));
	}
	
	@Test 
	public void testUpdatePhone() {
		Contact contact = new Contact("12345", "John", "Doe", "1234567890", "123 Main Street");
		contact.setPhone("9875643210");
		assertEquals("9875643210", contact.getPhone());
		assertThrows(IllegalArgumentException.class, () -> contact.setPhone(null));
		assertThrows(IllegalArgumentException.class, () -> contact.setPhone("12354678901"));
		assertThrows(IllegalArgumentException.class, () -> contact.setPhone("123456"));
		assertThrows(IllegalArgumentException.class, () -> contact.setPhone("1235abcde"));
	}
	
	@Test
	public void testUpdateAddress() {
		Contact contact = new Contact("12345", "John", "Doe", "1234567890", "123 Main Street");
		contact.setAddress("101 Beach Blvd");
		assertEquals("101 Beach Blvd", contact.getAddress());
		assertThrows(IllegalArgumentException.class, () -> contact.setAddress(null));
		assertThrows(IllegalArgumentException.class, () -> contact.setAddress("1234567890 Garden Grove Boulevard"));
	}
	
	@Test
	public void testUpdateContact() {
		ContactService service = new ContactService();
		Contact contact = new Contact("12345", "John", "Doe", "1234567890", "123 Main Street");
		service.addContact(contact);
		
		assertTrue(service.updateContact("12345", "Jane", "Doe", "1234567890", "123 Main Street"));
		Contact updatedContact = service.getContact("12345");
		assertEquals("Jane", updatedContact.getFirstName()); 
	}
}
