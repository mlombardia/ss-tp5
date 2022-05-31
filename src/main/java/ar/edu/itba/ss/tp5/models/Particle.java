package ar.edu.itba.ss.tp5.models;

import javafx.util.Pair;

import java.util.Objects;

import static ar.edu.itba.ss.tp5.simulation.SimulationController.*;

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

    private double radius;

    public Particle(int id, double xPos, double yPos, double vel, double radius, double color, boolean isHuman, boolean isWall) {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.vel = vel;
        this.radius = radius;
        this.color = color;
        this.isHuman = isHuman;
        this.isWall = isWall;
    }

    public Particle(double xPos, double yPos, double vel, double radius, double color, boolean isHuman, boolean isWall) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.vel = vel;
        this.radius = radius;
        this.color = color;
        this.isHuman = isHuman;
        this.isWall = isWall;
    }

    public double getXPos() {
        return this.xPos;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
    }

    public double getYPos() {
        return this.yPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
    }

    public boolean isZombie() {
        return !this.isHuman && !this.isWall;
    }

    public double getVel() {
        return vel;
    }

    public void setVel(double vel) {
        this.vel = vel;
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

    public void setYVel(double yVel) {
        this.yVel = yVel;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius){
        this.radius = radius;
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

    public boolean isAtContact(){
        // WIP :'(
        return true;
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
        double distance = closest.getKey();
        Particle particle = closest.getValue();

        if (this.isZombie()) {
            if (distance > interactionDistance) {
                this.vel = zombieVelocity;
                // ir por targets randoms
                persecutions.remove(this);
            } else {
                this.vel = persecutionZombieVelocity;
                // ir en la direccion de la particula que persigo
                persecutions.put(this, particle); //zombie, human
            }
        } else {
            if (persecutions.containsValue(particle)) { //si esta siendo perseguido

            } else if (distance > interactionDistance) { //si no tiene nada cerca
                this.vel = 0;
            } else { //si tiene otro humano cerca -> se repelen

            }
        }

    }
}
