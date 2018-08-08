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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CommandHome extends CommandBase {

	@Override
	public String getName() {
		return "home";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/home";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	public static void print(String in) {
		System.out.println(in);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0)
		{
			int dimId = sender.getEntityWorld().provider.getDimension();
			EntityPlayerMP senderMP = (EntityPlayerMP) sender;
			PlayerList list = server.getPlayerList();

			BlockPos pos = sender.getPosition();
			EntityPlayer senderP = (EntityPlayer) sender;
			
			QHWorldStorage sd = QHWorldStorage.get(senderP.world);
			
			int[] data = sd.data.getIntArray(senderP.getUniqueID().toString());
			if(data.length <= 0) {
				throw new CommandException("Please set a home first!");
			} else {
				int destWorldId = data[0];
				int posX = data[1];
				int posY = data[2];
				int posZ = data[3];
				BlockPos newPos = new BlockPos(posX, posY, posZ);
				WorldServer destWorld = server.getWorld(destWorldId);
				if(!(destWorld == server.getWorld(dimId))){
					list.transferPlayerToDimension(senderMP, destWorldId, new HomeTeleporter(destWorld, true));
				}
				senderMP.setPositionAndUpdate(posX, posY, posZ);
				senderP.setPositionAndUpdate(posX, posY, posZ);
			}
		} else {
			throw new WrongUsageException("Command /home does not take any arguments.");
		}
	}

}
