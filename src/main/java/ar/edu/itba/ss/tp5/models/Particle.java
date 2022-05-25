package ar.edu.itba.ss.tp5.models;

public class Particle {
    public double xPos;
    public double yPos;
    public double vel;
    public double xVel;
    public double yVel;
    public double color;
    public boolean isHuman;
    public boolean isWall;

    public Particle(double xPos, double yPos, double vel, double color, boolean isHuman, boolean isWall){
        this.xPos = xPos;
        this.yPos = yPos;
        this.vel = vel;
        this.color = color;
        this.isHuman = isHuman;
        this.isWall = isWall;
    }

    public void move(){

    }
}
