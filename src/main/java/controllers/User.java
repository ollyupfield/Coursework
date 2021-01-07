package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

@Path("user/")
public class User {

    @POST
    @Path("login")
    public String loginUser(@FormDataParam("email") String email, @FormDataParam("password") String password) {
        System.out.println("Invoked User.login");
        try {
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT Password FROM Users WHERE Email = ?");
            ps1.setString(1, email);
            ResultSet loginResults = ps1.executeQuery();
            if (loginResults.next() == true) {
                String correctPassword = loginResults.getString(1);
                if (password.equals(correctPassword)) {
                    String token = UUID.randomUUID().toString();
                    PreparedStatement ps2 = Main.db.prepareStatement("UPDATE Users SET Token = ? WHERE Email = ?");
                    ps2.setString(1, token);
                    ps2.setString(2, email);
                    ps2.executeUpdate();
                    JSONObject userDetails = new JSONObject();
                    userDetails.put("sessionToken", token);
                    return userDetails.toString();
                } else {
                    return "{\"Error\": \"Incorrect password\"}";
                }
            } else {
                return "{\"Error\": \"Email and password are incorrect.\"}";
            }
        } catch (Exception exception) {
            System.out.println("Database error during /user/login: " + exception.getMessage());
            return "{\"Error\": \"Server side error!\"}";
        }
    }
    @POST
    @Path("signup")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)

    public String signup(@FormDataParam("name") String Name, @FormDataParam("email1") String Email, @FormDataParam("password1") String Password) {
        System.out.println("Invoked User.signup()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (Name, Email, Password) VALUES (?, ?, ?)");
            ps.setString(1, Name);
            ps.setString(2, Email);
            ps.setString(3, Password);
            ps.execute();
            return "{\"OK\": \"Added User.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new user, please see server console for more info.\"}";
        }
    }

    public static int validateSessionCookie(Cookie sessionCookie) {     //returns the userID that of the record with the cookie value

        String token = sessionCookie.getValue();
        System.out.println("Invoked User.validateSessionCookie(), cookie value " + token);

        try {
            PreparedStatement statement = Main.db.prepareStatement("SELECT UserID FROM Users WHERE Token = ?");
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("userID is " + resultSet.getInt("UserID"));
            return resultSet.getInt("UserID");  //Retrieve by column name  (should really test we only get one result back!)
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;  //rogue value indicating error
        }
    }
    @POST
    @Path("logout")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteAmp(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked deleteAmp()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Token = NULL WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
            return "{\"OK\": \"sessionToken deleted\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to delete session token, please see server console for more info.\"}";
        }
    }
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String ampList(@CookieParam("sessionToken") Cookie sessionCookie) {

        System.out.println("Invoked User.list()" + sessionCookie.getValue());

        int userID = User.validateSessionCookie(sessionCookie);
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, Name, Email, Password FROM Users WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("UserID", results.getInt(1));
                row.put("Name", results.getString(2));
                row.put("Email", results.getString(3));
                row.put("Password", results.getString(4));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list users. Error code xx.\"}";
        }
    }
    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteUser(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked deleteUser()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Users WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
            return "{\"OK\": \"User deleted\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to delete user, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateUser(@FormDataParam("Name") String Name, @FormDataParam("Email") String Email, @FormDataParam("Password") String Password, @CookieParam("sessionToken") Cookie sessionCookie) {
        try {
            System.out.println("Invoked User.update");

            int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
            if (userID == -1) {
                return "{\"Error\": \"Please log in\"}";
            }

            PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Name = ?, Email = ?, Password = ? WHERE UserID = ?");
            ps.setString(1, Name);
            ps.setString(2, Email);
            ps.setString(3, Password);
            ps.setInt(4, userID);
            ps.execute();
            return "{\"OK\": \"User updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update user, please see server console for more info.\"}";
        }
    }
}
