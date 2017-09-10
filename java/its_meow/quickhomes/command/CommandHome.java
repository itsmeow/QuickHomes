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
            File dataDir = new File(server.getDataDirectory().getAbsolutePath());
            File lDataDir = new File(dataDir.getAbsolutePath() + "\\quickhomes");
            File pDataFile = new File(lDataDir.getAbsolutePath() + "\\" + senderP.getUniqueID() + ".txt");
            if(!lDataDir.exists()) {
            	lDataDir.mkdir();
            }
            if(!pDataFile.exists()) {
				throw new CommandException("Please set a home first!");
            } else {
            	Scanner sc;
				try {
					sc = new Scanner(pDataFile);
	            	int destWorldId = Integer.parseInt(sc.nextLine());
	            	int posX = Integer.parseInt(sc.nextLine());
	            	int posY = Integer.parseInt(sc.nextLine());
	            	int posZ = Integer.parseInt(sc.nextLine());
	            	BlockPos newPos = new BlockPos(posX, posY, posZ);
	            	sc.close();
	    			WorldServer destWorld = server.getWorld(destWorldId);
	    			if(!(destWorld == server.getWorld(dimId))){
	    				list.transferPlayerToDimension(senderMP, destWorldId, new HomeTeleporter(destWorld));
	    			}
	    			senderP.setLocationAndAngles(posX, posY, posZ, senderP.rotationYaw, 0.0F);
	    			senderP.setLocationAndAngles(posX, posY, posZ, senderP.rotationYaw, 0.0F);
	    			senderP.setLocationAndAngles(posX, posY, posZ, senderP.rotationYaw, 0.0F); //TODO: Instead of these stupid repeats to ensure it happens, make it happen!
				} catch (FileNotFoundException e) {}	
            }
		} else {
			throw new WrongUsageException("No arguments required, use /home");
		}
	}

}
