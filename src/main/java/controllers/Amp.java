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

@Path("amp/")
public class Amp {
    @GET
    @Path("total")
    @Produces(MediaType.APPLICATION_JSON)
    public String ampTotal(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Amp.ampTotal()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT COUNT(AmpID) AS TotalAmps FROM Amps WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("TotalAmps", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list total amps\"}";
        }
    }
    @GET
    @Path("latest")
    @Produces(MediaType.APPLICATION_JSON)
    public String ampLatest(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Amp.ampLatest()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Title, ImageLink FROM Amps WHERE UserID = ? ORDER BY AmpID DESC LIMIT 1");
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
            return "{\"Error\": \"Unable to list latest amps\"}";
        }
    }
    @GET
    @Path("value")
    @Produces(MediaType.APPLICATION_JSON)
    public String ampValue(@CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Amp.ampValue()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT SUM(Value) AS ampsValue FROM Amps WHERE UserID = ?");
            ps.setInt(1, userID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("ampsValue", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list amp values\"}";
        }
    }
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String ampList(@CookieParam("sessionToken") Cookie sessionCookie) {

        System.out.println("Invoked Amp.ampList()" + sessionCookie.getValue());

        int userID = User.validateSessionCookie(sessionCookie);
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT AmpID, Title, Description, Model, Value, DateAdded, ImageLink FROM Amps WHERE UserID = ?");
            ps.setInt(1, userID);
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
                row.put("ImageLink", results.getString(7));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list amps\"}";
        }
    }
        @POST
        @Path("delete/{AmpID}")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)
        public String deleteAmp(@PathParam("AmpID") Integer AmpID, @CookieParam("sessionToken") Cookie sessionCookie) {
            System.out.println("Invoked deleteAmp()");

            int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
            if (userID == -1) {
                return "{\"Error\": \"Please log in\"}";
            }

            try {
                PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Amps WHERE AmpID = ?");
                ps.setInt(1, AmpID);
                ps.execute();
                return "{\"OK\": \"Amp Deleted\"}";
            } catch (Exception exception) {
                System.out.println("Database error: " + exception.getMessage());
                return "{\"Error\": \"Unable to delete amp\"}";
            }
        }
        @POST
        @Path("add")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)
        public String ampAdd(@FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded, @FormDataParam("outputFile") String ImageLink, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Amp.ampAdd()");

            int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
            if (userID == -1) {
                return "{\"Error\": \"Please log in\"}";
            }

        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Amps (Title, Description, Model, Value, DateAdded, ImageLink, UserID) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Model);
            ps.setInt(4, Value);
            ps.setString(5, DateAdded);
            ps.setString(6, ImageLink);
            ps.setInt(7, userID);
            ps.execute();
            return "{\"OK\": \"Amp Added.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new amp\"}";
            }
        }
    @GET
    @Path("listEdit/{AmpID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String ampListEdit(@PathParam("AmpID") Integer AmpID, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Amp.ampListEdit()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT AmpID, Title, Description, Model, Value, DateAdded, ImageLink FROM Amps WHERE AmpID = ? AND UserID = ?");
            ps.setInt(1, AmpID);
            ps.setInt(2, userID);
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
                row.put("ImageLink", results.getString(7));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list amp\"}";
        }
    }
    @GET
    @Path("listSearch/{searchValue}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String ampListEdit(@PathParam("searchValue") String searchValue, @CookieParam("sessionToken") Cookie sessionCookie) {
        System.out.println("Invoked Amp.ampListSearch()");

        int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "{\"Error\": \"Please log in\"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT AmpID, Title, Description, Model, Value, DateAdded, ImageLink FROM Amps WHERE Title LIKE  ? AND UserID = ?");
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
                row.put("Value", results.getInt(5));
                row.put("DateAdded", results.getString(6));
                row.put("ImageLink", results.getString(7));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list amp\"}";
        }
    }
    @POST
    @Path("update/{AmpID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateAmp(@PathParam("AmpID") Integer AmpID, @FormDataParam("Title") String Title, @FormDataParam("Description") String Description, @FormDataParam("Model") String Model, @FormDataParam("Value") Integer Value, @FormDataParam("DateAdded") String DateAdded, @FormDataParam("outputFile") String ImageLink, @CookieParam("sessionToken") Cookie sessionCookie) {
        try {
            System.out.println("Invoked Amp.updateAmp" + AmpID);

            int userID = User.validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
            if (userID == -1) {
                return "{\"Error\": \"Please log in\"}";
            }

            PreparedStatement ps = Main.db.prepareStatement("UPDATE Amps SET Title = ?, Description = ?, Model = ?, Value = ?, DateAdded = ?, ImageLink = ?, UserID = ? WHERE AmpID = ?");
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Model);
            ps.setInt(4, Value);
            ps.setString(5, DateAdded);
            ps.setString(6, ImageLink);
            ps.setInt(7, userID);
            ps.setInt(8, AmpID);
            ps.execute();
            return "{\"OK\": \"Amp Updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update amp\"}";
        }
    }
    @POST
    @Path("image")
    public String userImage(@CookieParam("sessionToken") Cookie sessionCookie, @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {

        System.out.println("Invoked Amp.Image()");

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
