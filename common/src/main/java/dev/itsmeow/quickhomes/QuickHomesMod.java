package dev.itsmeow.quickhomes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

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
            if(home.getLeft() != null && home.getRight() != null) {
                if (player.isPassenger()) {
                    Entity vehicle = player.getVehicle();
                    vehicle.teleportTo(home.getLeft().x, home.getLeft().y, home.getLeft().z);
                } else {
                    player.teleportTo(player.getServer().getLevel(home.getRight()), home.getLeft().x, home.getLeft().y, home.getLeft().z, player.getYRot(), player.getXRot());
                }
                return 1;
            } else {
                player.sendMessage(new TextComponent("No home set."), Util.NIL_UUID);
            }
            return 0;
        }));
        dispatcher.register(Commands.literal("sethome").requires(isPlayer).executes(command -> {
            ServerPlayer player = command.getSource().getPlayerOrException();
            ((IStoreHome) player).setHome(new Vec3(player.getX(), player.getY(), player.getZ()), player.getLevel().dimension());
            player.sendMessage(new TextComponent("Home set."), Util.NIL_UUID);
            return 1;
        }));
    }

    public static void onPlayerJoin(Player player) {
        if(!player.level.isClientSide() && isJoinMessageEnabled()) {
            player.sendMessage(new TextComponent("This server is running QuickHomes " + getModVersion() + " by itsmeowdev!"), Util.NIL_UUID);
            player.sendMessage(new TextComponent("You can use /sethome and /home with this mod installed."), Util.NIL_UUID);
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
