package com.mygdx.game.Entity;

public class Astronaut extends Entity {
    public int id;
    private String color;

    public Astronaut() {
        super();
    }

    public Astronaut(int id, String color) {
        super(260, 230);
        this.id = id;
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
