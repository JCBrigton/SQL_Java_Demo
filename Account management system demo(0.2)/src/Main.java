//Import libraries
import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

//Main class
public class Main {

    //List declarations
    public static LinkedList<User> us = new LinkedList<>(); //Initialise linked list "User"

    //Global variables
    public static int currentUser = 0; //Public access to current user ID variable

    //METHODS//

    //Email validation
    public static boolean patternMatches(String Email) { //Receive Email address
        String regexPattern = "^(.+)@(S+)$"; //Check for presence of the characters stored here
        return Pattern.compile(regexPattern)//Return output of function
                .matcher(Email)
                .matches(); //Boolean output
    }

    //Main
    public static void main(String[] args) throws IOException, SQLException {

        //Create scanner for input
        Scanner newInput = new Scanner(System.in);

        //Read user database
        //Establish SQL connection
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/projecttracker","root","Cx11ved12");
        Statement statement = connection.createStatement();

        //Save results
        ResultSet resultSet = statement.executeQuery("select * from users");

        //Add entries to list
        while (resultSet.next()){
            new User(resultSet.getInt("ID"), resultSet.getString("Username"), resultSet.getString("Password"), resultSet.getString("Forename"), resultSet.getString("Surname"), resultSet.getString("Email"), resultSet.getBoolean("isAdmin"), us);
        }

        //Program main loop
        boolean running = true;
        while(running) {

            //HOME MENU
            boolean loggedIn = false;
            while (!loggedIn) {

                //Menu text to terminal
                System.out.println("Account system Demo");
                System.out.println("--------------------");
                System.out.println("Select menu option...");
                System.out.println("1.Log in");
                System.out.println("2.Register");
                System.out.println("3.Exit");

                //Get main menu input from terminal
                String inputMain = newInput.nextLine();

                //1.Log in
                if (inputMain.equals("1")) {

                    //While attempting to log in
                    boolean logAttempt = true;
                    while (logAttempt) { //Loop for allowing retry

                        //Retrieve Username
                        System.out.println("Enter username");
                        String Username = newInput.nextLine();

                        //Retrieve Password
                        System.out.println("Enter password");
                        String Password = newInput.nextLine();

                        //Check list for match
                        for (int i = 0; i < us.size(); i++) { //Repeat for the length of the list
                            if (Username.equals(us.get(i).Username)) { //Compare usernames
                                if (Password.equals(us.get(i).Password)) { //If found compare passwords

                                    //Log in successful
                                    System.out.println("Log in successful.");
                                    currentUser = us.get(i).id; //Set global ID variable
                                    loggedIn = true;
                                    logAttempt = false;
                                    break;
                                }

                                else {
                                    //If password is incorrect
                                    System.out.println("Password is incorrect.");
                                }
                            }
                        }
                        //Log in failed
                        if (!loggedIn) {
                            System.out.println("Log in failed.");
                            System.out.println("Would you like to try again? [Y/N]");

                            //Check for retry request
                            String inputTry = newInput.nextLine();
                            if (inputTry.equals("N") || inputTry.equals("n")) {
                                logAttempt = false;
                            }
                        }
                    }
                }

                //2.Register new User
                else if (inputMain.equals("2")) {

                    //Variables for each authentication issue
                    //*Array could be used
                    boolean validUser = true;
                    boolean UNTaken = false;
                    boolean PWStrong = true;
                    boolean ValidEmail = true;

                    //Retrieve Username
                    System.out.println("Enter username");
                    String Username = newInput.nextLine();

                    //Check availability
                    for (User u : us) {
                        if (Username.equals(u.Username)) { //Search user list for username match

                            //Apply authentication update
                            validUser = false;
                            UNTaken = true;
                            break;
                        }
                    }

                    //Retrieve Password
                    System.out.println("Enter password\n(5+ characters, at least one number)");
                    String Password = newInput.nextLine();

                    //Check strength
                    if (Password.length() > 5){ //If longer than 5 characters

                        //Search password string for integer number
                        for(int i=0; i < Password.length(); i++) {
                            boolean flag = Character.isDigit(Password.charAt(i));
                            if (!flag){
                                break;
                            }

                            //Update authentication
                            else{
                                validUser = false;
                                PWStrong = false;
                            }
                        }
                    }
                    else{
                        validUser = false;
                    }

                    //Retrieve Name
                    System.out.println("Enter full name");
                    String Name = newInput.nextLine();

                    //Save first and last names to individual variables
                    String[] SplitNames = Name.split(" ", 0);
                    String Forename = SplitNames[0];
                    String Surname = SplitNames[SplitNames.length - 1];

                    //Retrieve Email address
                    System.out.println("Enter email address");
                    String Email = newInput.nextLine();

                    //Authenticate Email address using method
                    boolean checkValid = !patternMatches(Email);
                    if (!checkValid){
                        validUser = false;
                        ValidEmail = false;
                    }

                    //Add valid user
                    if (validUser) {

                        //Generate ID
                        int id = us.size();
                        System.out.println("Your account ID number is: " + id);

                        //Create user listing
                        User newUser = new User(id, Username, Password, Forename, Surname, Email, false, us);

                        //Write to user database
                        statement.executeUpdate("INSERT INTO users " + "VALUES (" + id + ", '" + Username + "', '" + Password + "', '" + Forename + "', '" + Surname + "', '" + Email + "', false)");
                    }

                    //Invalid user
                    //Output authentication issue to terminal
                    else{
                        System.out.println("User details invalid.");
                        if(UNTaken){ //If username is taken
                            System.out.println("Username unavailable.");
                        }
                        if(!PWStrong){ //If password doesn't fit criteria
                            System.out.println("Password too weak.");
                        }
                        if(!ValidEmail){ //If regex character not present in email address
                            System.out.println("Invalid Email address provided.");
                        }
                    }
                }

                //3.Exit
                else if (inputMain.equals("3")) {
                    running = false;
                    break;
                }

                //USER MENU
                if (loggedIn) { //If a user is logged into the program
                    while (loggedIn) {

                        //Dashboard
                        System.out.println("Dashboard for user " + us.get(currentUser).Username); //Display username
                        System.out.println("1.Log out");
                        if (((User) us.get(currentUser)).isAdmin){
                            System.out.println("2.Manage users");
                        }

                        //Receive menu selection
                        String inputHome = newInput.nextLine();

                        //3.Log out
                        if (inputHome.equals("1")){
                            loggedIn = false;
                            break;
                        }

                        //Admin option
                        if (((User) us.get(currentUser)).isAdmin) {
                            int count = 1;

                            //Manage users
                            if (inputHome.equals("2")) {

                                //Display list of users
                                resultSet = statement.executeQuery("select * from users");
                                while (resultSet.next()) {
                                    System.out.println(count + "." + resultSet.getString("Forename") + " " + resultSet.getString("Surname"));
                                    count += 1;
                                }
                                System.out.println((count) + ". Back");
                            }

                            //Get menu selection
                            inputHome = newInput.nextLine();

                            //Convert to int for list length scaling
                            int inputNum = Integer.parseInt(inputHome) - 1;

                            // If user selected
                            if (inputNum < count - 1) {
                                //Show user details
                                System.out.println(((User) us.get(inputNum)).Forename + " " + ((User) us.get(inputNum)).Surname);
                                System.out.println("1. Delete user");
                                System.out.println("2. Change admin status");
                                System.out.println("3. Back");

                                //Get menu selection
                                inputHome = newInput.nextLine();

                                //Delete user
                                if (inputHome.equals("1")) {
                                    //Delete database entry
                                    String query = "delete from users where id = ?";
                                    PreparedStatement preparedStmt = connection.prepareStatement(query);
                                    int idInt = ((User) us.get(inputNum)).id;
                                    preparedStmt.setInt(1, idInt);

                                    // execute the prepared statement
                                    preparedStmt.execute();

                                    //Delete from local list
                                    us.clear();
                                    //Add entries to list
                                    resultSet = statement.executeQuery("select * from users");
                                    while (resultSet.next()) {
                                        new User(resultSet.getInt("ID"), resultSet.getString("Username"), resultSet.getString("Password"), resultSet.getString("Forename"), resultSet.getString("Surname"), resultSet.getString("Email"), resultSet.getBoolean("isAdmin"), us);
                                    }
                                }

                                //Change admin status
                                if (inputHome.equals("2")) {

                                    int idInt = ((User) us.get(inputNum)).id;

                                    String sqlUpdate = "update users "
                                        + "SET isAdmin = ? "
                                        + "WHERE id = ?";

                                    Boolean setAdmin = !((User) us.get(inputNum)).isAdmin;

                                    PreparedStatement pstmt = connection.prepareStatement(sqlUpdate);
                                    pstmt.setBoolean(1, setAdmin);
                                    pstmt.setInt(2, idInt);

                                    pstmt.executeUpdate();

                                    pstmt.close();
                                }

                            } else
                            {
                                continue;
                            }
                        }
                    }
                }
            }
        }
    }
}
