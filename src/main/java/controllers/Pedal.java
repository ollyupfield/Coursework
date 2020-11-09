package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("pedal/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class Pedal {
    @GET
    @Path("value")
    @Produces(MediaType.APPLICATION_JSON)
    public String pedalValue() {
        System.out.println("Invoked Pedal.pedalValue()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT SUM(Value) AS pedalsValue FROM Pedals");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("pedalsValue", results.getInt(1));
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
    public String pedalLatest() {
        System.out.println("Invoked Pedal.pedalLatest()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Title FROM Pedals ORDER BY PedalID DESC LIMIT 1;");
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
    public String pedalTotal() {
        System.out.println("Invoked Pedal.pedalTotal()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT COUNT(PedalID) AS TotalPedals FROM Pedals");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("TotalPedals", results.getInt(1));
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
    public String pedalList() {
        System.out.println("Invoked Pedal.pedalList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT PedalID, Title, Description, Model, Value, DateAdded FROM Pedals");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("PedalID", results.getInt(1));
                row.put("Title", results.getString(2));
                row.put("Description", results.getString(3));
                row.put("Model", results.getString(4));
                row.put("Value", results.getInt(5));
                row.put("DateAdded", results.getString(6));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items. Error code xx.\"}";
        }
    }

    @POST
    @Path("delete/{PedalID}")
    public String deletePedal(@PathParam("PedalID") Integer PedalID) {
        System.out.println("Invoked deletePedal()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Pedals WHERE PedalID = ?");
            ps.setInt(1, PedalID);
            ps.execute();
            return "{\"OK\": \"Pedal deleted\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("add")
    public String ampAdd(@FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded) {
        System.out.println("Invoked Pedal.pedalAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Pedals (Title, Description, Model, Value, DateAdded) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Model);
            ps.setInt(4, Value);
            ps.setString(5, DateAdded);
            ps.execute();
            return "{\"OK\": \"Added pedal.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("update")
    public String updatePedal(@FormDataParam("PedalID") Integer PedalID, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded) {
        try {
            System.out.println("Invoked Pedal.updatePedal/update id=" + PedalID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Pedals SET Title = ?, Description = ?, Model = ?, Value = ?, DateAdded = ? WHERE PedalID = ?");
            ps.setInt(1, PedalID);
            ps.setString(2, Title);
            ps.setString(3, Description);
            ps.setString(4, Model);
            ps.setInt(5, Value);
            ps.setString(6, DateAdded);
            ps.execute();
            return "{\"OK\": \"Pedal updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }
}
