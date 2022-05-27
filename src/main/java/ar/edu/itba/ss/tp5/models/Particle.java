package ar.edu.itba.ss.tp5.models;

import javafx.util.Pair;

import java.util.Objects;

public class Particle {
    private int id;
    private double xPos;
    private double yPos;
    private double vel;
    private double xVel;
    private double yVel;
    private double color;
    private boolean isHuman;
    private boolean isWall;

    public Particle(int id, double xPos, double yPos, double vel, double color, boolean isHuman, boolean isWall) {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.vel = vel;
        this.color = color;
        this.isHuman = isHuman;
        this.isWall = isWall;
    }

    public Particle(double xPos, double yPos, double vel, double color, boolean isHuman, boolean isWall) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.vel = vel;
        this.color = color;
        this.isHuman = isHuman;
        this.isWall = isWall;
    }

    public double getXPos() {
        return xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public boolean isZombie() {
        return !this.isHuman && !this.isWall;
    }

    public double getVel() {
        return vel;
    }

    public double getxVel() {
        return xVel;
    }

    public double getyVel() {
        return yVel;
    }

    public double getColor() {
        return color;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public boolean isWall() {
        return isWall;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return id == particle.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void move(Pair<Double, Particle> closest) {

    }
}
