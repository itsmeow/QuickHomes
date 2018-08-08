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
		return sender instanceof EntityPlayer;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer | sender instanceof EntityPlayerMP) {
			if (args.length == 0) {
				BlockPos pos = sender.getPosition();
				EntityPlayer senderP = (EntityPlayer) sender;

				QHWorldStorage sd = QHWorldStorage.get(senderP.world);

				int[] arrayToStore = {0,0,0,0}; 
				arrayToStore[0] = senderP.getEntityWorld().provider.getDimension();
				arrayToStore[1] = pos.getX();
				arrayToStore[2] = pos.getY();
				arrayToStore[3] = pos.getZ();

				sd.data.setIntArray(senderP.getUniqueID().toString(), arrayToStore);
				sd.markDirty();
				sender.sendMessage(new TextComponentString("Home set."));
			} else {
				throw new WrongUsageException("Command /sethome does not take any arguments.");
			}
		}
	}

}
