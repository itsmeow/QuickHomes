package dev.itsmeow.quickhomes;

import net.minecraftforge.event.RegisterCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(QuickHomesMod.MOD_ID)
@Mod.EventBusSubscriber(modid = QuickHomesMod.MOD_ID)
public class QuickHomesMod {

    public static final String MOD_ID = "quickhomes";
    private static ServerConfig SERVER_CONFIG = null;
    private static ForgeConfigSpec SERVER_CONFIG_SPEC = null;

    public QuickHomesMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG_SPEC = specPair.getRight();
        SERVER_CONFIG = specPair.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG_SPEC);
    }

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        registerCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        registerCommands(event.getServer().getCommandManager().getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if(!player.world.isRemote && SERVER_CONFIG.joinMessageEnabled.get()) {
            player.sendMessage(new StringTextComponent("This server is running QuickHomes " + ModList.get().getModContainerById(MOD_ID).get().getModInfo().getVersion() + " by its_meow!"), Util.DUMMY_UUID);
            player.sendMessage(new StringTextComponent("You can use /sethome and /home with this mod installed."), Util.DUMMY_UUID);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        CompoundNBT oldData = event.getOriginal().getPersistentData();
        if(oldData.contains(MOD_ID, NBT.TAG_COMPOUND)) {
            event.getPlayer().getPersistentData().put(MOD_ID, oldData.getCompound(MOD_ID));
        }
    }

    public static void registerCommands(CommandDispatcher<CommandSource> commandDispatcher) {
        // Home command
        commandDispatcher.register(Commands.literal("home").requires(source -> {
            try {
                return source.asPlayer() != null;
            } catch(CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> {
            ServerPlayerEntity player = command.getSource().asPlayer();

            CompoundNBT playerD = player.getPersistentData();
            if(playerD.contains(MOD_ID, NBT.TAG_COMPOUND)) {
                CompoundNBT data = playerD.getCompound(MOD_ID);
                double posX = data.getDouble("x");
                double posY = data.getDouble("y");
                double posZ = data.getDouble("z");
                String dim = data.getString("dim");
                player.teleport(player.getServer().getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(dim))), posX, posY, posZ, player.rotationYaw, player.rotationPitch);
                return 1;
            } else {
                player.sendMessage(new StringTextComponent("No home set."), Util.DUMMY_UUID);
            }
            return 0;
        }));

        // Sethome command
        commandDispatcher.register(Commands.literal("sethome").requires(source -> {
            try {
                return source.asPlayer() != null;
            } catch(CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> {
            ServerPlayerEntity player = command.getSource().asPlayer();
            CompoundNBT playerD = player.getPersistentData();
            CompoundNBT data = new CompoundNBT();
            data.putDouble("x", player.getPosX());
            data.putDouble("y", player.getPosY());
            data.putDouble("z", player.getPosZ());
            data.putString("dim", player.getEntityWorld().getDimensionKey().getLocation().toString());
            playerD.put(MOD_ID, data);
            player.sendMessage(new StringTextComponent("Home set."), Util.DUMMY_UUID);
            return 1;
        }));
    }

    public static class ServerConfig {
        public ForgeConfigSpec.Builder builder;
        public final ForgeConfigSpec.BooleanValue joinMessageEnabled;

        ServerConfig(ForgeConfigSpec.Builder builder) {
            this.builder = builder;
            this.joinMessageEnabled = builder.comment("Set to false to disable join message. Place a copy of this config in the defaultconfigs/ folder in the main server/.minecraft directory (or make the folder if it's not there) to copy this to new worlds.").worldRestart().define("enable_join_message", true);
            builder.build();
        }
    }

}
