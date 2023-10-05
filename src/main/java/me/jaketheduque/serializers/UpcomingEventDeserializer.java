package me.jaketheduque.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.jaketheduque.data.UpcomingEvent;
import me.jaketheduque.data.Class;
import me.jaketheduque.main.CanvasToNotionSync;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class UpcomingEventDeserializer extends StdDeserializer<UpcomingEvent> {
    public UpcomingEventDeserializer(java.lang.Class<UpcomingEvent> t) {
        super(t);
    }

    @Override
    public UpcomingEvent deserialize(JsonParser parser, DeserializationContext deserializer) {
        try {
            ObjectCodec codec = parser.getCodec();
            JsonNode node;
            node = codec.readTree(parser);

            JsonNode titleNode = node.get("title");
            String title = titleNode.asText();

            // Make sure the event has an assignment linked
            if (node.hasNonNull("assignment")) {
                JsonNode assignment = node.get("assignment");

                JsonNode dueDateNode = assignment.get("due_at");
                Date dueDate = Date.from(Instant.parse(dueDateNode.asText()));

                JsonNode idNode = assignment.get("id");
                String id = idNode.asText();

                JsonNode classNode = assignment.get("course_id");
                String classID = classNode.asText();
                Class assignmentClass = CanvasToNotionSync.CANVAS_CLASSES_MAP.get(classID);

                return new UpcomingEvent(id, title, dueDate, assignmentClass);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
