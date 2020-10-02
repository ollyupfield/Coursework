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
    @Path("list")
    public String pedalList() {
        System.out.println("Invoked Pedal.pedalList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT PedalID, CollectionID, TotalCount, Title, Description, Model, Make, DateAdded FROM Pedals");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("PedalID", results.getInt(1));
                row.put("CollectionID", results.getInt(2));
                row.put("TotalCount", results.getInt(3));
                row.put("Title", results.getString(4));
                row.put("Description", results.getString(5));
                row.put("Model", results.getString(6));
                row.put("Make", results.getString(7));
                row.put("DateAdded", results.getString(8));
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
    public String ampAdd(@FormDataParam("PedalID") Integer PedalID, @FormDataParam("CollectionID") Integer CollectionID, @FormDataParam("TotalCount") Integer TotalCount, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Make") String Make, @FormDataParam("DateAdded") String DateAdded) {
        System.out.println("Invoked Pedal.pedalAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Pedals (PedalID, CollectionID, TotalCount, Title, Description, Model, Make, DateAdded) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, PedalID);
            ps.setInt(2, CollectionID);
            ps.setInt(3, TotalCount);
            ps.setString(4, Title);
            ps.setString(5, Description);
            ps.setString(6, Model);
            ps.setString(7, Make);
            ps.setString(8, DateAdded);
            ps.execute();
            return "{\"OK\": \"Added pedal.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("update")
    public String updatePedal(@FormDataParam("PedalID") Integer PedalID, @FormDataParam("CollectionID") Integer CollectionID, @FormDataParam("TotalCount") Integer TotalCount, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Make") String Make, @FormDataParam("DateAdded") String DateAdded) {
        try {
            System.out.println("Invoked Pedal.updatePedal/update id=" + PedalID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Pedals SET CollectionID = ?, TotalCount = ?, Title = ?, Description = ?, Model = ?, Make = ?, DateAdded = ? WHERE PedalID = ?");
            ps.setInt(1, PedalID);
            ps.setInt(2, CollectionID);
            ps.setInt(3, TotalCount);
            ps.setString(4, Title);
            ps.setString(5, Description);
            ps.setString(6, Model);
            ps.setString(7, Make);
            ps.setString(8, DateAdded);
            ps.execute();
            return "{\"OK\": \"Pedal updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }
}
