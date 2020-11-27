package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

@Path("user/")
public class User {

    @POST
    @Path("login")
    public String loginUser(@FormDataParam("email") String Email, @FormDataParam("password") String Password) {
        System.out.println("Invoked loginUser() on path user/login");
        try {
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT Password FROM Users WHERE Email = ?");
            ps1.setString(1, Email);
            ResultSet loginResults = ps1.executeQuery();
            if (loginResults.next() == true) {
                String correctPassword = loginResults.getString(1);
                if (Password.equals(correctPassword)) {
                    String token = UUID.randomUUID().toString();
                    PreparedStatement ps2 = Main.db.prepareStatement("UPDATE Users SET SessionToken = ? WHERE Email = ?");
                    ps2.setString(1, token);
                    ps2.setString(2, Email);
                    ps2.executeUpdate();
                    JSONObject userDetails = new JSONObject();
                    userDetails.put("email", Email);
                    userDetails.put("token", token);
                    return userDetails.toString();
                } else {
                    return "{\"Error\": \"Incorrect password!\"}";
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

    public String signup(@FormDataParam("signupEmail") String Email, @FormDataParam("signupPassword") String Password, @FormDataParam("Name") String Name) {
        System.out.println("Invoked User.signup()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (Email, Password, Name) VALUES (?, ?, ?)");
            ps.setString(1, Email);
            ps.setString(2, Password);
            ps.setString(3, Name);
            ps.execute();
            return "{\"OK\": \"Added User.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new user, please see server console for more info.\"}";
        }
    }

    public static boolean validToken(String token) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID FROM Users WHERE SessionToken = ?");
            ps.setString(1, token);
            ResultSet logoutResults = ps.executeQuery();
            return logoutResults.next();
        } catch (Exception exception) {
            System.out.println("Database error" + exception.getMessage());
            return false;
        }
    }
}