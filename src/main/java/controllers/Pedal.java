package controllers;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("pedal/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class Pedal {
    @GET
    @Path("value")
    @Produces(MediaType.APPLICATION_JSON)
    public String pedalValue(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Pedal.pedalValue()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT SUM(Value) AS pedalsValue FROM Pedals WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("pedalsValue", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list pedal values\"}";
        }
    }
    @GET
    @Path("latest")
    @Produces(MediaType.APPLICATION_JSON)
    public String pedalLatest(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Pedal.pedalLatest()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Title, ImageLink FROM Pedals WHERE UserID = ? ORDER BY PedalID DESC LIMIT 1");
            ps.setInt(1, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("Title", results.getString(1));
                row.put("ImageLink", results.getString(2));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list latest pedals\"}";
        }
    }
    @GET
    @Path("total")
    public String pedalTotal(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Pedal.pedalTotal()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT COUNT(PedalID) AS TotalPedals FROM Pedals WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("TotalPedals", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list total pedals\"}";
        }
    }
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String pedalList(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Pedal.pedalList()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT PedalID, Title, Description, Model, Value, DateAdded, ImageLink FROM Pedals WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("PedalID", results.getInt(1));
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
            return "{\"Error\": \"Unable to list pedals\"}";
        }
    }
    @POST
    @Path("delete/{PedalID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String deletePedal(@PathParam("PedalID") Integer PedalID, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked deletePedal()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Pedals WHERE PedalID = ?");
            ps.setInt(1, PedalID);
            ps.execute();
            return "{\"OK\": \"Pedal Deleted\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to delete pedal\"}";
        }
    }
    @POST
    @Path("add")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String pedalAdd(@FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded, @FormDataParam("outputFile") String ImageLink, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Pedal.pedalAdd()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Pedals (Title, Description, Model, Value, DateAdded, ImageLink, UserID) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Model);
            ps.setInt(4, Value);
            ps.setString(5, DateAdded);
            ps.setString(6, ImageLink);
            ps.setInt(7, userID);
            ps.execute();
            return "{\"OK\": \"Pedal Added.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new pedal\"}";
        }
    }
    @GET
    @Path("listEdit/{PedalID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String pedalListEdit(@PathParam("PedalID") Integer PedalID, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Pedal.pedalListEdit()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT PedalID, Title, Description, Model, Value, DateAdded, ImageLink FROM Pedals WHERE PedalID = ? AND UserID = ?");
            ps.setInt(1, PedalID);
            ps.setInt(2, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("PedalID", results.getInt(1));
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
            return "{\"Error\": \"Unable to list pedal\"}";
        }
    }
    @GET
    @Path("listSearch/{searchValue}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String pedalListEdit(@PathParam("searchValue") String searchValue, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Pedal.pedalListSearch()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT PedalID, Title, Description, Model, Value, DateAdded, ImageLink FROM Pedals WHERE Title LIKE  ? AND UserID = ?");
            ps.setString(1, '%' + searchValue.toLowerCase() + '%');  //% is wildcard so Title contains search value
            ps.setInt(2, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("PedalID", results.getInt(1));
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
            return "{\"Error\": \"Unable to list pedal\"}";
        }
    }
    @POST
    @Path("update/{PedalID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updatePedal(@PathParam("PedalID") Integer PedalID, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded, @FormDataParam("outputFile") String ImageLink, @CookieParam("sessionToken") Cookie sessionCookie) {

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        try {
            System.out.println("Invoked Pedal.updatePedal" + PedalID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Pedals SET Title = ?, Description = ?, Model = ?, Value = ?, DateAdded = ?, ImageLink = ?, UserID = ? WHERE PedalID = ?");
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Model);
            ps.setInt(4, Value);
            ps.setString(5, DateAdded);
            ps.setString(6, ImageLink);
            ps.setInt(7, PedalID);
            ps.setInt(8, userID);
            ps.execute();
            return "{\"OK\": \"Pedal Updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update pedal\"}";
        }
    }
    @POST
    @Path("image")
    public String userImage(@CookieParam("sessionToken") Cookie sessionCookie, @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {

        System.out.println("Invoked pedal.Image()");

        String fileName = fileDetail.getFileName();  //file name submitted through form
        int dot = fileName.lastIndexOf('.');            //find where the . is to get the file extension
        String fileExtension = fileName.substring(dot + 1);   //get file extension from fileName
        String newFileName = "client/img/" + fileName;  //create a new unique identifier for file and append extension

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        String uploadedFileLocation = "C:\\Users\\44737\\IdeaProjects\\Coursework\\resources\\" + newFileName;

        try {
            int read = 0;
            byte[] bytes = new byte[1024];
            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            System.out.println("File uploaded to server and imageLink saved to database");
            return "File uploaded to server and imageLink saved to database";

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return "{\"Error\": \"Unable to upload image\"}";
        }
    }
}
