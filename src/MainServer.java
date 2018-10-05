

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;


public class MainServer {
	static ServerSocket welcomeSocket;
	static ArrayList<Socket> allConnections = new ArrayList<Socket>();
	static ArrayList<ChatMessage> allMessages = new ArrayList<ChatMessage>();
	static ArrayList<RegisteredUser> allRegisteredUsers = new ArrayList<RegisteredUser>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
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
			  allConnections.add(((ServerThread)st).connectionSocket);
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
		ArrayList<Socket> brokenones = new ArrayList<Socket>();
		for(Socket s : allConnections) {
		DataOutputStream outToClient;
		if(s.isConnected()&&!s.isClosed()) {
			try {
				outToClient = new DataOutputStream(s.getOutputStream());
				outToClient.write(message.toSend().getBytes("UTF8"));
				if(message.toSend().equalsIgnoreCase("#disconnect#"))s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				brokenones.add(s);
			}
			
		}
		   
		}
		for(Socket s : brokenones) {
			allConnections.remove(s);//jo
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

}
