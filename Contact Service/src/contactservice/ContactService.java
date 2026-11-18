/**
 * Name: Dan Pham
 * Course: CS-320
 * Date: 2/14/2025
 * Description: ContactService class manages contact objects by storing them. Also, allows contacts to be 
 * added, updated, and deleted based on their unique contact ID. 
 */
package contactservice;

import java.util.HashMap;
import java.util.Map;

public class ContactService {
	// A hashmap to store contacts with their unique contact ID as the key
	private final Map<String, Contact> contacts;
	
	//Constructor to initialize contact storage 
	public ContactService() {
		this.contacts = new HashMap<>();
	}
	
	/**
	 * Adds a new contact if the ID is unique
	 * @param contact Contact object to be added
	 * @return true if contact was successfully added, false if ID already exists  
	 */
	public boolean addContact(Contact contact) {
		if (contacts.containsKey(contact.getContactId())) {
			return false; //Contact ID must be unique
		}
		contacts.put((contact.getContactId()), contact);
			return true;
		}
		
	public boolean deleteContact(String id) {
		return contacts.remove(id) != null;
	}
		
	/**
	 * Updates an existing contact's information except for the contact ID
	 * 
	 * @param id Unique ID (non-null, max 10 characters, cannot be updated)
	 * @param firstName First name (non-null, max 10 characters)
	 * @param lastName Last name (non-null, max 10 characters)
	 * @param phone Phone number (non-null, exactly 10 digits)
	 * @param address Address (non-null, max 30 characters)
	 * @return true if contact was successfully updated, false if contact was not found
	 */
	public boolean updateContact(String id, String firstName, String lastName, String phone, String address) {
		//Retrieves contact 
		Contact contact = contacts.get(id);
		if (contact == null) {
			return false; //contact not found
		}
		//Updates first name 
		if (firstName != null && firstName.length() <= 10) {
			contact.setFirstName(firstName); 
		}
		//Updates last name
		if (lastName != null && lastName.length() <= 10) {
			contact.setLastName(lastName);
		}
		//Updates phone number
		if (phone != null && phone.matches("\\d{10}")) {
			contact.setPhone(phone);
		}
		//Updates address
		if (address != null && address.length() <= 30) {
			contact.setAddress(address);
		}
		return true; //Update successful
	}
		
		public Contact getContact(String id) {
			return contacts.get(id);
		}

}
