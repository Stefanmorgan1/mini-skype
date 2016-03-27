import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class LoginGUI extends JFrame{

	JTextField usernameTextField;
	JPasswordField passwordTextField;
	JButton loginButton;
	JPanel panel;
	public LoginGUI() {
		
	}
	
	private void initializeComponents() {
		usernameTextField = new JTextField();
		passwordTextField = new JPasswordField();
		loginButton = new JButton("Login");
		panel = new JPanel(new MigLayout());
	}
}
