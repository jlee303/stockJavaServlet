package jlee3900_CSCI201L_Assignment4_v2;

public class User {
	String email;
	String username;
	String password;

	public User(String u, String e, String p) {
		email = e;
		username = u;
		password = p;
	}

	String getEmail() {
		return email;
	}

	String getPassword() {
		return password;
	}

	String getUsername() {
		return username;
	}
}
