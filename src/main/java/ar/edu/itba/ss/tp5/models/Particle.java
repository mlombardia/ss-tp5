package ar.edu.itba.ss.tp5.models;

import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private final boolean isHuman;
    private final boolean isWall;

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
        double angle = getRandom(0, 360);
        double angleInRadians = angle * Math.PI / 180.0;
        this.xVel = Math.cos(angleInRadians) * vel;
        this.yVel = Math.sin(angleInRadians) * vel;
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

    public boolean isAtContact() {
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

    private void setNewDirection(Particle particle){
        double angleX = Math.acos(particle.getXVel() / particle.vel);
        double angleY = Math.asin(particle.getYVel() / particle.vel);
        this.xVel = this.vel * Math.cos(angleX);
        this.yVel = this.vel * Math.sin(angleY);
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
    }

    private void avoidWalls(Particle wall) {
        if (Math.abs(wall.getXPos() - this.getXPos()) < interactionDistance / 2) { //si va a chocar horizontalmente
            if (Math.abs(wall.getYPos() - this.getYPos()) < interactionDistance / 2) { // y tambien verticalmente
                this.xVel = -this.xVel; // sale para el otro lado
                this.yVel = -this.yVel;
            } else { //solo horizontal
                this.xVel = -this.xVel; //solo cambia su velocidad en x
            }
        } else if (Math.abs(wall.getYPos() - this.getYPos()) < interactionDistance / 2) { //solo vertical
            this.yVel = -this.yVel; //solo cambia su velocidad en y
        }
    }

    private List<Particle> getZombiesPersecutingHuman(Particle human) {
        return persecutions
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), human))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void avoidZombies(List<Particle> zombies, Particle obstacle, double obstacleDistance) {
        if (zombies.size() > 0){
            Particle closestZombie = zombies.get(0);
            double distance = calculateDistance(this, zombies.get(0));
            for (Particle zombie: zombies){
                double aux = calculateDistance(this, zombie);
                if (aux < distance){
                    closestZombie = zombie;
                    distance = aux;
                }
            }
            if (distance > obstacleDistance){
                setNewDirection(closestZombie); //solo escapar del zombie
            }else{
                //TODO setTemporalTarget //evitar al zombie y la pared/humano
            }
            //play with the obstacle and the closest zombie
        }
    }

    public void move(Pair<Double, Particle> closest) {
        double distance = closest.getKey();
        Particle particle = closest.getValue();

        if (this.isZombie()) {
            if (distance > interactionDistance || particle.isZombie() || particle.isWall()) {
                this.vel = zombieVelocity;
                if (particle.isZombie() || particle.isWall()) { //si lo que tiene mas cerca es otro zombie o una pared
                    Particle p = getRandomTarget(); //busca un humano random para perseguir
                    persecuteHuman(p);
                } else {
                    persecuteHuman(particle);
                }
                persecutions.remove(this);
            } else {
                this.vel = persecutionZombieVelocity;
                persecutions.put(this, particle); //zombie, human
                persecuteHuman(particle);
            }
        } else if (this.isHuman()) {
            if (persecutions.containsValue(this)) { //si esta siendo perseguido
                List<Particle> dangerousZombies = getZombiesPersecutingHuman(this);
                avoidZombies(dangerousZombies, particle, distance);
            } else if (distance > humanInteractionDistance || distance == 0) { //si no tiene nada cerca o se choco
                this.vel = 0;
                this.xVel = 0;
                this.yVel = 0;
            } else {
                if (particle.isHuman()) {
                    repelHumans(particle);
                } else if (particle.isWall()) {
                    avoidWalls(particle);
                }
            }
        }

    }


}
