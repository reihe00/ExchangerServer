
public class RegisteredUser {
public String username;
public String role;
public String passhash;
public RegisteredUser(String name, String rol, String password) {
	username=name;
	role=rol;
	passhash=password;
}

public String toString() {
	return username+":"+role+":"+passhash;
}
}
