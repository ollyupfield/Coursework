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
public class Amp {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String ampList() {
        System.out.println("Invoked Amp.ampList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT AmpID, CollectionID, Title, Description, Model, Make, DateAdded FROM Amps");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("AmpID", results.getInt(1));
                row.put("CollectionID", results.getInt(2));
                row.put("Title", results.getString(3));
                row.put("Description", results.getString(4));
                row.put("Model", results.getString(5));
                row.put("Make", results.getString(6));
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
        @Path("delete/{AmpID}")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)
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
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)

        public String ampAdd(@FormDataParam("CollectionID") Integer CollectionID, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Make") String Make, @FormDataParam("DateAdded") String DateAdded) {
        System.out.println("Invoked Amp.ampAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Amps (CollectionID, Title, Description, Model, Make, DateAdded) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, CollectionID);
            ps.setString(2, Title);
            ps.setString(3, Description);
            ps.setString(4, Model);
            ps.setString(5, Make);
            ps.setString(6, DateAdded);
            ps.execute();
            return "{\"OK\": \"Added amp.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateAmp(@FormDataParam("AmpID") Integer AmpID, @FormDataParam("CollectionID") Integer CollectionID, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Make") String Make, @FormDataParam("DateAdded") String DateAdded) {
        try {
            System.out.println("Invoked Amp.updateAmp/update id=" + AmpID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Amps SET CollectionID = ?, Title = ?, Description = ?, Model = ?, Make = ?, DateAdded = ? WHERE AmpID = ?");
            ps.setInt(1, AmpID);
            ps.setInt(2, CollectionID);
            ps.setString(3, Title);
            ps.setString(4, Description);
            ps.setString(5, Model);
            ps.setString(6, Make);
            ps.setString(7, DateAdded);
            ps.execute();
            return "{\"OK\": \"Amp updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }
}
