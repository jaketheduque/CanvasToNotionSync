package me.jaketheduque.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ResourceBundle;
public class CanvasToNotionSync {
    private final static ResourceBundle PROPERTIES_FILE = ResourceBundle.getBundle("application");

    public static void main(String[] args) throws IOException, URISyntaxException {
        URI uri = new URI("https://webcampus.unr.edu/api/v1/users/self/upcoming_events");
        HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
        con.setRequestProperty("Authorization","Bearer "+ PROPERTIES_FILE.getString("canvas_token"));
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        System.out.println(content);

        con.disconnect();
    }
}
