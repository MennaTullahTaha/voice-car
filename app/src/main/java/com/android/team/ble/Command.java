package com.android.team.ble;

import java.io.Serializable;


public class Command implements Serializable {
    private String commandDirection;

    private String commandDistance;
    public String getCommandDirection() {
        return commandDirection;
    }
public Command()
{

}
    public Command(String direction,String distance)
    {
        commandDirection=direction;
        commandDistance=distance;
    }

    public void setCommandDirection(String commandDirection) {
        this.commandDirection = commandDirection;
    }

    public String getCommandDistance() {
        return commandDistance;
    }

    public void setCommandDistance(String commandDistance) {
        this.commandDistance = commandDistance;
    }

}
