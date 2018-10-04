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
		executeCommand(s,"local");
	}
}

private static void allmessages() {
	for(ChatMessage cm : MainServer.allMessages) {
		System.out.println(cm.toString());
	}
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

public static boolean executeCommand(String command,String role) {
	switch(command) {
	case "stop" : MainServer.killServer(); return true;
	case "help" : System.out.println("not available"); return true;
	case "connections" : System.out.println(MainServer.allConnections.size()); return true;
	case "allmessages" : allmessages(); return true;
	case "save" : saveToLog(); return true;
	case "disconnect" : if(role=="local")System.out.println("client only"); return true;
	default : if(role=="local")System.out.println("unknown command"); return false;
	}
}
}
