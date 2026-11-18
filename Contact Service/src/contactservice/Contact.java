/**
 * Name: Dan Pham
 * Course: CS-320
 * Date: 2/14/2025
 * Description: Contact class represents an individual contact with a unique ID, first name,
 * last name, phone number, and address. 
 */
package contactservice;


public class Contact {
	//Unique contact ID that connect be updated 
	private final String id;
	
	//Contact information (first name, last name, phone number, address)
	private String firstName;
	private String lastName;
	private String phone;
	private String address;
	
	/**
	 * 
	 * @param id Unique ID (non-null, max 10 characters, cannot be updated)
	 * @param firstName First name (non-null, max 10 characters)
	 * @param lastName Last name (non-null, max 10 characters)
	 * @param phone Phone number (non-null, exactly 10 digits)
	 * @param address Address (non-null, max 30 characters)
	 */
	public Contact(String id, String firstName, String lastName, String phone, String address) {
		if (id == null || id.length() > 10) {
			throw new IllegalArgumentException("invalid contact ID");
		}
		if (firstName == null || firstName.length() > 10) {
			throw new IllegalArgumentException("invalid first name");
		}
		if (lastName == null || lastName.length() > 10) {
			throw new IllegalArgumentException("invalid last name");
		}
		if (phone == null || phone.length() != 10 || !phone.matches("\\d+")) {
			throw new IllegalArgumentException("invalid phone number");
		}
		if (address == null || address.length() > 30) {
			throw new IllegalArgumentException("invalid address");
		}
		
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.address = address;
	} 
	
	//Getters
	public String getContactId() {
		return id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getPhone() {
		return phone;
	}
	public String getAddress() {
		return address;
	}
	
	//Setters
	public void setFirstName(String firstName) {
		if (firstName == null || firstName.length() > 10 ) {
			throw new IllegalArgumentException("invalid first name");
		}
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		if (lastName == null || lastName.length() > 10 ) {
			throw new IllegalArgumentException("invalid last name");
		}
		this.lastName = lastName;
	}
	
	public void setPhone(String phone) {
		if (phone == null || !phone.matches("\\d{10}")) {
			throw new IllegalArgumentException("invalid phone number");
		}
		this.phone = phone;
	}
	
	public void setAddress(String address) {
		if (address == null || address.length() > 30 ) {
			throw new IllegalArgumentException("invalid address");
		}
		this.address = address;
	}
	
}
