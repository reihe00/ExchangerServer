import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread {
	public Socket connectionSocket;
	public String username;
	public String role;
	public RegisteredUser me;
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
		   clientSentence = inFromClient.readLine();
		 me= MainServer.loginUser(username, clientSentence); 
			if(me==null){
			   clientSentence="#disconnect#";
			   outToClient.writeBytes(clientSentence + "\n");
		   }else {
			   
		   }
		   while(!clientSentence.equalsIgnoreCase("#disconnect#")) {
		   System.out.println("Received: " + clientSentence);
		   if(me.role.equalsIgnoreCase("admin")) {
			   System.out.println("admin says " + clientSentence);
			   if(clientSentence.startsWith("#")&&clientSentence.endsWith("#")) {
				   String command = clientSentence.replaceAll("#","");
				   
				   if(ServerIO.executeCommand(command, me.role)) {
					   outToClient.writeBytes(command + " executed!\n");
				   }else {
					   outToClient.writeBytes("Unknown command: " + command+"\n");
				   }
			   }
			   
		   }
		   if(clientSentence.equalsIgnoreCase("#connections#")) {
			  outToClient.writeBytes(String.valueOf(MainServer.allConnections.size()));
		   }
		   if(clientSentence.equalsIgnoreCase("#role#")) {
				  outToClient.writeBytes(me.role + "\n");
			   }
		   //capitalizedSentence = clientSentence.toUpperCase() + "\n";
		   if(messagecount==0) {
		   capitalizedSentence = "Hallo " + username + "!";
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
