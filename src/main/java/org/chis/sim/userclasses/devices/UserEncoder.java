package org.chis.sim.userclasses.devices;

import org.chis.sim.*;

public class UserEncoder {
    Side side;

    public UserEncoder(Side side) {
        this.side = side;
    }

    public double getVelocity() {
        switch (side) {
            case LEFT:
                return convertEncoder(Main.robot.leftEncoderVelocity());
            case RIGHT:
                return convertEncoder(Main.robot.rightEncoderVelocity());
            default:
                return 0;
        }
    }

    public double getPosition(){
        switch (side) {
            case LEFT:
                return convertEncoder(Main.robot.leftEncoderPosition());
            case RIGHT:
                return convertEncoder(Main.robot.rightEncoderPosition());
            default:
                return 0;
        }
    }

    private static double convertEncoder(double encoder) {
        return encoder / Constants.TICKS_PER_REV.getDouble() / Constants.GEAR_RATIO.getDouble() * 2 * Math.PI
                * Constants.WHEEL_RADIUS.getDouble();
    }

    public enum Side {
        LEFT, RIGHT
    }
}