import java.io.Serializable;

public class Message implements Serializable{
	
	protected static final long serialVersionUID = 1121222200L;
	
	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
	private int type;
	private String message;
	
	public Message(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	int getType() {
		return type;
	}
	
	String getMessage() {
		return message;
	}
	
}
