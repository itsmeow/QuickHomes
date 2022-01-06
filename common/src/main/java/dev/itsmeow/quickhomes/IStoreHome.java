package dev.itsmeow.quickhomes;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public interface IStoreHome {

    void setHome(Vec3 pos, ResourceKey<Level> dimension);

    Pair<Vec3, ResourceKey<Level>> getHome();

}
