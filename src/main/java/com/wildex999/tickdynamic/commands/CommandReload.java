package com.wildex999.tickdynamic.commands;

import java.util.List;

import com.wildex999.tickdynamic.TickDynamicMod;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandReload implements ICommand {

	private TickDynamicMod mod;
	
	public CommandReload(TickDynamicMod mod) {
		this.mod = mod;
	}

	@Override
	public String getCommandName() {
		return "tickdynamic reload";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "tickdynamic reload";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		sender.addChatMessage(new ChatComponentText("Reloading configuration..."));
		sender.addChatMessage(new ChatComponentText("Note: Moving of (tile)entities to new groups might cause lag!"));
		mod.loadConfig(false);
		sender.addChatMessage(new ChatComponentText("Configuration reloaded!"));
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender.canCommandSenderUseCommand(1, getCommandName());
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_,
			String[] p_71516_2_) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}
	
	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

}
