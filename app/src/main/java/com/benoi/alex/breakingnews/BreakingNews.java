package com.benoi.alex.breakingnews;

import java.util.ArrayList;

class BreakingNews {
    private String title;
    private String section;
    private String timeAndDate;
    private String url;
    private ArrayList<String> authors;


    BreakingNews(String title, String section, String timeAndDate, String url, ArrayList<String> authors) {
        this.title = title;
        this.section = section;
        this.timeAndDate = timeAndDate;
        this.url = url;
        this.authors = authors;
    }

    String getTitle() {
        return title;
    }

    String getSection() {
        return section;
    }

    String getTimeAndDate() {
        return timeAndDate;
    }

    String getUrl() {
        return url;
    }

    ArrayList<String> getAuthors() {
        return authors;
    }
}
