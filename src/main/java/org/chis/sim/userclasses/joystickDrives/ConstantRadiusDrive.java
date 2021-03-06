package org.chis.sim.userclasses.joystickDrives;

import org.chis.sim.Constants;
import java.lang.Math;

public class ConstantRadiusDrive extends Drive{

    public double targetRadiusReciprocal;
    public boolean isQuickTurn;

    @Override
    public DrivePowers calcPowers(double joystickX, double joystickY, double joystickZ, double leftDist, double rightDist, double leftVelo, double rightVelo){
        super.getConstants();

        if(Math.abs(joystickX) < JOYSTICK_DEADBAND && Math.abs(joystickY) < JOYSTICK_DEADBAND && Math.abs(joystickZ) < JOYSTICK_DEADBAND){
            return new DrivePowers(0, 0);
        }else{
            targetLinVelo = senscurve(-joystickY, SENSCURVE_EXP, MAX_SPEED);
            targetRadiusReciprocal = senscurve(-joystickX, SENSCURVE_EXP, MAX_SPIN);

            if(Math.abs(targetRadiusReciprocal) > SPIN_DEADBAND){
                isGoingStraight = false;
                isQuickTurn = false;
                targetAngVelo = 0.5 * (leftVelo + rightVelo) * targetRadiusReciprocal * Math.copySign(1, targetLinVelo);
            }else if(Math.abs(joystickZ) > SPIN_DEADBAND && Math.abs(targetLinVelo) < 0.1*MAX_SPEED){
                isGoingStraight = false;
                isQuickTurn = true;
                targetAngVelo = senscurve(joystickZ, SENSCURVE_EXP, MAX_SPIN);
            }else{
                isGoingStraight = true;
                isQuickTurn = false;
                targetAngVelo = 0;
            }

            targetDelta = (rightDist - leftDist);
            targetLVelo = targetLinVelo - targetAngVelo * Constants.HALF_DIST_BETWEEN_WHEELS;
            targetRVelo = targetLinVelo + targetAngVelo * Constants.HALF_DIST_BETWEEN_WHEELS;
        
            errorInDelta = targetDelta - (rightDist - leftDist);
            errorInLVelo = targetLVelo - leftVelo;
            errorInRVelo = targetRVelo - rightVelo;

            double lPower = 
                errorInDelta * -DELTA_CORRECTION + 
                errorInLVelo * VELO_CORRECTION +
                errorInRVelo * -OPP_VELO_CORRECTION +
                Math.copySign(FRICTION_RATIO, leftVelo) + leftVelo / MAX_SPEED;
            double rPower = 
                errorInDelta * DELTA_CORRECTION + 
                errorInLVelo * -OPP_VELO_CORRECTION +
                errorInRVelo * VELO_CORRECTION + 
                Math.copySign(FRICTION_RATIO, rightVelo) + rightVelo / MAX_SPEED;

            if(Math.abs(lPower) > 1 || Math.abs(rPower) > 1){
                double biggerValue = Math.max(Math.abs(lPower), Math.abs(rPower));
                lPower = lPower / biggerValue;
                rPower = rPower / biggerValue;
            }
            return new DrivePowers(lPower, rPower);
        }
    }


}