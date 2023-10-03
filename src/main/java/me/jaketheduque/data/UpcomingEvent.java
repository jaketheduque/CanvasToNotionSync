package me.jaketheduque.data;

import java.util.Date;

public record UpcomingEvent(String id, String title, Date dueDate, Class linkedClass) {}
