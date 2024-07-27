package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.SimpleCommand;
import me.pilkeysek.skyenetv.SkyeNetV;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PidCommand implements SimpleCommand {

    private long pid;
    /*public PidCommand(long pid) {
        this.pid = pid;
    }*/
    @Override
    public void execute(Invocation invocation) {
        if(!(invocation.source().hasPermission("skyenetv.command.pid"))) {
            invocation.source().sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have the required permissions to execute this command!</red>"));
            return;
        }
        pid = ProcessHandle.current().pid();
        invocation.source().sendMessage(MiniMessage.miniMessage().deserialize("<green>The PID of the Proxy process is </green><dark_aqua><click:copy_to_clipboard:\"" + pid + "\"><hover:show_text:\"<blue>Click to copy!</blue>\">" + pid + "</hover></click></dark_aqua>"));
    }
}
