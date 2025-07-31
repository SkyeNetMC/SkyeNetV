package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.SimpleCommand;

/**
 * ReplyCommand - handles /r and /reply commands by delegating to PrivateMessageCommand
 */
public class ReplyCommand implements SimpleCommand {
    
    private final PrivateMessageCommand privateMessageCommand;
    
    public ReplyCommand(PrivateMessageCommand privateMessageCommand) {
        this.privateMessageCommand = privateMessageCommand;
    }
    
    @Override
    public void execute(Invocation invocation) {
        // Delegate to the reply method in PrivateMessageCommand
        privateMessageCommand.reply(invocation);
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("skyenetv.msg");
    }
}
