package ar.edu.itba.ss.tp5.models;

import java.util.Objects;

import static ar.edu.itba.ss.tp5.App.*;
import static ar.edu.itba.ss.tp5.simulation.SimulationController.*;

public class Particle {
    private final int id;
    private double xPos;
    private double yPos;
    private double xVel;
    private double yVel;
    private double color;
    private double radius;
    private boolean isHuman;
    private double biteTime = NOT_BITTEN;
    private double attackAttempts = 0;

    public Particle(int id, double xPos, double yPos, double vel, double radius, double color, boolean isHuman) {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        double angle = getRandom(0, 360);
        double angleInRadians = angle * Math.PI / 180.0;
        this.xVel = Math.cos(angleInRadians) * vel;
        this.yVel = Math.sin(angleInRadians) * vel;
        this.color = color;
        this.radius = radius;
        this.isHuman = isHuman;
    }

    public double getXPos() {
        return xPos;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
    }

    public double getXVel() {
        return xVel;
    }

    public void setXVel(double xVel) {
        this.xVel = xVel;
    }

    public double getYVel() {
        return yVel;
    }

    public int getId() {
        return id;
    }

    public void setYVel(double yVel) {
        this.yVel = yVel;
    }

    public double getColor() {
        return color;
    }

    public void setColor(double color) {
        this.color = color;
    }

    public double getRadius() {
        return radius;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public void setHuman(boolean human) {
        if (!human) setColor(zombieColor);
        isHuman = human;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getBiteTime() {
        return biteTime;
    }

    public void setBiteTime(double biteTime) {
        this.biteTime = biteTime;
    }

    public double getAttackAttempts() {
        return attackAttempts;
    }

    public void setAttackAttempts(double attackAttempts) {
        this.attackAttempts = attackAttempts;
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

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}