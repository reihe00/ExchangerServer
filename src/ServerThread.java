import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread {
	public Socket connectionSocket;
	public String username;
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
		   if(clientSentence.equalsIgnoreCase("killserver")) {
			   MainServer.killServer();
		   }
		   //capitalizedSentence = clientSentence.toUpperCase() + "\n";
		   if(messagecount==0) {
		   capitalizedSentence = "Hallo " + clientSentence + "!\n";
		   MainServer.addMessage(capitalizedSentence);
		   }else {
			   capitalizedSentence = username + ": " + clientSentence + "\n";
			   MainServer.addMessage(capitalizedSentence);
		   }
		   clientSentence = inFromClient.readLine();
		   messagecount++;
		   }
		   connectionSocket.close();
		   MainServer.allConnections.remove(connectionSocket);
		   System.out.println("Disc");
		   capitalizedSentence = username + " ist gegangen!\n";
		   MainServer.addMessage(capitalizedSentence);
    }catch (Exception e){
        System.out.println(e.toString());
       
    }
    
}
}
