package its_meow.quickhomes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod(QuickHomesMod.MOD_ID)
@Mod.EventBusSubscriber(modid = QuickHomesMod.MOD_ID)
public class QuickHomesMod {

    public static final String MOD_ID = "quickhomes";

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> d = event.getCommandDispatcher();
        // Home command
        d.register(Commands.literal("home").requires(source -> {
            try {
                return source.asPlayer() != null;
            } catch(CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> {
            EntityPlayerMP player = command.getSource().asPlayer();


            NBTTagCompound playerD = player.getEntityData();
            if(playerD.contains(MOD_ID, NBT.TAG_COMPOUND)) {
                NBTTagCompound data = playerD.getCompound(MOD_ID);
                double posX = data.getDouble("x");
                double posY = data.getDouble("y");
                double posZ = data.getDouble("z");
                int dim = data.getInt("dim");
                if(dim != player.getServerWorld().getDimension().getType().getId()){
                    player.server.getPlayerList().changePlayerDimension(player, DimensionType.getById(dim));
                }
                player.setPositionAndUpdate(posX, posY, posZ);
                return 1;
            } else {
                player.sendMessage(new TextComponentString("No home set."));
            }
            return 0;
        }));
        
        // Sethome command
        d.register(Commands.literal("sethome").requires(source -> {
            try {
                return source.asPlayer() != null;
            } catch(CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> {
            EntityPlayerMP player = command.getSource().asPlayer();
            NBTTagCompound playerD = player.getEntityData();
            NBTTagCompound data = new NBTTagCompound();
            data.putDouble("x", player.posX);
            data.putDouble("y", player.posY);
            data.putDouble("z", player.posZ);
            data.putInt("dim", player.getServerWorld().getDimension().getType().getId());
            playerD.put(MOD_ID, data);
            player.sendMessage(new TextComponentString("Home set."));
            return 1;
        }));

    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.getPlayer();
        if(!player.world.isRemote) {
            player.sendMessage(new TextComponentString("This server is running QuickHomes " + ModList.get().getModContainerById(MOD_ID).get().getModInfo().getVersion() + " by its_meow!"));
            player.sendMessage(new TextComponentString("You can use /sethome and /home with this mod installed."));
        }
    }
    
    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        NBTTagCompound oldData = event.getOriginal().getEntityData();
        if(oldData.contains(MOD_ID, NBT.TAG_COMPOUND)) {
            event.getEntityPlayer().getEntityData().put(MOD_ID, oldData.getCompound(MOD_ID));
        }
    }

}
