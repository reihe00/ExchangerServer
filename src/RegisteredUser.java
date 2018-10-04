import java.util.Date;

public class RegisteredUser {
public String username;
public String role;
public String passhash;
public Date lastseen;
public RegisteredUser(String name, String rol, String password) {
	username=name;
	role=rol;
	passhash=password;
	lastseen=new Date();
}

public String toString() {
	return username+":"+role+":"+passhash;
}
}
