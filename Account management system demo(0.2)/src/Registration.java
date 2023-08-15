import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Registration extends JDialog{
    private JTextField UsernameField;
    private JTextField PasswordField;
    private JTextField NameField;
    private JTextField EmailField;
    private JButton registerButton;
    private JButton cancelButton;
    private JPanel registerPanel;

    public Registration(JFrame parent){
        super(parent);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450,474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String Username = UsernameField.getText();
        String Password = PasswordField.getText();

        String Name = NameField.getText();

        //Save first and last names to individual variables
        String[] SplitNames = Name.split(" ", 0);
        String Forename = SplitNames[0];
        String Surname = SplitNames[SplitNames.length - 1];

        String Email = EmailField.getText();

        user = addUserToDatabase(Username, Password, Forename, Surname, Email);
    }

    public User user;
    private User addUserToDatabase(String Username,String Password,String Forename,String Surname,String Email){
            User user = null;
            final String DB_URL = "jdbc:mysql://localhost:3306/projecttracker";
            final String USERNAME = "root";
            final String PASSWORD = "Cx11ved12";

            try{
                Connection conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);

                Statement stnt = conn.createStatement();
                String sql = "INSERT INTO users (Username,Password,Forename,Surname,Email)" +
                        "VALUES (?,?,?,?,?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, Username);
                preparedStatement.setString(2, Password);
                preparedStatement.setString(3, Forename);
                preparedStatement.setString(4, Surname);
                preparedStatement.setString(5, Email);

                //Insert rows to table
                int addedRows = preparedStatement.executeUpdate();
                if (addedRows > 0){
                    user = new User();
                    user.Username = Username;
                    user.Password = Password;
                    user.Forename = Forename;
                    user.Surname = Surname;
                    user.Email = Email;
                    user.isAdmin = false;
                }

                stnt.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return user;
        }

    public static void main(String[] args){

        Registration myForm = new Registration(null);

        User user = myForm.user;
        if (user != null){
            System.out.println("Successful registration of: " + user.Forename);
        }
        else{
            System.out.println("Registration cancelled");
        }
    }
}
