package com.ferru97.beatbracelet.data;

public class Bracelet {
    private  String id;
    private String name;
    private String last_activity;

    public Bracelet(String id, String name, String last_activity) {
        this.id = id;
        this.name = name;
        this.last_activity = last_activity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLast_activity() {
        return last_activity;
    }
}
