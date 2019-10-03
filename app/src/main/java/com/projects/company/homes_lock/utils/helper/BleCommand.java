package com.projects.company.homes_lock.utils.helper;

public class BleCommand {
    private byte[] command;
    private String commandType;

    public BleCommand() {
        this.command = new byte[0];
        this.commandType = null;
    }

    public BleCommand(BleCommand bleCommand) {
        this.command = bleCommand.command;
        this.commandType = bleCommand.commandType;
    }

    public BleCommand(byte[] command, String commandType) {
        this.command = command;
        this.commandType = commandType;
    }

    public byte[] getCommand() {
        return command;
    }

    public void setCommand(byte[] command) {
        this.command = command;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }
}
