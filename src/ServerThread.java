import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread {
	public Socket connectionSocket;
	public String username;
	public String role;
@Override
public void run(){
	String clientSentence;
	  String capitalizedSentence;
	  int messagecount=0;
    try {
    	
    	System.out.println("accepting");
		   BufferedReader inFromClient =
		    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		   DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		  
		   clientSentence = inFromClient.readLine();
		   username=clientSentence;
		   while(!clientSentence.equalsIgnoreCase("#disconnect#")) {
		   System.out.println("Received: " + clientSentence);
		   if(role=="admin") {
			   if(clientSentence.startsWith("#")&&clientSentence.endsWith("#")) {
				   String command = clientSentence.replaceAll("#","");
				   
				   if(ServerIO.executeCommand(command, role)) {
					   outToClient.writeBytes(command + " executed!");
				   }else {
					   outToClient.writeBytes("Unknown command: " + command);
				   }
			   }
			   
		   }
		   if(clientSentence.equalsIgnoreCase("#connections#")) {
			  outToClient.writeBytes(String.valueOf(MainServer.allConnections.size()));
		   }
		   //capitalizedSentence = clientSentence.toUpperCase() + "\n";
		   if(messagecount==0) {
		   capitalizedSentence = "Hallo " + clientSentence + "!";
		   MainServer.addMessage(new ChatMessage(capitalizedSentence,""));
		   }else {
			   capitalizedSentence = clientSentence;
			   MainServer.addMessage(new ChatMessage(capitalizedSentence,username));
		   }
		   clientSentence = inFromClient.readLine();
		   messagecount++;
		   }
		   connectionSocket.close();
		   MainServer.allConnections.remove(connectionSocket);
		   System.out.println("Disc");
		   capitalizedSentence = username + " ist gegangen!";
		   MainServer.addMessage(new ChatMessage(capitalizedSentence,""));
    }catch (Exception e){
        System.out.println(e.toString());
       
    }
    
}
}
