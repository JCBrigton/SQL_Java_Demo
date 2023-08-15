import java.util.LinkedList;

public class User {
    int id;
    public String Username;
    public String Password;
    public String Forename;
    public String Surname;
    public String Email;
    public boolean isAdmin;

    LinkedList<User> us;

    public User(int id, String Username, String Password, String Forename, String Surname, String Email,boolean isAdmin, LinkedList<User> us) {
        this.id = id;
        this.Username = Username;
        this.Password = Password;
        this.Forename = Forename;
        this.Surname = Surname;
        this.Email = Email;
        this.isAdmin = isAdmin;
        this.us = us;
        us.add(this);
    }

    public User() {

    }
}
