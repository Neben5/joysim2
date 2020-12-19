package org.chis.sim.userclasses.devices;

import org.chis.sim.*;

public class UserMotor {
    Side side;

    public UserMotor(Side side) {
        this.side = side;
    }

    public void setPower(double power) { // ! power must ALWAYS be between [-1.0, 1.0]
        switch (side) {
            case LEFT:
                Main.robot.leftGearbox.setPower(power);
                break;
            case RIGHT:
                Main.robot.rightGearbox.setPower(power);
                break;
        }
    }

    public enum Side {
        LEFT, RIGHT
    }
}
