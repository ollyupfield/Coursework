package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("guitar/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Guitar {
    @GET
    @Path("value")
    @Produces(MediaType.APPLICATION_JSON)
    public String guitarValue() {
        System.out.println("Invoked Guitar.guitarValue()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT SUM(Value) AS guitarsValue FROM Guitars");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("guitarsValue", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list value. Error code xx.\"}";
        }
    }
    @GET
    @Path("latest")
    @Produces(MediaType.APPLICATION_JSON)
    public String guitarLatest() {
        System.out.println("Invoked Guitar.guitarLatest()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Title FROM Guitars ORDER BY GuitarID DESC LIMIT 1;");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("Title", results.getString(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list latest. Error code xx.\"}";
        }
    }
    @GET
    @Path("total")
    @Produces(MediaType.APPLICATION_JSON)
    public String guitarTotal() {
        System.out.println("Invoked Guitar.guitarTotal()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT COUNT(GuitarID) AS TotalGuitars FROM Guitars");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("TotalGuitars", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list total. Error code xx.\"}";
        }
    }
    @GET
    @Path("list")
    public String guitarList() {
        System.out.println("Invoked Guitar.guitarList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT GuitarID, Title, Description, Model, Colour, Value, DateAdded FROM Guitars");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("GuitarID", results.getInt(1));
                row.put("Title", results.getString(2));
                row.put("Description", results.getString(3));
                row.put("Model", results.getString(4));
                row.put("Colour", results.getString(5));
                row.put("Value", results.getInt(6));
                row.put("DateAdded", results.getString(7));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items. Error code xx.\"}";
        }
    }
    @POST
    @Path("delete/{GuitarID}")

    public String deleteGuitar(@PathParam("GuitarID") Integer GuitarID) {
        System.out.println("Invoked deleteGuitar()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Guitars WHERE GuitarID = ?");
            ps.setInt(1, GuitarID);
            ps.execute();
            return "{\"OK\": \"Guitar deleted\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("add")
    public String guitarAdd(@FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Colour") String Colour, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded) {
        System.out.println("Invoked Amp.guitarAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Guitars (Title, Description, Model, Colour, Value, DateAdded) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Model);
            ps.setString(4, Colour);
            ps.setInt(5, Value);
            ps.setString(6, DateAdded);
            ps.execute();
            return "{\"OK\": \"Added guitar.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("update")
    public String updateGuitar(@FormDataParam("GuitarID") Integer GuitarID, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Colour") String Colour, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded) {
        try {
            System.out.println("Invoked Amp.updateGuitar/update id=" + GuitarID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Guitars SET Title = ?, Description = ?, Model = ?, Colour = ?, Value = ?, DateAdded = ? WHERE GuitarID = ?");
            ps.setInt(1, GuitarID);
            ps.setString(2, Title);
            ps.setString(3, Description);
            ps.setString(4, Model);
            ps.setString(5, Colour);
            ps.setInt(6, Value);
            ps.setString(7, DateAdded);
            ps.execute();
            return "{\"OK\": \"Guitar updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }
}
