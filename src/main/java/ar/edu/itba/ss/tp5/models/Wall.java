package ar.edu.itba.ss.tp5.models;

public class Wall {
    private final double xPos;
    private final double yPos;
    private final double color;

    private final double radius;


    public Wall(double xPos, double yPos, double color, double radius) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.color = color;
        this.radius = radius;
    }

    public double getXPos() {
        return xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public double getColor() {
        return color;
    }

    public double getRadius() {
        return radius;
    }

}