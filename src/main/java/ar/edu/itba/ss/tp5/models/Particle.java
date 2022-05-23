package ar.edu.itba.ss.tp5.models;

public class Particle {
    public double xPos;
    public double yPos;
    public double xVel;
    public double yVel;
    public double color;

    public Particle(double xPos, double yPos, double xVel, double yVel, double color){
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVel = xVel;
        this.yVel = yVel;
        this.color = color;
    }
}
