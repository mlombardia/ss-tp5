package ar.edu.itba.ss.tp5.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static ar.edu.itba.ss.tp5.App.*;
import static ar.edu.itba.ss.tp5.simulation.SimulationController.*;


public class FilePositionGenerator {
    private FileWriter fw1;
    private FileWriter fw2;
    private final BufferedWriter buffer1;
    private final BufferedWriter buffer2;


    public FilePositionGenerator() {
        try {
            FileWriter pw1 = new FileWriter("ParticlesPosition.xyz");
            FileWriter pw2 = new FileWriter("WallsPosition.xyz");

            pw1.close();
            pw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.fw1 = new FileWriter("ParticlesPosition.xyz", true);
            this.fw2 = new FileWriter("WallsPosition.xyz", true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.buffer1 = new BufferedWriter(fw1);
            this.buffer2 = new BufferedWriter(fw2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addWalls(List<Particle> particles) {
        try {
            buffer2.write(walls.size() + "\n");
            buffer2.write("XPosition YPosition Radius Color\n");
            for (Wall wall : walls) {
                buffer2.write(wall.getXPos() + " " + wall.getYPos() + " " + wall.getRadius() + " " + wall.getColor() + " " + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addParticles(List<Particle> particles) {
        try {
            buffer1.write(particles.size() + "\n");
            buffer1.write("XPosition YPosition Radius Color\n");
            for (Particle particle : particles) {
                buffer1.write(particle.getXPos() + " " + particle.getYPos() + " " + particle.getRadius() + " " + particle.getColor() + " " + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFiles() {
        try {
            buffer1.close();
            buffer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
