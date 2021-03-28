package com.company;

public class Law {
    private String name;
    private String status;

    Law(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String toString(){
        return name + " " + status;
    }
}
