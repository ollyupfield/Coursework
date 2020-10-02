package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("manufacture/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Manufacture {
    @GET
    @Path("list")
    public String manufactureList() {
        System.out.println("Invoked Manufacture.manufactureList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT ManufactureID, ManufactureName FROM Manufactures");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("ManufactureID", results.getInt(1));
                row.put("ManufactureName", results.getString(2));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items. Error code xx.\"}";
        }
    }
    @POST
    @Path("delete/{ManufactureID}")

    public String deleteManufacture(@PathParam("ManufactureID") Integer ManufactureID) {
        System.out.println("Invoked deleteManufacture()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Manufactures WHERE ManufactureID = ?");
            ps.setInt(1, ManufactureID);
            ps.execute();
            return "{\"OK\": \"Manufacture deleted\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("add")
    public String ampManufacture(@FormDataParam("ManufactureID") Integer ManufactureID, @FormDataParam("ManufactureName") String ManufactureName) {
        System.out.println("Invoked Manufacture.ampManufacture()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Manufactures (ManufactureID, ManufactureName) VALUES (?, ?");
            ps.setInt(1, ManufactureID);
            ps.setString(2, ManufactureName);
            ps.execute();
            return "{\"OK\": \"Added manufacture.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("update")
    public String updateManufacture(@FormDataParam("ManufactureID") Integer ManufactureID, @FormDataParam("ManufactureName") String ManufactureName) {
        try {
            System.out.println("Invoked Manufacture.updateManufacture/update id=" + ManufactureID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Manufactures SET ManufactureID = ?, ManufactureName = ?");
            ps.setInt(1, ManufactureID);
            ps.setString(2, ManufactureName);
            ps.execute();
            return "{\"OK\": \"Manufacture updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }
}
