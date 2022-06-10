package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.FilePositionGenerator;
import ar.edu.itba.ss.tp5.models.Particle;
import ar.edu.itba.ss.tp5.models.Wall;

import java.util.*;

import static ar.edu.itba.ss.tp5.App.*;

public class SimulationController {
    public static List<Particle> particles = new ArrayList<>();
    public static List<Wall> walls = new ArrayList<>();

    public static double tau = 0.5;

    public static double vdMin = 0;
    public static double vdMax = 4;

    public static double vzMin = 0.3;
    public static double vzMax = 3;
    public static double ve = 4;
    public static double rMin = 0.15;
    public static double rMax = 0.30;

    public static double deltaT = rMin / (2 * Math.max(vdMax, ve));
    public static double deltaR = rMax / (tau / deltaT);

    public static double humanInteractionDistance =4;
    public static double zombieInteractionDistance = 4;

    FilePositionGenerator filePositionGenerator;

    public static double zombieColor = 1;
    private double humanColor = 0.5;
    private double wallColor = 0.75;

    public static int zombies = 1;

    public static double RECOVERY_POSSIBILITY = 0.01;

    public SimulationController(FilePositionGenerator filePositionGenerator) {
        this.filePositionGenerator = filePositionGenerator;
        generateMap();
    }

    public void generateMap() {
        double randomX, randomY;
        while (particles.size() < N) {
            double randomAngle = Math.random() * (360);
            double randomRadius = Math.random() * (circleRadius - 1) + 1;
            randomX = circleRadius + randomRadius * Math.cos(randomAngle);
            randomY = circleRadius + randomRadius * Math.sin(randomAngle);
            if (!particleOverlaps(randomX, randomY)) {
                particles.add(new Particle(particles.size(), randomX, randomY, vdMax, rMin, humanColor, true));
            }
        }

        // agrega al zombie
        particles.add(new Particle(particles.size(), circleRadius, circleRadius, vzMin, rMin, zombieColor, false));

        // agrega las paredes
        for (double j = 0; j < 360.0; j += 0.1) {
            walls.add(new Wall(circleRadius + circleRadius * Math.cos(j), circleRadius + circleRadius * Math.sin(j), wallColor, rMin));
        }
        filePositionGenerator.addWalls(particles);
        filePositionGenerator.addParticles(particles);
    }

    public boolean particleOverlaps(double x, double y) {
        for (Particle particle : particles) {
            if ((Math.abs(x - particle.getXPos()) < 0) && (Math.abs(y - particle.getYPos()) < 0)) {
                return true;
            }
        }
        return false;
    }

    public static double getRandom(double min, double max) {
        return (Math.random() * (max - min + 1) + min);
    }

    public void simulate() {
        CPM.run(filePositionGenerator);
    }

    public static boolean cutCondition() {
        return (double) zombies / N >= 0.75;
    }
}
