package ar.edu.itba.ss.tp5.models;

import ar.edu.itba.ss.tp5.simulation.Dynamics;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.ss.tp5.simulation.SimulationController.*;

public class Particle {
    private int id;
    private double xPos;
    private double yPos;
    private double vel;
    private double xVel;
    private double yVel;
    private final double color;
    private boolean isHuman;
    private final boolean isWall;
    private boolean isAtContactWithHuman;

    public Pair<Double, Double> currentTarget;

    public Pair<Double, Double> originalTarget;

    public Pair<Double, Double> temporalTarget;

    private double radius;

    private long collisionTime = 0;

    private boolean isWaiting = false; //esta comiendo o siendo comido

    public boolean isVertical;

    public Particle(int id, double xPos, double yPos, double vel, double radius, double color, boolean isHuman, boolean isWall) {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.vel = vel;
        this.radius = radius;
        this.color = color;
        this.isHuman = isHuman;
        this.isWall = isWall;
        double angle = getRandom(0, 360);
        double angleInRadians = angle * Math.PI / 180.0;
        this.xVel = Math.cos(angleInRadians) * vel;
        this.yVel = Math.sin(angleInRadians) * vel;
        this.originalTarget = new Pair<>(xPos, yPos);
        this.currentTarget = originalTarget;
        this.temporalTarget = originalTarget;
        this.isVertical = false;
        this.isAtContactWithHuman = false;
        this.currentTarget = null;
    }

    public Particle(double xPos, double yPos, double vel, double radius, double color, boolean isHuman, boolean isWall) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.vel = vel;
        this.radius = radius;
        this.color = color;
        this.isHuman = isHuman;
        this.isWall = isWall;
        double angle = getRandom(0, 360);
        double angleInRadians = angle * Math.PI / 180.0;
        this.xVel = Math.cos(angleInRadians) * vel;
        this.yVel = Math.sin(angleInRadians) * vel;
        this.originalTarget = new Pair<>(xPos, yPos);
        this.currentTarget = originalTarget;
        this.temporalTarget = originalTarget;
        this.isAtContactWithHuman = false;
        this.currentTarget = null;
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

    public void setHuman(boolean human) {
        isHuman = human;
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

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getColor() {
        return color;
    }

    public boolean isHuman() {
        return isHuman && !isWall;
    }

    public boolean isWall() {
        return isWall;
    }

    public boolean isAtContactWithHuman() {
        return isAtContactWithHuman;
    }

    public void setIsAtContactWithHuman(boolean bool){
        this.isAtContactWithHuman = bool;
    }

    public long getCollisionTime() {
        return collisionTime;
    }

    public void setCollisionTime(long collisionTime) {
        this.collisionTime = collisionTime;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
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

    private void setNewDirection(Particle particle) {
        double angleX = Math.acos(particle.getXVel() / particle.vel);
        double angleY = Math.asin(particle.getYVel() / particle.vel);
        this.xVel = this.vel * Math.cos(angleX);
        this.yVel = this.vel * Math.sin(angleY);
        Dynamics.reRoute(particle);
    }

    private void persecuteHuman(Particle human) {
        // ir en la direccion de la particula que persigo
        setNewDirection(human);
    }

    private void repelHumans(Particle human) {
        // <-- • • --> se repelen en direcciones opuestas
        double firstHumanAngle;
        double secondHumanAngle;
        if (this.xPos > human.xPos) { // si el first human (this) se encuentra mas a la derecha
            firstHumanAngle = 0;
            secondHumanAngle = 180;
        } else {
            firstHumanAngle = 180;
            secondHumanAngle = 0;
        }
        double firstAngleInRadians = firstHumanAngle * Math.PI / 180.0;
        this.xVel = Math.cos(firstAngleInRadians) * this.vel;
        this.yVel = Math.sin(firstAngleInRadians) * this.vel;
        double secondAngleInRadians = secondHumanAngle * Math.PI / 180.0;
        human.xVel = Math.cos(secondAngleInRadians) * human.vel;
        human.yVel = Math.sin(secondAngleInRadians) * human.vel;
        if (firstHumanAngle == 0){ // <-- H T -->
            this.originalTarget = new Pair(this.xPos + 2.0, this.yPos);
            this.temporalTarget = this.originalTarget;
            human.originalTarget = new Pair(this.xPos - 2.0, this.yPos);
            human.temporalTarget = human.originalTarget;
        } else {                   // <-- T H -->
            this.originalTarget = new Pair(this.xPos - 2.0, this.yPos);
            this.temporalTarget = this.originalTarget;
            human.originalTarget = new Pair(this.xPos + 2.0, this.yPos);
            human.temporalTarget = human.originalTarget;
        }
    }

    private void avoidWalls(Particle wall) {
        if (Math.abs(wall.getXPos() - this.getXPos()) < interactionDistance / 2) { //si va a chocar horizontalmente
            if (Math.abs(wall.getYPos() - this.getYPos()) < interactionDistance / 2) { // y tambien verticalmente
                this.xVel = -this.xVel; // sale para el otro lado
                this.yVel = -this.yVel;
                this.originalTarget = new Pair(this.xPos + 2 * (this.xVel/Math.abs(this.xVel)), this.yPos + 2 * (this.yVel/Math.abs(this.yVel)));
                this.temporalTarget = this.originalTarget;
            } else { //solo horizontal
                this.xVel = -this.xVel; //solo cambia su velocidad en x
                this.originalTarget = new Pair(this.xPos + (2 * (this.xVel/Math.abs(this.xVel))), this.yPos);
                this.temporalTarget = this.originalTarget;
            }
        } else if (Math.abs(wall.getYPos() - this.getYPos()) < interactionDistance / 2) { //solo vertical
            this.yVel = -this.yVel; //solo cambia su velocidad en y
            this.originalTarget = new Pair(this.xPos, this.yPos + (2 * (this.yVel/Math.abs(this.yVel))));
            this.temporalTarget = this.originalTarget;
        }
    }

    private List<Particle> getZombiesPersecutingHuman(Particle human) {
        return persecutions.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), human)).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private void avoidZombies(List<Particle> zombies, Particle obstacle, double obstacleDistance) {
        if (zombies.size() > 0) {
            Particle closestZombie = zombies.get(0);
            double distance = calculateDistance(this, zombies.get(0));
            for (Particle zombie : zombies) {
                double aux = calculateDistance(this, zombie);
                if (aux < distance) {
                    closestZombie = zombie;
                    distance = aux;
                }
            }
            this.temporalTarget = new Pair(this.xPos + 2 * (closestZombie.getXVel()/Math.abs(closestZombie.getXVel())), this.yPos + 2 * (closestZombie.getYVel()/Math.abs(closestZombie.getYVel())));
            if (obstacleDistance > humanInteractionDistance) {
                setNewDirection(closestZombie); //solo escapar del zombie
            } else {
                Dynamics.reRoute(this);
                //TODO setTemporalTarget //evitar al zombie y la pared/humano
            }
            //play with the obstacle and the closest zombie
        }
    }

    //CREO QUE TENDRIA QUE ESTAR MOVIENDOSE RANDOM Y SI ENCUENTR UNO EN DISTANCE<RADIO QUE VE
    //LO EMPIEZA A PERSEGUIR
    private void persecuteRandomHuman() {
        Particle p = getRandomTarget(); //busca un humano random para perseguir
        persecuteHuman(p);
    }

    private void setVelocityToZero(Particle particle) {
        particle.vel = 0;
        particle.xVel = 0;
        particle.yVel = 0;
    }

    public void move(Pair<Double, Particle> closest) {
        double distance = closest.getKey();
        Particle particle = closest.getValue();
        if (this.isWaiting()) { //si esta siendo comido o comiendo
            if (System.currentTimeMillis() - this.getCollisionTime() > 7000) {
                this.setWaiting(false);             //lo convierte en zombie
                this.setHuman(false);
                persecuteRandomHuman();
            }
        } else {
            if (this.isZombie()) {
                if (distance > interactionDistance || particle.isZombie() || particle.isWall()) {
                    //this.vel = zombieVelocitySlow;
                    persecuteRandomHuman();
                    persecutions.remove(this);
                } else if(distance == 0){               //si choco
                    this.setWaiting(true);
                } else {            // tiene a un humano cerca
                    //this.vel = persecutionZombieVelocity;
                    persecutions.put(this, particle); //zombie, human
                    persecuteHuman(particle);
                }
            } else if (this.isHuman()) {
                if (persecutions.containsValue(this)) { //si esta siendo perseguido
                    List<Particle> dangerousZombies = getZombiesPersecutingHuman(this);
                    avoidZombies(dangerousZombies, particle, distance);
                } else if (distance > humanInteractionDistance) { //si no tiene nada cerca
                    //setVelocityToZero(this);
                } else if (distance == 0) {     //si se choco
                    if (particle.isZombie()) {          //si choco con un zombie
                        this.setWaiting(true);
                        this.setCollisionTime(System.currentTimeMillis());
                        particle.setCollisionTime(System.currentTimeMillis());
                    }
                    else if(particle.isHuman){
                        this.setIsAtContactWithHuman(true);
                    }
                } else {
                    if (particle.isHuman()) {
                        repelHumans(particle);
                    } else if (particle.isWall()) {
                        avoidWalls(particle);
                    }
                }
            }
        }
        this.currentTarget = Dynamics.checkStatus(this);
        Dynamics.cpm(this, this.currentTarget);
    }

}
