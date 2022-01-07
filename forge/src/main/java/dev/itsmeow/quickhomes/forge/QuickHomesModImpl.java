package dev.itsmeow.quickhomes.forge;

import dev.itsmeow.quickhomes.QuickHomesMod;
import net.minecraftforge.fml.ModList;

public class QuickHomesModImpl {
    public static boolean isJoinMessageEnabled() {
        return QuickHomesModForge.SERVER_CONFIG.joinMessageEnabled.get();
    }

    public static String getModVersion() {
        return ModList.get().getModContainerById(QuickHomesMod.MOD_ID).get().getModInfo().getVersion().toString();
    }
}
