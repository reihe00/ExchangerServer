import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {
public String content;
public String owner;
public String target;
public Date when;
public ChatMessage(String what,String from) {
	content=what;
	owner=from;
	when=new Date();
	target="";
}

public ChatMessage(String what,String from, String to) {
	content=what;
	owner=from;
	when=new Date();
	target=to;
}

public String toSend() {
	if(target=="") {
	if(owner.length()>1)
	return owner + ": " + content;
	else
		return content;
	}else {
		if(owner.length()>1)
			return "#from#" + owner + ": " + content;
			else
				return content;
	}
	
}

public String toString() {
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    return dateFormat.format(date) + " " + toSend();
}

public String toChatHistory() {
	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	String timewhen = dateFormat.format(when) + " ";
	if(target=="") {
		if(owner.length()>1)
		return timewhen + owner + ": " + content + "#newmessage#";
		else
			return timewhen + content + "#newmessage#";
		}else {
	
	if(owner.length()>1)
		return "#from#" + owner + ": " + content + "#newmessage#";
		else
			return timewhen + content + "#newmessage#";
		}
}


}
