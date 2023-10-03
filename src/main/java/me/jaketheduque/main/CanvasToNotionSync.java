package me.jaketheduque.main;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import me.jaketheduque.data.Class;
import me.jaketheduque.data.UpcomingEvent;
import me.jaketheduque.serializers.UpcomingEventDeserializer;
import me.jaketheduque.util.NotionInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CanvasToNotionSync {
    public final static ResourceBundle PROPERTIES_FILE = ResourceBundle.getBundle("application");
    public final static Map<String, Class> CANVAS_CLASSES_MAP;

    static {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Class> classes = mapper.readValue(new File(PROPERTIES_FILE.getString("class_json_path")), new TypeReference<List<Class>>() {
            });
            Map<String, Class> tempMap = new HashMap<>();
            for (Class c : classes) {
                for (String canvasID : c.getCanvasCourseIDs()) {
                    tempMap.put(canvasID, c);
                }
            }
            CANVAS_CLASSES_MAP = tempMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        // Gets the Canvas assignment IDs of assignments already added to the Notion page (does not matter if finished or not)
        List<String> ids = NotionInterface.getExistingAssignments();

        // Gets the JSON string from Canvas API of upcoming events
        String json = getUpcomingEvents().toString();

        ObjectMapper objectMapper = new ObjectMapper();

        // Creates a List of UpcomingEvent objects
        SimpleModule module =
                new SimpleModule("UpcomingEventDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(UpcomingEvent.class, new UpcomingEventDeserializer(UpcomingEvent.class));
        objectMapper.registerModule(module);
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<UpcomingEvent> eventsList = objectMapper.readValue(json, typeFactory.constructCollectionType(List.class, UpcomingEvent.class));

        // Removes any UpcomingEvent objects that have a matching Canvas assignment ID in the Notion homework database
        eventsList.removeIf(e -> ids.contains(e.id()));

        // Adds each remaining UpcomingEven to the Notion homework database
        eventsList.forEach(NotionInterface::addCanvasAssignmentToNotion);
    }

    private static StringBuffer getUpcomingEvents() throws URISyntaxException, IOException {
        URI uri = new URI("https://webcampus.unr.edu/api/v1/users/self/upcoming_events");
        HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
        con.setRequestProperty("Authorization", "Bearer " + PROPERTIES_FILE.getString("canvas_token"));
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
        con.disconnect();

        return content;
    }
}
