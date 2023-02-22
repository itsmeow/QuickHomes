package dev.itsmeow.quickhomes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Predicate;

public class QuickHomesMod {

    public static final String MOD_ID = "quickhomes";
    public static final String CONFIG_FIELD_NAME = "enable_join_message";
    public static final String CONFIG_FIELD_COMMENT = "Set to false to disable join message.";
    public static final boolean CONFIG_FIELD_VALUE = true;

    public static void registerCommands(CommandDispatcher dispatcher) {
        Predicate<CommandSourceStack> isPlayer = source -> {
            try {
                return source.getPlayerOrException() != null;
            } catch(CommandSyntaxException e) {
                return false;
            }
        };
        dispatcher.register(Commands.literal("home").requires(isPlayer).executes(command -> {
            ServerPlayer player = command.getSource().getPlayerOrException();
            Pair<Vec3, ResourceKey<Level>> home = ((IStoreHome) player).getHome();
            Vec3 homePos = home.getLeft();
            ResourceKey<Level> homeLevel = home.getRight();
            if(homePos != null && homeLevel != null) {
                ServerLevel serverLevel = player.getServer().getLevel(homeLevel);
                Entity vehicle = player.getVehicle();
                if (player.isPassenger()) {
                    if (!serverLevel.equals(player.getLevel())) {
                        if (hasPlayer(vehicle, player)) {
                            player.teleportTo(serverLevel, homePos.x, homePos.y, homePos.z, player.getYRot(), player.getXRot());
                            return 1;
                        }
                        List<Entity> passengers = vehicle.getPassengers();
                        Entity tpVehicle = teleportAcrossDimensions(vehicle, homePos, serverLevel);
                        for (Entity passenger : passengers) {
                            Entity tpPassenger = teleportAcrossDimensions(passenger, homePos, serverLevel);
                            tpVehicle.positionRider(tpPassenger);
                            tpPassenger.startRiding(tpVehicle);
                        }
                        return 1;
                    } else {
                        if (!hasPlayer(vehicle, player)) {
                            vehicle.teleportTo(homePos.x, homePos.y, homePos.z);
                            return 1;
                        }
                    }
                }
                player.teleportTo(serverLevel, homePos.x, homePos.y, homePos.z, player.getYRot(), player.getXRot());
                return 1;
            } else {
                player.sendSystemMessage(Component.literal("No home set."));
            }
            return 0;
        }));
        dispatcher.register(Commands.literal("sethome").requires(isPlayer).executes(command -> {
            ServerPlayer player = command.getSource().getPlayerOrException();
            ((IStoreHome) player).setHome(new Vec3(player.getX(), player.getY(), player.getZ()), player.getLevel().dimension());
            player.sendSystemMessage(Component.literal("Home set."));
            return 1;
        }));
    }

    /**
     * Looks for a player in the passengers and vehicle itself, other than the passed in player.
     * @param vehicle The vehicle to search through.
     * @param player The player to not count as a player when one if found.
     * @return true if another player was found, false otherwise.
     */
    private static boolean hasPlayer(Entity vehicle, ServerPlayer player) {
        return vehicle.getPassengersAndSelf().anyMatch(entity -> (!entity.equals(player)) && entity instanceof Player);
    }

    /**
     * Teleport an entity to another dimension.
     * @param entity The entity to teleport.
     * @param homePos The position to teleport the entity to.
     * @param serverLevel The dimension to teleport the entity to.
     * @return
     */
    private static Entity teleportAcrossDimensions(Entity entity, Vec3 homePos, ServerLevel serverLevel) {
        entity.unRide();
        // If entity is a player, just call the ServerPlayer tp function that handles cross-dimension already.
        if (entity instanceof ServerPlayer) {
            ((ServerPlayer) entity).teleportTo(serverLevel, homePos.x, homePos.y, homePos.z, entity.getYRot(), entity.getXRot());
            return entity;
        }
        // Handle recreating new entity in other dimension.
        entity.level.getProfiler().popPush("reloading");
        Entity newEntity = entity.getType().create(serverLevel);
        if (newEntity != null) {
            newEntity.restoreFrom(entity);
            newEntity.moveTo(homePos.x, homePos.y, homePos.z, entity.getYRot(), entity.getXRot());
            newEntity.setDeltaMovement(entity.getDeltaMovement());
            serverLevel.addDuringTeleport(newEntity);
        }
        // Removing old entity.
        entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
        ((ServerLevel) entity.level).resetEmptyTime();
        serverLevel.resetEmptyTime();

        return newEntity;
    }

    public static void onPlayerJoin(Player player) {
        if(!player.level.isClientSide() && isJoinMessageEnabled()) {
            player.sendSystemMessage(Component.literal("This server is running QuickHomes " + getModVersion() + " by itsmeowdev!"));
            player.sendSystemMessage(Component.literal("You can use /sethome and /home with this mod installed."));
        }
    }

    @ExpectPlatform
    public static boolean isJoinMessageEnabled() {
        throw new RuntimeException();
    }

    @ExpectPlatform
    public static String getModVersion() {
        throw new RuntimeException();
    }

}
