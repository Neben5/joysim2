package org.chis.sim;

import org.chis.sim.GraphicDebug.Serie;
import org.chis.sim.userclasses.devices.UserEncoder;
import org.chis.sim.userclasses.devices.UserEncoder.Side;

import java.awt.Color;

public class Debugger {
    UserEncoder[] encoders;

    Serie leftVeloSerie;
    Serie rightVeloSerie;
    Serie joystickSerie;

    GraphicDebug printOutWindow;
    GraphicDebug velocityWindow;
    GraphicDebug joystickWindow;

    public Debugger() {
        encoders = new UserEncoder[] { new UserEncoder(Side.LEFT), new UserEncoder(Side.RIGHT) };

        leftVeloSerie = new Serie(Color.BLUE, 3);
        rightVeloSerie = new Serie(Color.RED, 3);
        joystickSerie = new Serie(Color.GRAY, 10);

        printOutWindow = new GraphicDebug();
        velocityWindow = new GraphicDebug("Velocity", 200, true, leftVeloSerie, rightVeloSerie);
        joystickWindow = new GraphicDebug("Joystick Position", 1, false, joystickSerie);
    }

    public void update() {
        double[] velocityValues = { encoders[0].getVelocity(), encoders[1].getVelocity() };
        double[] joystickValues = { Controls.rawX, -Controls.rawY };
        double[] robotValues = { Main.robot.x, Main.robot.y, Main.robot.heading };

        putVals(velocityValues, joystickValues, robotValues);
    }

    private void putVals(double[] velocityValues, double[] joystickValues, double[] robotValues) {
        leftVeloSerie.addPoint(Main.elaspedTime, velocityValues[0]);
        rightVeloSerie.addPoint(Main.elaspedTime, velocityValues[1]);

        joystickSerie.addPoint(joystickValues[0], joystickValues[1]);

        printOutWindow.putNumber("x", robotValues[0]);
        printOutWindow.putNumber("y", robotValues[1]);
        printOutWindow.putNumber("Heading", robotValues[2]);
    }
}