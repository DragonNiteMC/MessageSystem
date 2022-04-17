package com.ericlam.mc.msgsystem.commands.staff;

import com.ericlam.mc.bungee.dnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.dnmc.commands.caxerx.DefaultCommand;
import com.ericlam.mc.bungee.dnmc.permission.Perm;
import com.ericlam.mc.msgsystem.commands.staff.announce.AnnounceCheckCommand;
import com.ericlam.mc.msgsystem.commands.staff.announce.AnnounceListCommand;
import com.ericlam.mc.msgsystem.commands.staff.announce.AnnounceSendCommand;

public class AnnounceCommand extends DefaultCommand {
    public AnnounceCommand(CommandNode parent) {
        super(parent, "announce", Perm.ADMIN, "公告指令列表", "announcement");
        this.addSub(new AnnounceCheckCommand(this));
        this.addSub(new AnnounceListCommand(this));
        this.addSub(new AnnounceSendCommand(this));
    }

}
