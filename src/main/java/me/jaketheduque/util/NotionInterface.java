package me.jaketheduque.util;

import me.jaketheduque.data.Class;
import me.jaketheduque.data.UpcomingEvent;
import me.jaketheduque.main.CanvasToNotionSync;
import notion.api.v1.NotionClient;
import notion.api.v1.model.common.OptionColor;
import notion.api.v1.model.common.PropertyType;
import notion.api.v1.model.common.RichTextType;
import notion.api.v1.model.databases.Database;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageParent;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.request.databases.QueryDatabaseRequest;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class NotionInterface {

    public static List<String> getExistingAssignments() {
        List<String> ids = new ArrayList<>();
        try (NotionClient client = new NotionClient(CanvasToNotionSync.PROPERTIES_FILE.getString("notion_token"))) {
            // Finds the Canvas assignment ID of all pages in the Notion homework database
            QueryResults homework = client.queryDatabase(new QueryDatabaseRequest(CanvasToNotionSync.PROPERTIES_FILE.getString("homework_database_id")));
            for (Page p : homework.getResults()) {
                String id = !p.getProperties().get("id").component4().isEmpty() ? p.getProperties().get("id").component4().get(0).component2().component1() : null;

                if (id != null) ids.add(id);
            }
        }
        return ids;
    }

    public static void addCanvasAssignmentToNotion(UpcomingEvent event) {

        try (NotionClient client = new NotionClient(CanvasToNotionSync.PROPERTIES_FILE.getString("notion_token"))) {
            String title = event.title();
            Class assignmentClass = event.linkedClass();
            Date dueDate = event.dueDate();
            String canvasID = event.id();

            // This would look **MUCH** nicer in Kotlin but oh well
            // Adds the given UpcomingEvent page to homework database as a Notion page
            client.createPage(PageParent.database(CanvasToNotionSync.PROPERTIES_FILE.getString("homework_database_id")), Map.of(
                    "name", new PageProperty(UUID.randomUUID().toString(), PropertyType.Title, Collections.singletonList(new PageProperty.RichText(RichTextType.Text, new PageProperty.RichText.Text(title.toLowerCase())))),
                    "class", new PageProperty(UUID.randomUUID().toString(), PropertyType.Relation, null, null, null, null, null, null, null, null, null, null, null, null, null, Collections.singletonList(new PageProperty.PageReference(assignmentClass.getNotionPageID()))),
                    "due date", new PageProperty(UUID.randomUUID().toString(), PropertyType.Date, null, null, null, null, null, null, new PageProperty.Date(new SimpleDateFormat("yyyy-MM-dd").format(dueDate))),
                    "id", new PageProperty(UUID.randomUUID().toString(), PropertyType.RichText, null, Collections.singletonList(new PageProperty.RichText(RichTextType.Text, new PageProperty.RichText.Text(canvasID))))
            ), null, null, null);
        }
    }
}
