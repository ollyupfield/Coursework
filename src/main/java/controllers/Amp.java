package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@Path("amp/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Amp {
    @GET
    @Path("list")
    public String ampList() {
        System.out.println("Invoked Amp.ampList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT AmpID, CollectionID, TotalCount, Title, Description, Model, Make, DateAdded FROM Amps");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("AmpID", results.getInt(1));
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
        @Path("delete/{AmpID}")

        public String deleteAmp(@PathParam("AmpID") Integer AmpID) {
            System.out.println("Invoked deleteAmp()");
            try {
                PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Amps WHERE AmpID = ?");
                ps.setInt(1, AmpID);
                ps.execute();
                return "{\"OK\": \"Amp deleted\"}";
            } catch (Exception exception) {
                System.out.println("Database error: " + exception.getMessage());
                return "{\"Error\": \"Unable to delete item, please see server console for more info.\"}";
            }
        }
    @POST
    @Path("add")
    public String ampAdd(@FormDataParam("AmpID") Integer AmpID, @FormDataParam("CollectionID") Integer CollectionID, @FormDataParam("TotalCount") Integer TotalCount, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Make") String Make, @FormDataParam("DateAdded") String DateAdded) {
        System.out.println("Invoked Amp.ampAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Amps (AmpID, CollectionID, TotalCount, Title, Description, Model, Make, DateAdded) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, AmpID);
            ps.setInt(2, CollectionID);
            ps.setInt(3, TotalCount);
            ps.setString(4, Title);
            ps.setString(5, Description);
            ps.setString(6, Model);
            ps.setString(7, Make);
            ps.setString(8, DateAdded);
            ps.execute();
            return "{\"OK\": \"Added amp.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("update")
    public String updateAmp(@FormDataParam("AmpID") Integer AmpID, @FormDataParam("CollectionID") Integer CollectionID, @FormDataParam("TotalCount") Integer TotalCount, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Make") String Make, @FormDataParam("DateAdded") String DateAdded) {
        try {
            System.out.println("Invoked Amp.updateAmp/update id=" + AmpID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Amps SET CollectionID = ?, TotalCount = ?, Title = ?, Description = ?, Model = ?, Make = ?, DateAdded = ? WHERE AmpID = ?");
            ps.setInt(1, AmpID);
            ps.setInt(2, CollectionID);
            ps.setInt(3, TotalCount);
            ps.setString(4, Title);
            ps.setString(5, Description);
            ps.setString(6, Model);
            ps.setString(7, Make);
            ps.setString(8, DateAdded);
            ps.execute();
            return "{\"OK\": \"Amp updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }
}
