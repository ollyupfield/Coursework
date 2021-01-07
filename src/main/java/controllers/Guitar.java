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

@Path("guitar/")
public class Guitar {
    @GET
    @Path("value")
    @Produces(MediaType.APPLICATION_JSON)
    public String guitarValue(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Guitar.guitarValue()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT SUM(Value) AS guitarsValue FROM Guitars WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("guitarsValue", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list amp values\"}";
        }
    }
    @GET
    @Path("latest")
    @Produces(MediaType.APPLICATION_JSON)
    public String guitarLatest(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Guitar.guitarLatest()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Title, ImageLink FROM Guitars WHERE UserID = ? ORDER BY GuitarID DESC LIMIT 1");
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
            return "{\"Error\": \"Unable to list latest guitar\"}";
        }
    }
    @GET
    @Path("total")
    @Produces(MediaType.APPLICATION_JSON)
    public String guitarTotal(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Guitar.guitarTotal()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT COUNT(GuitarID) AS TotalGuitars FROM Guitars WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("TotalGuitars", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list total guitars\"}";
        }
    }
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String guitarList(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Guitar.guitarList()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT GuitarID, Title, Description, Model, Colour, Value, DateAdded, ImageLink FROM Guitars WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
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
                row.put("ImageLink", results.getString(8));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list guitars\"}";
        }
    }
    @POST
    @Path("delete/{GuitarID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteGuitar(@PathParam("GuitarID") Integer GuitarID, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked deleteGuitar()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Guitars WHERE GuitarID = ?");
            ps.setInt(1, GuitarID);
            ps.execute();
            return "{\"OK\": \"Guitar Deleted\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to delete guitar\"}";
        }
    }
    @POST
    @Path("add")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String guitarAdd(@FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Colour") String Colour, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded, @FormDataParam("outputFile") String ImageLink, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Guitar.add()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Guitars (Title, Description, Model, Colour, Value, DateAdded, ImageLink, UserID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Model);
            ps.setString(4, Colour);
            ps.setInt(5, Value);
            ps.setString(6, DateAdded);
            ps.setString(7, ImageLink);
            ps.setInt(8, userID);
            ps.execute();
            return "{\"OK\": \"Guitar Added.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new guitar\"}";
        }
    }
    @GET
    @Path("listEdit/{GuitarID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String guitarListEdit(@PathParam("GuitarID") Integer GuitarID, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Guitar.guitarListEdit()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT GuitarID, Title, Description, Model, Colour, Value, DateAdded, ImageLink FROM Guitars WHERE GuitarID = ? AND UserID = ?");
            ps.setInt(1, GuitarID);
            ps.setInt(2, userID);
            ps.execute();
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
                row.put("ImageLink", results.getString(8));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list guitar\"}";
        }
    }
    @GET
    @Path("listSearch/{searchValue}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String guitarListEdit(@PathParam("searchValue") String searchValue, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Guitar.guitarListSearch()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT GuitarID, Title, Description, Model, Colour, Value, DateAdded, ImageLink FROM Guitars WHERE Title LIKE  ? AND UserID = ?");
            ps.setString(1, '%' + searchValue.toLowerCase() + '%');  //% is wildcard so Title contains search value
            ps.setInt(2, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("AmpID", results.getInt(1));
                row.put("Title", results.getString(2));
                row.put("Description", results.getString(3));
                row.put("Model", results.getString(4));
                row.put("Colour", results.getString(5));
                row.put("Value", results.getInt(6));
                row.put("DateAdded", results.getString(7));
                row.put("ImageLink", results.getString(8));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list guitar\"}";
        }
    }
    @POST
    @Path("update/{GuitarID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateGuitar(@PathParam("GuitarID") Integer GuitarID, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Colour") String Colour, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded, @FormDataParam("outputFile") String ImageLink, @CookieParam("sessionToken") Cookie sessionCookie) {

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        try {
            System.out.println("Invoked Guitar.updateGuitar" + GuitarID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Guitars SET Title = ?, Description = ?, Model = ?, Colour = ?, Value = ?, DateAdded = ?, ImageLink = ?, UserID = ?  WHERE GuitarID = ?");
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Model);
            ps.setString(4, Colour);
            ps.setInt(5, Value);
            ps.setString(6, DateAdded);
            ps.setString(7, ImageLink);
            ps.setInt(8, GuitarID);
            ps.setInt(9, userID);
            ps.execute();
            return "{\"OK\": \"Guitar Updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update guitar\"}";
        }
    }
    @POST
    @Path("image")
    public String userImage(@CookieParam("sessionToken") Cookie sessionCookie, @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {

        System.out.println("Invoked Guitar.Image()");

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
