package org.chis.sim.userclasses.subsystems;

import org.chis.sim.commandBased.SubsystemBase;
import org.chis.sim.userclasses.devices.UserEncoder;
import org.chis.sim.userclasses.devices.UserMotor;

public class ExampleSubsystem extends SubsystemBase {
    UserMotor leftMotor;
    UserMotor rightMotor;
    UserEncoder leftEncoder;
    UserEncoder rightEncoder;

    public ExampleSubsystem(){
        leftMotor = new UserMotor(UserMotor.Side.LEFT);
        rightMotor = new UserMotor(UserMotor.Side.RIGHT);
        leftEncoder = new UserEncoder(UserEncoder.Side.LEFT);
        rightEncoder = new UserEncoder(UserEncoder.Side.RIGHT);
    }

    public void setPower(double left, double right){
        leftMotor.setPower(left);
        rightMotor.setPower(right);
    }
}