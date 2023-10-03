package me.jaketheduque.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class Class {
    private String name;
    @JsonProperty("notion_page_id")
    private String notionPageID;
    @JsonProperty("canvas_course_ids")
    private String[] canvasCourseIDs;

    public Class() {
    }

    public Class(String name, String[] canvasCourseIDs, String notionPageID) {
        this.name = name;
        this.canvasCourseIDs = canvasCourseIDs;
        this.notionPageID = notionPageID;
    }

    public String getName() {
        return name;
    }

    public String[] getCanvasCourseIDs() {
        return canvasCourseIDs;
    }

    public String getNotionPageID() {
        return notionPageID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotionPageID(String notionPageID) {
        this.notionPageID = notionPageID;
    }

    public void setCanvasCourseIDs(String[] canvasCourseIDs) {
        this.canvasCourseIDs = canvasCourseIDs;
    }

    @Override
    public String toString() {
        return "Class{" +
                "name='" + name + '\'' +
                ", notionPageID='" + notionPageID + '\'' +
                ", canvasCourseIDs=" + Arrays.toString(canvasCourseIDs) +
                '}';
    }
}
