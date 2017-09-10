package its_meow.quickhomes;

import its_meow.quickhomes.command.CommandHome;
import its_meow.quickhomes.command.CommandSetHome;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class EventHandler {
	@SubscribeEvent
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		World world = player.getEntityWorld();
		if(!world.isRemote) {
			player.sendMessage(new TextComponentString("This server is running QuickHomes " + Ref.VERSION + " by its_meow!"));
			player.sendMessage(new TextComponentString("You can use /sethome and /home with this mod installed."));
		}
	}
	
}
