import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

public class ServerThread extends Thread {
	public Socket connectionSocket;
	public String username;
	public PublicKey userpubkey;
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
			clientSentence=inFromClient.readLine();
			if(clientSentence.startsWith("#pubkey#")) {
				clientSentence=clientSentence.replaceAll("#pubkey#","");
				clientSentence=clientSentence.replaceAll("\n","");
				clientSentence=clientSentence.replaceAll("#n#","\n");
				KeyFactory kf = KeyFactory.getInstance("RSA");
				 //PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
			       // PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
					System.out.println(clientSentence);
			        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(clientSentence));
			userpubkey = kf.generatePublic(keySpecX509);
			//KeySpec x509Spec2 = new X509EncodedKeySpec(MainServer.pubKey.getEncoded());
			String serverkeyasstring = "#pubkey#" + MainServer.publicKeyToString(MainServer.pubKey);
			serverkeyasstring = serverkeyasstring.replaceAll("\n","#n+") + "\n";
			System.out.println(serverkeyasstring);
			outToClient.write(serverkeyasstring.getBytes("UTF8"));
				clientSentence=" uses a secure connection!";
			}
		   while(!clientSentence.equalsIgnoreCase("#disconnect#")) {
		   System.out.println("Received: " + clientSentence);
		   if(userpubkey!=null)System.out.println("pubkey gesetzt");
		   if(MainServer.useEncryption)System.out.println("server uses encryption");
		   if(clientSentence.startsWith("#encoded#"))System.out.println("encoded message");
		   if(userpubkey!=null&&MainServer.useEncryption&&clientSentence.startsWith("#encoded#")) {			//decrypt here
			   clientSentence=clientSentence.replaceAll("#encoded#", "");
			   clientSentence=clientSentence.replaceAll("\n", "");
			   clientSentence=clientSentence.replaceAll("#n#", "\n");
			   System.out.println("trying to decode");
		   clientSentence = new String(MainServer.decrypt(MainServer.privKey, clientSentence.getBytes()));
		   System.out.println(clientSentence);
		   }
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
				   
				   		String exec = ServerIO.executeCommand(command, me.role);
					   outToClient.write(new String(exec+"\n").getBytes("UTF8"));
				  
			   }
			   
		   }
		   
		   //first message!
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
		   MainServer.allConnections.remove(this);
		   System.out.println("Disc");
		   capitalizedSentence = username + " ist gegangen!";
		   MainServer.addMessage(new ChatMessage(capitalizedSentence,""));
		   
    }catch (Exception e){
        System.out.println(e.toString());
       
    }
    
}
}
