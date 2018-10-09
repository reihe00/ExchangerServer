
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import java.util.Base64;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;


public class MainServer {
	static ServerSocket welcomeSocket;
	static ArrayList<ServerThread> allConnections = new ArrayList<ServerThread>();
	static ArrayList<ChatMessage> allMessages = new ArrayList<ChatMessage>();
	static ArrayList<RegisteredUser> allRegisteredUsers = new ArrayList<RegisteredUser>();
	static boolean useEncryption=false;
	static KeyPair keyPair;
	static PublicKey pubKey;
	static PrivateKey privKey;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		try {
			keyPair = buildKeyPair();
	        pubKey = keyPair.getPublic();
	        privKey = keyPair.getPrivate();
			if(args[0].equalsIgnoreCase("true")) {
				useEncryption=true;
				// generate public and private keys
		        
			}else {
				useEncryption=false;
				System.out.println(args[0]);
			}
		}catch(Exception e) {
			System.out.println("WARNING! Running in insecure mode!");
			useEncryption=false;
		}
		try {
		FileReader fw = new FileReader("exchangerusers.snt");
		
	    BufferedReader bw = new BufferedReader(fw);
	    String s = bw.readLine();
	    while(s.length()>3) {
	    	try {
	    	System.out.println(s);
	    	String[] sa = s.split(":");
	    	allRegisteredUsers.add(new RegisteredUser(sa[0],sa[1],sa[2]));
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	s=bw.readLine();
	    }
	    bw.close();
	    fw.close();
		}catch(Exception e) {
			System.out.println("exchangerusers.snt konnte nicht geöffnet werden");
		}
		
		Thread myIO = new ServerIO();
		myIO.start();
		  welcomeSocket = new ServerSocket(21245);

		  while (true) {
			  Thread st = new ServerThread();
			  ((ServerThread)st).connectionSocket=welcomeSocket.accept();
			  allConnections.add(((ServerThread)st));
			  st.start();
		  
		  }
	}
	
	public static void killServer() {
		try {
			addMessage(new ChatMessage("#disconnect#",""));
			welcomeSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public static void addMessage(ChatMessage message) {
		allMessages.add(message);
		ArrayList<ServerThread> brokenones = new ArrayList<ServerThread>();
		for(ServerThread st : allConnections) {
			Socket s = st.connectionSocket;
		DataOutputStream outToClient;
		if(s.isConnected()&&!s.isClosed()) {
			try {
				outToClient = new DataOutputStream(s.getOutputStream());
				if(useEncryption&&(st.userpubkey!=null)) {
					String enc ="#encoded#"+new String(encrypt(st.userpubkey,message.toSend()),"UTF8");
					enc=enc.replaceAll("\n", "#n#");
			enc+="\n";
					outToClient.write(enc.getBytes(("UTF8")));
				}else {
				outToClient.write(message.toSend().getBytes("UTF8"));
				if(message.toSend().equalsIgnoreCase("#disconnect#"))s.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				brokenones.add(st);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else {
			brokenones.add(st);
		}
		   
		}
		for(ServerThread s : brokenones) {
			allConnections.remove(s);//jo
			System.out.println(s.toString() + " removed");
		}
	}
	
	public static RegisteredUser loginUser(String uname,String pass) {
		
		for(RegisteredUser r : allRegisteredUsers) {
			if(r.username.equalsIgnoreCase(uname)) {
				if(pass.contains(r.passhash)) {
					System.out.println(uname + " logged in");
					return r;
				}else {
					System.out.println(uname + " used wrong password");
					return null;
				}
			}
		}
		RegisteredUser ret = new RegisteredUser(uname,"user",pass);
		allRegisteredUsers.add(ret);
		return ret;
	}
	
	public static ArrayList<ChatMessage> allMessagesSince(Date when) {
		ArrayList<ChatMessage> ret = new ArrayList<ChatMessage>();
		for(ChatMessage cm : allMessages) {
			if(cm.when.after(when)){
				ret.add(cm);
			}
		}
		return ret;
	}
	
	 public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
	        final int keySize = 2048;
	        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	        keyPairGenerator.initialize(keySize);      
	        return keyPairGenerator.genKeyPair();
	    }

	    public static byte[] encrypt(PublicKey publicKey, String message) throws Exception {
	        Cipher cipher = Cipher.getInstance("RSA");  
	        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  

	        return cipher.doFinal(message.getBytes());  
	    }
	    
	    public static byte[] decrypt(PrivateKey privateKey, byte [] encrypted) throws Exception {
	        Cipher cipher = Cipher.getInstance("RSA");  
	        cipher.init(Cipher.DECRYPT_MODE, privateKey);
	        
	        return cipher.doFinal(encrypted);
	}
	    
	    public static String publicKeyToString(PublicKey p) {

	        byte[] publicKeyBytes = p.getEncoded();
	        
	        return Base64.getEncoder().encodeToString(publicKeyBytes);

	    }

}
