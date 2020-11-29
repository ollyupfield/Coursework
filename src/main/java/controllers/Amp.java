package controllers;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("amp/")
public class Amp {
    @GET
    @Path("total")
    @Produces(MediaType.APPLICATION_JSON)
    public String ampTotal() {
        System.out.println("Invoked Amp.ampTotal()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT COUNT(AmpID) AS TotalAmps FROM Amps");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("TotalAmps", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list total. Error code xx.\"}";
        }
    }
    @GET
    @Path("latest")
    @Produces(MediaType.APPLICATION_JSON)
    public String ampLatest() {
        System.out.println("Invoked Amp.ampLatest()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Title FROM Amps ORDER BY AmpID DESC LIMIT 1;");
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
    @Path("value")
    @Produces(MediaType.APPLICATION_JSON)
    public String ampValue() {
        System.out.println("Invoked Amp.ampValue()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT SUM(Value) AS ampsValue FROM Amps");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("ampsValue", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list value. Error code xx.\"}";
        }
    }
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String ampList() {
        System.out.println("Invoked Amp.ampList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT AmpID, Title, Description, Model, Value, DateAdded, ImageLink FROM Amps");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("AmpID", results.getInt(1));
                row.put("Title", results.getString(2));
                row.put("Description", results.getString(3));
                row.put("Model", results.getString(4));
                row.put("Value", results.getInt(5));
                row.put("DateAdded", results.getString(6));
                row.put("ImageLink", results.getString(7));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list amps. Error code xx.\"}";
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
                return "{\"Error\": \"Unable to delete amp, please see server console for more info.\"}";
            }
        }
        @POST
        @Path("add")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)
        public String ampAdd(@FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded) {
        System.out.println("Invoked Amp.ampAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Amps (Title, Description, Model, Value, DateAdded) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Model);
            ps.setInt(4, Value);
            ps.setString(5, DateAdded);
            ps.execute();
            return "{\"OK\": \"Amp Added.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new amp, please see server console for more info.\"}";
        }
    }
    @GET
    @Path("listEdit/{AmpID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String ampListEdit(@PathParam("AmpID") Integer AmpID) {
        System.out.println("Invoked Amp.ampListEdit()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT AmpID, Title, Description, Model, Value, DateAdded FROM Amps WHERE AmpID = ?");
            ps.setInt(1, AmpID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("AmpID", results.getInt(1));
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
            return "{\"Error\": \"Unable to list amp. Error code xx.\"}";
        }
    }
    @GET
    @Path("listSearch/{searchValue}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String ampListEdit(@PathParam("searchValue") String searchValue) {
        System.out.println("Invoked Amp.ampListSearch()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT AmpID, Title, Description, Model, Value, DateAdded FROM Amps WHERE Title LIKE  ?");
            ps.setString(1, '%' + searchValue.toLowerCase() + '%');  //% is wildcard so Title contains search value
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("AmpID", results.getInt(1));
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
            return "{\"Error\": \"Unable to list amp. Error code xx.\"}";
        }
    }
    @POST
    @Path("update/{AmpID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateAmp(@PathParam("AmpID") Integer AmpID, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded) {
        try {
            System.out.println("Invoked Amp.updateAmp" + AmpID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Amps SET Title = ?, Description = ?, Model = ?, Value = ?, DateAdded = ? WHERE AmpID = ?");
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Model);
            ps.setInt(4, Value);
            ps.setString(5, DateAdded);
            ps.setInt(6, AmpID);
            ps.execute();
            return "{\"OK\": \"Amp updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update amp, please see server console for more info.\"}";
        }
    }
}