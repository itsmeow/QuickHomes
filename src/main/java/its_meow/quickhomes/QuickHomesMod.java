package its_meow.quickhomes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
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
            ServerPlayerEntity player = command.getSource().asPlayer();


            CompoundNBT playerD = player.getEntityData();
            if(playerD.contains(MOD_ID, NBT.TAG_COMPOUND)) {
                CompoundNBT data = playerD.getCompound(MOD_ID);
                double posX = data.getDouble("x");
                double posY = data.getDouble("y");
                double posZ = data.getDouble("z");
                int dim = data.getInt("dim");
                if(dim != player.getServerWorld().getDimension().getType().getId()){
                    player.changeDimension(DimensionType.getById(dim));
                }
                player.setPositionAndUpdate(posX, posY, posZ);
                return 1;
            } else {
                player.sendMessage(new StringTextComponent("No home set."));
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
            ServerPlayerEntity player = command.getSource().asPlayer();
            CompoundNBT playerD = player.getEntityData();
            CompoundNBT data = new CompoundNBT();
            data.putDouble("x", player.posX);
            data.putDouble("y", player.posY);
            data.putDouble("z", player.posZ);
            data.putInt("dim", player.getServerWorld().getDimension().getType().getId());
            playerD.put(MOD_ID, data);
            player.sendMessage(new StringTextComponent("Home set."));
            return 1;
        }));

    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if(!player.world.isRemote) {
            player.sendMessage(new StringTextComponent("This server is running QuickHomes " + ModList.get().getModContainerById(MOD_ID).get().getModInfo().getVersion() + " by its_meow!"));
            player.sendMessage(new StringTextComponent("You can use /sethome and /home with this mod installed."));
        }
    }

}
