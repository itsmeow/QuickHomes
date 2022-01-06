package dev.itsmeow.quickhomes.fabric;

import dev.itsmeow.quickhomes.QuickHomesMod;
import dev.itsmeow.quickhomes.QuickHomesModFabric;
import net.fabricmc.loader.api.FabricLoader;

public class QuickHomesModImpl {
    public static boolean isJoinMessageEnabled() {
        return QuickHomesModFabric.enableJoinMessage.getValue();
    }

    public static String getModVersion() {
        return FabricLoader.getInstance().getModContainer(QuickHomesMod.MOD_ID).get().getMetadata().getVersion().getFriendlyString();
    }
}
