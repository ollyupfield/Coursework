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
    @Path("list")
    public String guitarList() {
        System.out.println("Invoked Guitar.guitarList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT GuitarID, CollectionID, Title, Description, Make, Model, Colour, DateAdded FROM Guitars");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("GuitarID", results.getInt(1));
                row.put("CollectionID", results.getInt(2));
                row.put("Title", results.getString(3));
                row.put("Description", results.getString(4));
                row.put("Make", results.getString(5));
                row.put("Model", results.getString(6));
                row.put("Colour", results.getString(7));
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
    public String guitarAdd(@FormDataParam("CollectionID") Integer CollectionID, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Make") String Make, @FormDataParam("Model") String Model, @FormDataParam("Colour") String Colour, @FormDataParam("DateAdded") String DateAdded) {
        System.out.println("Invoked Amp.guitarAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Guitars (CollectionID, Title, Description, Make, Model, Colour, DateAdded) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, CollectionID);
            ps.setString(2, Title);
            ps.setString(3, Description);
            ps.setString(4, Make);
            ps.setString(5, Model);
            ps.setString(6, Colour);
            ps.setString(7, DateAdded);
            ps.execute();
            return "{\"OK\": \"Added guitar.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("update")
    public String updateGuitar(@FormDataParam("GuitarID") Integer GuitarID, @FormDataParam("CollectionID") Integer CollectionID, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Make") String Make, @FormDataParam("Model") String Model, @FormDataParam("Colour") String Colour, @FormDataParam("DateAdded") String DateAdded) {
        try {
            System.out.println("Invoked Amp.updateGuitar/update id=" + GuitarID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Guitars SET CollectionID = ?, Title = ?, Description = ?, Make = ?, Model = ?, Colour = ?, DateAdded = ? WHERE GuitarID = ?");
            ps.setInt(1, GuitarID);
            ps.setInt(2, CollectionID);
            ps.setString(3, Title);
            ps.setString(4, Description);
            ps.setString(5, Make);
            ps.setString(6, Model);
            ps.setString(7, Colour);
            ps.setString(8, DateAdded);
            ps.execute();
            return "{\"OK\": \"Guitar updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }
}
