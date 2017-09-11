package its_meow.quickhomes.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CommandTPD extends CommandBase {

	@Override
	public String getName() {
		return "tpd";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/tpd (dimension ID) or /tpd";
	}



	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			sender.sendMessage(new TextComponentString("You are in dimension ID: " + sender.getEntityWorld().provider.getDimension()));
			sender.sendMessage(new TextComponentString("To teleport to a dimension, use /tpd (dimension ID)"));
		} else if(args.length > 1) {
			throw new WrongUsageException("Too many arguments! Correct usage: /tpd (dimension ID)");
		} else {
			String arg = args[0];
			int id;
			if(arg.toLowerCase().equals("nether")) {
				id = -1;
			} else if(arg.toLowerCase().equals("end") | arg.toLowerCase().equals("the end") ) {
				id = 1;
			} else if(arg.toLowerCase().equals("overworld")) {
				id = 0;
			} else {
				id = Integer.parseInt(arg);
			}
			if(!(sender instanceof EntityPlayer | sender instanceof EntityPlayerMP)) { 
				throw new WrongUsageException("Must be player to execute!");
			}
			EntityPlayer senderP = (EntityPlayer) sender;
			EntityPlayerMP senderMP = (EntityPlayerMP) sender;
			World world = senderP.getEntityWorld();
			int curID = world.provider.getDimension();
			if(id == curID) {
				throw new WrongUsageException("You are already in that dimension!");
			}
			PlayerList list = server.getPlayerList();
			WorldServer destWorld = server.getWorld(id);
			list.transferPlayerToDimension(senderMP, id, new HomeTeleporter(destWorld, false));
		}
	}

}
