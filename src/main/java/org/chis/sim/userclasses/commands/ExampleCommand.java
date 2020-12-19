package org.chis.sim.userclasses.commands;

import org.chis.sim.commandBased.*;
import org.chis.sim.userclasses.subsystems.ExampleSubsystem;

public class ExampleCommand extends CommandBase {

    ExampleSubsystem subsystem;

    public ExampleCommand(ExampleSubsystem subsystem) {
        this.subsystem = subsystem;
    }

    @Override
    public void execute() {
        subsystem.setPower(0, 0);
    }
}