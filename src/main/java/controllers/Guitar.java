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
            PreparedStatement ps = Main.db.prepareStatement("SELECT GuitarID, CollectionID, ManufactureID, TotalCount, Title, Description, Model, Colour, DateAdded FROM Guitars");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("GuitarID", results.getInt(1));
                row.put("CollectionID", results.getInt(2));
                row.put("ManufactureID", results.getInt(3));
                row.put("TotalCount", results.getInt(4));
                row.put("Title", results.getString(5));
                row.put("Description", results.getString(6));
                row.put("Model", results.getString(7));
                row.put("Colour", results.getString(8));
                row.put("DateAdded", results.getString(9));
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
    public String guitarAdd(@FormDataParam("GuitarID") Integer GuitarID, @FormDataParam("CollectionID") Integer CollectionID, @FormDataParam("ManufactureID") Integer ManufactureID, @FormDataParam("TotalCount") Integer TotalCount, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Colour") String Colour, @FormDataParam("DateAdded") String DateAdded) {
        System.out.println("Invoked Amp.guitarAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Guitars (GuitarID, CollectionID, ManufactureID, TotalCount, Title, Description, Model, Colour, DateAdded) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, GuitarID);
            ps.setInt(2, CollectionID);
            ps.setInt(3, ManufactureID);
            ps.setInt(4, TotalCount);
            ps.setString(5, Title);
            ps.setString(6, Description);
            ps.setString(7, Model);
            ps.setString(8, Colour);
            ps.setString(9, DateAdded);
            ps.execute();
            return "{\"OK\": \"Added guitar.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("update")
    public String updateGuitar(@FormDataParam("GuitarID") Integer GuitarID, @FormDataParam("CollectionID") Integer CollectionID, @FormDataParam("ManufactureID") Integer ManufactureID, @FormDataParam("TotalCount") Integer TotalCount, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Colour") String Colour, @FormDataParam("DateAdded") String DateAdded) {
        try {
            System.out.println("Invoked Amp.updateGuitar/update id=" + GuitarID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Guitars SET CollectionID = ?, ManufactureID = ?, TotalCount = ?, Title = ?, Description = ?, Model = ?, Colour = ?, DateAdded = ? WHERE GuitarID = ?");
            ps.setInt(1, GuitarID);
            ps.setInt(2, CollectionID);
            ps.setInt(3, ManufactureID);
            ps.setInt(4, TotalCount);
            ps.setString(5, Title);
            ps.setString(6, Description);
            ps.setString(7, Model);
            ps.setString(8, Colour);
            ps.setString(9, DateAdded);
            ps.execute();
            return "{\"OK\": \"Guitar updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }
}
