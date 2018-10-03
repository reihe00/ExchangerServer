

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MainServer {
	static ServerSocket welcomeSocket;
	static ArrayList<Socket> allConnections = new ArrayList<Socket>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String clientSentence;
		  String capitalizedSentence;
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
			welcomeSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public static void addMessage(String message) {
		for(Socket s : allConnections) {
		DataOutputStream outToClient;
		if(s.isConnected()&&!s.isClosed()) {
			try {
				outToClient = new DataOutputStream(s.getOutputStream());
				outToClient.writeBytes(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		   
		}
	}

}
