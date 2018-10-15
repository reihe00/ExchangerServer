import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

import java.security.PublicKey;

import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class ServerThread extends Thread {
	public Socket connectionSocket;
	public String username;
	public PublicKey userpubkey;
	public RegisteredUser me;
	KeyGenerator generator;
	
	public SecretKey secKey;
	
@Override
public void run(){
	String clientSentence;
	  String capitalizedSentence;
	  int messagecount=0;
	  try {
		generator = KeyGenerator.getInstance("AES");
		generator.init(128); // The AES key size in number of bits
		  secKey = generator.generateKey();
	} catch (NoSuchAlgorithmException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  
	  
    try {
    	
    	System.out.println("accepting");
		   BufferedReader inFromClient =
		    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(),"UTF8"));
		   
		   DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		   
		   //keyexchange
		   clientSentence=inFromClient.readLine();
			if(clientSentence.startsWith("#pubkey#")) {
				clientSentence=clientSentence.replaceAll("#pubkey#","");
				clientSentence=clientSentence.replaceAll("\n","");

				clientSentence=clientSentence.replaceAll("#n#","\n");

				KeyFactory kf = KeyFactory.getInstance("RSA");
				 
			        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(clientSentence));
			userpubkey = kf.generatePublic(keySpecX509);
			
			String serverkeyasstring = "#pubkey#" + MainServer.publicKeyToString(MainServer.pubKey);
			serverkeyasstring = serverkeyasstring.replaceAll("\n","#n#") + "\n";

			
           
			outToClient.write(serverkeyasstring.getBytes("UTF8"));
			
			
			serverkeyasstring = "#aeskey#" + toHexString(encryptedAESKey(userpubkey));
			
			serverkeyasstring = serverkeyasstring.replaceAll("\n","#n#") + "\n";
			outToClient.write(serverkeyasstring.getBytes("UTF8"));
			
			//login secure
			clientSentence = inFromClient.readLine();
			clientSentence=decode(clientSentence);
			username=clientSentence;
			clientSentence = inFromClient.readLine();
			clientSentence = decode(clientSentence);
			me= MainServer.loginUser(username, clientSentence); 
			if(me==null){
				clientSentence="#disconnect#";
				outToClient.write(new String(clientSentence + "\n").getBytes("UTF8"));
			}else {
				if(me.lastseen!=null)
					System.out.println(me.lastseen);
			}
			
				clientSentence=" uses a secure connection!";
			}
			else {
				//login insecure
				username=clientSentence;
				clientSentence = inFromClient.readLine();
				me= MainServer.loginUser(username, clientSentence); 
				if(me==null){
					clientSentence="#disconnect#";
					outToClient.write(new String(clientSentence + "\n").getBytes("UTF8"));
				}else {
					if(me.lastseen!=null)
						System.out.println(me.lastseen);
				}
			}
			
		   while(!clientSentence.equalsIgnoreCase("#disconnect#")) {
		  
		   
		   
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
				   		
				   	 if(MainServer.useEncryption&&(userpubkey!=null)) {
							String enc ="#encoded#"+Base64.getEncoder().encodeToString(MainServer.encryptAES(secKey,exec));
							enc=enc.replaceAll("\n", "#n#");
					enc+="\n";
							outToClient.write(enc.getBytes(("UTF8")));
						}else {
					   outToClient.write(new String(exec+"\n").getBytes("UTF8"));
						}
				   	 sleep(250);
			   }
			   
		   }
		   
		   //first message!
		   if(messagecount==0) {
			   sleep(250);
			   System.out.println("sending chat-history for " + username);
			   String chathistory = "";
			   for(ChatMessage cm : MainServer.allMessagesSince(me.lastseen)) {
				   chathistory+=cm.toChatHistory();
			   
			   }
			   System.out.println(chathistory);
			   if(chathistory.length()>2)
				   if(MainServer.useEncryption&&(userpubkey!=null)) {
						String enc ="#encoded#"+Base64.getEncoder().encodeToString(MainServer.encryptAES(secKey,chathistory));
						enc=enc.replaceAll("\n", "#n#");
				enc+="\n";
						outToClient.write(enc.getBytes(("UTF8")));
					}else {
					outToClient.write(chathistory.getBytes("UTF8"));
					}
			   sleep(250);
		   capitalizedSentence = "Hallo " + username + "!";
		   MainServer.addMessage(new ChatMessage(capitalizedSentence,""));
		   }else {
			   capitalizedSentence = clientSentence;
			   MainServer.addMessage(new ChatMessage(capitalizedSentence,username));
		   }
		   clientSentence = inFromClient.readLine();
		   
		   clientSentence = decode(clientSentence);
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

public byte[] encryptedAESKey(PublicKey userKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	cipher.init(Cipher.PUBLIC_KEY, userKey);
	byte[] encryptedKey = cipher.doFinal(secKey.getEncoded()/*Seceret Key From Step 1*/);
	
	//System.out.println(hexStringToByteArray(new String(encryptedKey)).length);
	
	return encryptedKey;
}

public static String toHexString(byte[] array) {
    return DatatypeConverter.printHexBinary(array);
}

public static byte[] toByteArray(String s) {
    return DatatypeConverter.parseHexBinary(s);
}

private String decode(String clientSentence) {
if(userpubkey!=null&&MainServer.useEncryption&&clientSentence.startsWith("#encoded#")) {			//decrypt here
	   clientSentence=clientSentence.replaceAll("#encoded#", "");
	   clientSentence=clientSentence.replaceAll("\n", "");
	   clientSentence=clientSentence.replaceAll("#n#", "\n");
	   System.out.println("trying to decode: " + clientSentence);
	   if(clientSentence.length()>1)
		try {
			clientSentence = new String(MainServer.decryptAES(secKey, Base64.getDecoder().decode(clientSentence)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return clientSentence;
}
return clientSentence;

}
}
