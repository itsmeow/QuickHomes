package its_meow.quickhomes;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
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

            CompoundNBT playerD = player.getPersistentData();
            if(playerD.contains(MOD_ID, NBT.TAG_COMPOUND)) {
                CompoundNBT data = playerD.getCompound(MOD_ID);
                double posX = data.getDouble("x");
                double posY = data.getDouble("y");
                double posZ = data.getDouble("z");
                int dim = data.getInt("dim");
                if(dim != player.getEntityWorld().getDimension().getType().getId()) {
                    teleport(player, DimensionType.getById(dim));
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
            CompoundNBT playerD = player.getPersistentData();
            CompoundNBT data = new CompoundNBT();
            data.putDouble("x", player.posX);
            data.putDouble("y", player.posY);
            data.putDouble("z", player.posZ);
            data.putInt("dim", player.getEntityWorld().getDimension().getType().getId());
            playerD.put(MOD_ID, data);
            player.sendMessage(new StringTextComponent("Home set."));
            return 1;
        }));

    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if(!player.world.isRemote && SERVER_CONFIG.joinMessageEnabled.get()) {
            player.sendMessage(new StringTextComponent("This server is running QuickHomes " + ModList.get().getModContainerById(MOD_ID).get().getModInfo().getVersion() + " by its_meow!"));
            player.sendMessage(new StringTextComponent("You can use /sethome and /home with this mod installed."));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        CompoundNBT oldData = event.getOriginal().getPersistentData();
        if(oldData.contains(MOD_ID, NBT.TAG_COMPOUND)) {
            event.getPlayer().getPersistentData().put(MOD_ID, oldData.getCompound(MOD_ID));
        }
    }

    @SuppressWarnings("resource")
    public static Entity teleport(Entity entityIn, DimensionType dimensionTo) {
        if(!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entityIn, dimensionTo)) {
            return null;
        }
        if(!entityIn.getEntityWorld().isRemote && entityIn.isAlive()) {
            final ServerWorld worldFrom = entityIn.getServer().func_71218_a(entityIn.dimension);
            final ServerWorld worldTo = entityIn.getServer().func_71218_a(dimensionTo);
            entityIn.dimension = dimensionTo;

            if(entityIn instanceof ServerPlayerEntity) {
                final ServerPlayerEntity entityPlayer = (ServerPlayerEntity) entityIn;
                // Access Transformer exposes this field
                entityPlayer.invulnerableDimensionChange = true;
                // End Access Transformer
                WorldInfo worldinfo = entityPlayer.world.getWorldInfo();
                entityPlayer.connection.sendPacket(new SRespawnPacket(dimensionTo, worldinfo.getGenerator(),
                entityPlayer.interactionManager.getGameType()));
                entityPlayer.connection.sendPacket(
                new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
                PlayerList playerlist = entityPlayer.world.getServer().getPlayerList();
                playerlist.updatePermissionLevel(entityPlayer);
                worldFrom.removeEntity(entityPlayer, true); // Forge: the player entity is moved to the new world, NOT cloned. So keep the
                                                            // data alive with no matching invalidate call.
                entityPlayer.revive();
                entityPlayer.setWorld(worldTo);
                worldTo.func_217447_b(entityPlayer);
                // entityPlayer.func_213846_b(worldFrom);
                entityPlayer.interactionManager.func_73080_a(worldTo);
                entityPlayer.connection.sendPacket(new SPlayerAbilitiesPacket(entityPlayer.abilities));
                playerlist.func_72354_b(entityPlayer, worldTo);
                playerlist.sendInventory(entityPlayer);

                net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerChangedDimensionEvent(entityPlayer, entityPlayer.dimension, dimensionTo);
                // entityPlayer.clearInvulnerableDimensionChange();
                return entityPlayer;
            }

            entityIn.detach();
            Entity copy = entityIn.getType().create(worldTo);
            if(copy != null) {
                copy.copyDataFromOld(entityIn);
                copy.setMotion(entityIn.getMotion().mul(Vec3d.fromPitchYaw(entityIn.rotationPitch, entityIn.rotationYaw).normalize()));
                // used to unnaturally add entities to world
                worldTo.func_217460_e(copy);
            }
            // update world
            worldFrom.resetUpdateEntityTick();
            worldTo.resetUpdateEntityTick();
            // remove old entity
            entityIn.remove(false);
            return copy;
        }
        return null;
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
