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
	return owner + ": " + content + "\n";
	else
		return content + "\n";
}

public String toString() {
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    return dateFormat.format(date) + " " + toSend();
}


}
