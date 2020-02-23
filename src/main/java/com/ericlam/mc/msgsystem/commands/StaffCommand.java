package com.ericlam.mc.msgsystem.commands;

import com.ericlam.mc.bungee.hnmc.commands.caxerx.DefaultCommand;
import com.ericlam.mc.bungee.hnmc.permission.Perm;
import com.ericlam.mc.msgsystem.commands.staff.AnnounceCommand;
import com.ericlam.mc.msgsystem.commands.staff.BroadcastCommand;
import com.ericlam.mc.msgsystem.commands.staff.ChatSpyCommand;
import com.ericlam.mc.msgsystem.commands.staff.IgnoreListCommand;

public class StaffCommand extends DefaultCommand {

    public StaffCommand() {
        super(null, "msgadmin", Perm.HELPER, "管理員指令", "madmin");
        this.addSub(new AnnounceCommand(this));
        this.addSub(new BroadcastCommand(this));
        this.addSub(new ChatSpyCommand(this));
        this.addSub(new IgnoreListCommand(this));
    }
}
