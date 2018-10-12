import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {
public String content;
public String owner;
public Date when;
public ChatMessage(String what,String from) {
	content=what;
	owner=from;
	when=new Date();
}

public String toSend() {
	
	if(owner.length()>1)
	return owner + ": " + content;
	else
		return content;
	
}

public String toString() {
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    return dateFormat.format(date) + " " + toSend();
}

public String toChatHistory() {
	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	String timewhen = dateFormat.format(when) + " ";
	if(owner.length()>1)
		return timewhen + owner + ": " + content + "#newmessage#";
		else
			return timewhen + content + "#newmessage#";
}


}
