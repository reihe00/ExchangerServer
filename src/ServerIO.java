import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ServerIO extends Thread {
@Override
public void run() {
	Scanner in = new Scanner(System.in);
	while(true) {
		
		String s = in. nextLine();
		if(s=="stop")in.close();
		String exec = executeCommand(s,"local");
		exec = exec.replaceAll("#newline#", "\n");
		exec = exec.replaceAll("#newmessage#", "\n");
		System.out.println(exec);
	}
}

private static String allmessages() {
	String ret ="";
	for(ChatMessage cm : MainServer.allMessages) {
		//System.out.println(cm.toString());
		ret+=cm.toChatHistory();
	}
	return ret;
}
private static String getDate() {
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    Date date = new Date();
    return dateFormat.format(date);
}

private static void saveToLog() {
	try {
	FileWriter fw = new FileWriter("exchangerlog"+getDate()+".log");
    BufferedWriter bw = new BufferedWriter(fw);
    for(ChatMessage m : MainServer.allMessages) {
    bw.write(m.toString());
    bw.newLine();
    
    }
    bw.close();
	}catch (Exception e) {
		e.printStackTrace();
	}
}

private static void saveRegisteredUsers() {
	try {
	FileWriter fw = new FileWriter("exchangerusers.snt");
    BufferedWriter bw = new BufferedWriter(fw);
    for(RegisteredUser m : MainServer.allRegisteredUsers) {
    bw.write(m.toString());
    bw.newLine();
    
    }
    bw.close();
	}catch (Exception e) {
		e.printStackTrace();
	}
}

public static String executeCommand(String command,String role) {
	switch(command) {
	case "stop" : MainServer.killServer(); return "Command executed";
	case "help" : return "currently not available";
	case "connections" : return String.valueOf(MainServer.allConnections.size());
	case "allmessages" : return allmessages();
	case "save" : saveRegisteredUsers(); return "saved";
	case "savelog" : saveToLog(); return "saved";
	case "disconnect" : if(role=="local")System.out.println("client only"); return "disconnected";
	default : return "unknown command: " + command;
	}
}
}
