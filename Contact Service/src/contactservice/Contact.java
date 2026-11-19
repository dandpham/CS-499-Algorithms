/**
 * Name: Dan Pham
 * Course: CS-499 (enhanced from CS-320)
 * Description: Contact represents an individual contact with a unique ID, first name,
 * last name, phone number, and address.
 */
package contactservice;

public final class Contact {

	public static final int MAX_ID_LENGTH = 10;
	public static final int MAX_NAME_LENGTH = 10;
	public static final int MAX_ADDRESS_LENGTH = 30;
	private static final String PHONE_PATTERN = "\\d{10}"; // Exactly 10 digits

	// Unique contact ID that cannot be updated
	private final String contactId;

	private String firstName;
	private String lastName;
	private String phone;
	private String address;

	/**
	 * @param contactId Unique ID (non-null, max 10 characters, cannot be updated)
	 * @param firstName First name (non-null, max 10 characters)
	 * @param lastName  Last name (non-null, max 10 characters)
	 * @param phone     Phone number (non-null, exactly 10 digits)
	 * @param address   Address (non-null, max 30 characters)
	 */
	public Contact(String contactId, String firstName, String lastName, String phone, String address) {
		if (contactId == null || contactId.length() > MAX_ID_LENGTH) {
			throw new IllegalArgumentException("Invalid contact ID.");
		}
		this.contactId = contactId;
		this.firstName = validName(firstName, "first name");
		this.lastName = validName(lastName, "last name");
		this.phone = validPhone(phone);
		this.address = validAddress(address);
	}

	// Getters
	public String getContactId() {
		return contactId;
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

	// Setters (each one validates using the same rules as the constructor)
	public void setFirstName(String firstName) {
		this.firstName = validName(firstName, "first name");
	}

	/**
	 * Package-private on purpose: ContactService indexes contacts by last name,
	 * so only the service may change it. If outside code could change a last
	 * name directly, the index would point to the wrong name.
	 */
	void setLastName(String lastName) {
		this.lastName = validName(lastName, "last name");
	}

	public void setPhone(String phone) {
		this.phone = validPhone(phone);
	}

	public void setAddress(String address) {
		this.address = validAddress(address);
	}

	// Validation rules, written once and shared by the constructor and setters

	private static String validName(String name, String fieldName) {
		if (name == null || name.length() > MAX_NAME_LENGTH) {
			throw new IllegalArgumentException("Invalid " + fieldName + ".");
		}
		return name;
	}

	private static String validPhone(String phone) {
		if (phone == null || !phone.matches(PHONE_PATTERN)) {
			throw new IllegalArgumentException("Invalid phone number.");
		}
		return phone;
	}

	private static String validAddress(String address) {
		if (address == null || address.length() > MAX_ADDRESS_LENGTH) {
			throw new IllegalArgumentException("Invalid address.");
		}
		return address;
	}

	@Override
	public String toString() {
		return contactId + " (" + firstName + " " + lastName + ")";
	}
}