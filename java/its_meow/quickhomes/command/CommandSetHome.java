package its_meow.quickhomes.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class CommandSetHome extends CommandBase {

	@Override
	public String getName() {
		return "sethome";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "Use /sethome to set home location.";
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		if(sender instanceof EntityPlayer) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer | sender instanceof EntityPlayerMP) {
			if (args.length == 0) {
				BlockPos pos = sender.getPosition();
				EntityPlayer senderP = (EntityPlayer) sender;
				File dataDir = new File(server.getDataDirectory().getAbsolutePath());
				File lDataDir = new File(dataDir.getAbsolutePath() + "/quickhomes");
				File pDataFile = new File(lDataDir.getAbsolutePath() + "/" + senderP.getUniqueID() + ".txt");
				if(!lDataDir.exists()) {
					lDataDir.mkdir();
				}
				if(!pDataFile.exists()) {
					try {
						pDataFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						throw new CommandException("Failed to create user data file!");
					}
				} else {
					pDataFile.delete();
					try {
						pDataFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						throw new CommandException("Failed to recreate user data file!");
					}
				}
				try {
					PrintWriter pw = new PrintWriter(pDataFile);
					pw.println(senderP.getEntityWorld().provider.getDimension());
					pw.println(pos.getX());
					pw.println(pos.getY());
					pw.println(pos.getZ());
					pw.close();
					senderP.sendMessage(new TextComponentString("Home set."));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new CommandException("Failed to find user data file!");
				}
			}
			else
			{
				throw new WrongUsageException("No arguments required, use /sethome.");
			}
		}
	}

}
