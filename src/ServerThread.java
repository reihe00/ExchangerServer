import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

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
		    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(),"UTF8"));
		   
		   DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		  
		   clientSentence = inFromClient.readLine();
		   username=clientSentence;
		   clientSentence = inFromClient.readLine();
		 me= MainServer.loginUser(username, clientSentence); 
			if(me==null){
			   clientSentence="#disconnect#";
			   outToClient.write(new String(clientSentence + "\n").getBytes("UTF8"));
		   }else {
			   
		   }
		   while(!clientSentence.equalsIgnoreCase("#disconnect#")) {
		   System.out.println("Received: " + clientSentence);
		   if(clientSentence.equalsIgnoreCase("#connections#")) {
				  outToClient.write(new String(String.valueOf(MainServer.allConnections.size())+"\n").getBytes("UTF8"));
			   }
			   if(clientSentence.equalsIgnoreCase("#role#")) {
					  outToClient.write(new String(me.role + "\n").getBytes("UTF8"));
				   }
		   if(me.role.equalsIgnoreCase("admin")) {
			   //System.out.println("admin says " + clientSentence);
			   if(clientSentence.startsWith("#")&&clientSentence.endsWith("#")) {
				   String command = clientSentence.replaceAll("#","");
				   
				   		String exec = ServerIO.executeCommand(command, role);
					   outToClient.write(new String(exec+"\n").getBytes("UTF8"));
				  
			   }
			   
		   }
		   
		   //capitalizedSentence = clientSentence.toUpperCase() + "\n";
		   if(messagecount==0) {
			   String chathistory = "";
			   for(ChatMessage cm : MainServer.allMessagesSince(me.lastseen)) {
				   chathistory+=cm.toChatHistory();
			   
			   }
			   if(chathistory.length()>2)
			   outToClient.write(new String(chathistory + "\n").getBytes("UTF8"));
			   sleep(60);
		   capitalizedSentence = "Hallo " + username + "!";
		   MainServer.addMessage(new ChatMessage(capitalizedSentence,""));
		   }else {
			   capitalizedSentence = clientSentence;
			   MainServer.addMessage(new ChatMessage(capitalizedSentence,username));
		   }
		   clientSentence = inFromClient.readLine();
		   messagecount++;
		   }
		   if(me!=null)
		   me.lastseen=new Date();
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
