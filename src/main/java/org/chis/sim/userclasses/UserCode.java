package org.chis.sim.userclasses;

import java.util.ArrayList;

import org.chis.sim.*;
import org.chis.sim.commandBased.*;
import org.chis.sim.userclasses.subsystems.*;
import org.chis.sim.userclasses.commands.*;

public class UserCode {
    static UserCode instance;
    static Debugger debugger;

    static ArrayList<CommandBase> commands = new ArrayList<>();
    ExampleSubsystem exampleSubsystem;
    public static void initialize() { // this function is run once when the robot starts
        instance = new UserCode(); // this is the object that runs your code and contains your subsystems and
                                   // commands
        debugger = new Debugger(); // this object handles debug info
    }

    UserCode() {
        // ! instantiate commands and subsystems here
        exampleSubsystem = new ExampleSubsystem();
        commands.add(new ExampleCommand(exampleSubsystem));
    }

    public static void execute() { // this function is run 50 times a second (every 0.02 second)
        for (Command command : commands) {
            command.execute();
        }
        debugger.update(); // ! keep this
    }

    public static UserCode getInstance() {
        return instance;
    }

    public static Debugger getDebugger() {
        return debugger;
    }

}