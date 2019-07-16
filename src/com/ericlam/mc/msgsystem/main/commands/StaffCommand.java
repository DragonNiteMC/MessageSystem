package com.ericlam.mc.msgsystem.main.commands;

import com.ericlam.mc.bungee.hnmc.commands.caxerx.DefaultCommand;
import com.ericlam.mc.bungee.hnmc.permission.Perm;

public class StaffCommand extends DefaultCommand {

    public StaffCommand() {
        super(null, "msgadmin", Perm.HELPER, "管理員指令", "madmin");
    }
}
