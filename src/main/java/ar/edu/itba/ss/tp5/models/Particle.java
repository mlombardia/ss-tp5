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
    private double color;
    private boolean isHuman;
    private boolean isZombie;
    private final boolean isWall;
    private boolean isAtContactWithHuman;
    private boolean isParticleSleeping;
    public int hitWall = 1;
    public Pair<Double, Double> currentTarget;

    public Pair<Double, Double> originalTarget;

    public Pair<Double, Double> temporalTarget;

    private double radius;

    private long collisionTime = 0;

    private boolean isWaiting = false; //esta comiendo o siendo comido

    public boolean isVertical;

    public Particle(int id, double xPos, double yPos, double vel, double radius, double color, boolean isHuman, boolean isWall, boolean isZombie) {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.vel = vel;
        this.radius = radius;
        this.color = color;
        this.isHuman = isHuman;
        this.isWall = isWall;
        this.isZombie = isZombie;
        double angle = getRandom(0, 360);
        double angleInRadians = angle * Math.PI / 180.0;
        this.xVel = Math.cos(angleInRadians) * vel;
        this.yVel = Math.sin(angleInRadians) * vel;
        this.originalTarget = new Pair<>(xPos, yPos);
        this.currentTarget = originalTarget;
        this.temporalTarget = originalTarget;
        this.isVertical = false;
        this.isAtContactWithHuman = false;
        this.isParticleSleeping = true;
    }

    public Particle(double xPos, double yPos, double vel, double radius, double color, boolean isHuman, boolean isWall, boolean isZombie) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.vel = vel;
        this.radius = radius;
        this.color = color;
        this.isHuman = isHuman;
        this.isWall = isWall;
        this.isZombie = isZombie;
        double angle = getRandom(0, 360);
        double angleInRadians = angle * Math.PI / 180.0;
        this.xVel = Math.cos(angleInRadians) * vel;
        this.yVel = Math.sin(angleInRadians) * vel;
        this.originalTarget = new Pair<>(xPos, yPos);
        this.currentTarget = originalTarget;
        this.temporalTarget = originalTarget;
        this.isAtContactWithHuman = false;
        this.isParticleSleeping = true;
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
        return this.isZombie;
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
        if(human ==false){
            this.isZombie = true;
        }
    }

    public void setParticleSleeping(boolean bool) {
        isParticleSleeping = bool;
    }

    public boolean isParticleSleeping() {
        return isParticleSleeping;
    }

    public double getYVel() {
        return yVel;
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

    public int getId() {
        return id;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public boolean isWall() {
        return isWall;
    }

    public boolean isAtContactWithHuman() {
        return isAtContactWithHuman;
    }

    public void setIsAtContactWithHuman(boolean bool) {
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

    public boolean isOutOfBounds(){
        return Math.sqrt(Math.pow(this.getXPos() - circleRadius, 2) + Math.pow(this.getYPos() - circleRadius, 2)) > circleRadius;
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
        /*double angleX = Math.acos(particle.getXVel() / particle.getVel());
        double angleY = Math.asin(particle.getYVel() / particle.getVel());
        if (particle.getVel() == 0) {
            angleX = 0;
            angleY = 0;
        }
        this.xVel = this.vel * Math.cos(angleX);
        this.yVel = this.vel * Math.sin(angleY);

        //Dynamics.reRoute(particle);
        if (this.isZombie()) {
            this.xPos = this.xPos + this.xVel * deltaT;
            this.yPos = this.yPos + this.yVel * deltaT;
        }*/

        double difx = Math.abs(particle.getXPos() - this.getXPos());
        double dify = Math.abs(particle.getYPos() - this.getYPos());

        double a = Math.tan(dify/difx);
        this.xVel = this.vel * Math.cos(-a);
        this.yVel = this.vel * Math.sin(-a);

        if (this.isZombie()) {
            this.xPos = this.xPos + this.xVel * deltaT;
            this.yPos = this.yPos + this.yVel * deltaT;
        }

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
        if (firstHumanAngle == 0) { // <-- H T -->
            this.originalTarget = new Pair<>(this.xPos + 2.0, this.yPos);
            this.temporalTarget = this.originalTarget;
            human.originalTarget = new Pair<>(this.xPos - 2.0, this.yPos);
            human.temporalTarget = human.originalTarget;
        } else {                   // <-- T H -->
            this.originalTarget = new Pair<>(this.xPos - 2.0, this.yPos);
            this.temporalTarget = this.originalTarget;
            human.originalTarget = new Pair<>(this.xPos + 2.0, this.yPos);
            human.temporalTarget = human.originalTarget;
        }
    }

    private void avoidWalls(Particle wall) {
        if (Math.abs(wall.getXPos() - this.getXPos()) < interactionDistance / 2) { //si va a chocar horizontalmente
            if (Math.abs(wall.getYPos() - this.getYPos()) < interactionDistance / 2) { // y tambien verticalmente
                this.xVel = -this.xVel; // sale para el otro lado
                this.yVel = -this.yVel;
                this.originalTarget = new Pair<>(this.xPos + 2 * (this.xVel / Math.abs(this.xVel)), this.yPos + 2 * (this.yVel / Math.abs(this.yVel)));
                this.temporalTarget = this.originalTarget;
            } else { //solo horizontal
                this.xVel = -this.xVel; //solo cambia su velocidad en x
                this.originalTarget = new Pair<>(this.xPos + (2 * (this.xVel / Math.abs(this.xVel))), this.yPos);
                this.temporalTarget = this.originalTarget;
            }
        } else if (Math.abs(wall.getYPos() - this.getYPos()) < interactionDistance / 2) { //solo vertical
            this.yVel = -this.yVel; //solo cambia su velocidad en y
            this.originalTarget = new Pair<>(this.xPos, this.yPos + (2 * (this.yVel / Math.abs(this.yVel))));
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
            this.temporalTarget = new Pair<>(this.xPos + 2 * (closestZombie.getXVel() / Math.abs(closestZombie.getXVel())), this.yPos + 2 * (closestZombie.getYVel() / Math.abs(closestZombie.getYVel())));
            //if (obstacleDistance > humanInteractionDistance) {
                //setNewDirection(closestZombie); //solo escapar del zombie
            //} else {
                //Dynamics.reRoute(this);
                //TODO setTemporalTarget //evitar al zombie y la pared/humano
           // }
            //play with the obstacle and the closest zombie
        }
    }

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
                this.color = zombieColor;
                countZombies++;
                this.vel = zombieVelocitySlow;
                if(this.isZombie()){
                    persecutions.remove(this);
                }
                persecuteRandomHuman();
            }
        } else {
            if (this.isZombie()) {
                if (distance > interactionDistance || particle.isZombie() || particle.isWall()) { // particle.isZombie no tendria sentido porque se
                    this.vel = zombieVelocitySlow;                                                 //descarto en checkProximity
                    persecuteRandomHuman();
                    persecutions.remove(this);
                }
                if (distance <= 0.1) {               //si choco
                    if(particle.isHuman){                   //choca con humano
                        this.setWaiting(true);
                        this.vel = 0;
                        this.xVel = 0;
                        this.yVel = 0;
                    }
                    else if(hitWall==1){                                     //choca con wall
                        hitsWall(this);
                        hitWall =0;
                    }

                } else {            // tiene a un humano cerca
                    this.vel = persecutionZombieVelocity;
                    persecutions.put(this, particle); //zombie, human
                    persecuteHuman(particle);
                }
            } else if (this.isHuman()) {
                if (persecutions.containsValue(this)) { //si esta siendo perseguido
                    List<Particle> dangerousZombies = getZombiesPersecutingHuman(this);
                    setParticleSleeping(false);
                    avoidZombies(dangerousZombies, particle, distance);
                } else if (distance > humanInteractionDistance) { //si no tiene nada cerca
                    setVelocityToZero(this);
                    setParticleSleeping(true);
                } else if (distance <= 1) {     //si se choco
                    if (particle.isZombie()) {          //si choco con un zombie
                        this.setWaiting(true);
                        setVelocityToZero(this);
                        this.setCollisionTime(System.currentTimeMillis());
                        particle.setCollisionTime(System.currentTimeMillis());
                    } else if (particle.isHuman) {
                        this.setIsAtContactWithHuman(true);
                    }else if(hitWall == 0.1){
                        hitsWall(this);
                        hitWall = 0;
                    }
                } else {
                    if (particle.isHuman() && !this.isParticleSleeping ) {
                        repelHumans(particle);
                    } else if (particle.isWall() && !this.isParticleSleeping) {
                        avoidWalls(particle);
                    }
                }
            }
        }
        this.currentTarget = Dynamics.checkStatus(this);
        Dynamics.cpm(this, this.currentTarget);
    }

    public void hitsWall(Particle particle){
        particle.vel = -(particle.getVel());
        particle.xVel = -(particle.getXVel());
        particle.yVel = -(particle.getYVel());
    }

}
