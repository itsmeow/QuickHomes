package dev.itsmeow.quickhomes;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuickHomesModFabric implements ModInitializer {

    public static final PropertyMirror<Boolean> enableJoinMessage = PropertyMirror.create(ConfigTypes.BOOLEAN);
    protected static final JanksonValueSerializer JANKSON_VALUE_SERIALIZER = new JanksonValueSerializer(false);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, dedicated) -> QuickHomesMod.registerCommands(commandDispatcher));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            QuickHomesMod.onPlayerJoin(handler.player);
        });
        ServerLifecycleEvents.SERVER_STARTING.register(state -> {
            ConfigTreeBuilder builder = ConfigTree.builder().withName(QuickHomesMod.MOD_ID).beginValue(QuickHomesMod.CONFIG_FIELD_NAME, ConfigTypes.BOOLEAN, QuickHomesMod.CONFIG_FIELD_VALUE).withComment(QuickHomesMod.CONFIG_FIELD_COMMENT).finishValue(enableJoinMessage::mirror);
            ConfigBranch branch = builder.build();
            File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), QuickHomesMod.MOD_ID + ".json5");
            boolean recreate = false;
            while (true) {
                try {
                    if (!configFile.exists() || recreate) {
                        FiberSerialization.serialize(branch, Files.newOutputStream(configFile.toPath()), JANKSON_VALUE_SERIALIZER);
                        break;
                    } else {
                        try {
                            FiberSerialization.deserialize(branch, Files.newInputStream(configFile.toPath()), JANKSON_VALUE_SERIALIZER);
                            FiberSerialization.serialize(branch, Files.newOutputStream(configFile.toPath()), JANKSON_VALUE_SERIALIZER);
                            break;
                        } catch (ValueDeserializationException e) {
                            String fileName = (QuickHomesMod.MOD_ID + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".json5");
                            configFile.renameTo(new File(configFile.getParent(), fileName));
                            recreate = true;
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
    }
}
