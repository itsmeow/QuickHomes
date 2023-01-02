package dev.itsmeow.quickhomes.mixin;

import dev.itsmeow.quickhomes.IStoreHome;
import dev.itsmeow.quickhomes.QuickHomesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IStoreHome {

    @Unique
    public Vec3 quickhomes_homePos = null;
    @Unique
    public ResourceKey<Level> quickhomes_dimension = null;

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag tag, CallbackInfo c) {
        CompoundTag data = null;
        if (tag.contains(QuickHomesMod.MOD_ID)) {
            data = tag.getCompound(QuickHomesMod.MOD_ID);
        } else if (tag.contains("PlayerPersisted") && tag.getCompound("PlayerPersisted").contains(QuickHomesMod.MOD_ID)) {
            data = tag.getCompound("PlayerPersisted").getCompound(QuickHomesMod.MOD_ID);
        }
        if (data != null && data.contains("x") && data.contains("y") && data.contains("z") && data.contains("dim")) {
            quickhomes_homePos = new Vec3(data.getDouble("x"), data.getDouble("y"), data.getDouble("z"));
            quickhomes_dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(data.getString("dim")));
        }
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag tag, CallbackInfo c) {
        if (quickhomes_homePos != null && quickhomes_dimension != null) {
            CompoundTag data = new CompoundTag();
            data.putDouble("x", quickhomes_homePos.x());
            data.putDouble("y", quickhomes_homePos.y());
            data.putDouble("z", quickhomes_homePos.z());
            data.putString("dim", quickhomes_dimension.location().toString());
            tag.put(QuickHomesMod.MOD_ID, data);
        }
    }

    @Inject(at = @At("RETURN"), method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V")
    public void restoreFrom(ServerPlayer serverPlayer, boolean bl, CallbackInfo c) {
        ServerPlayerMixin serverPlayerMixin = (ServerPlayerMixin) (Object) serverPlayer;
        quickhomes_homePos = serverPlayerMixin.quickhomes_homePos;
        quickhomes_dimension = serverPlayerMixin.quickhomes_dimension;
    }

    @Override
    public void setHome(Vec3 pos, ResourceKey<Level> dimension) {
        quickhomes_homePos = pos;
        quickhomes_dimension = dimension;
    }

    @Override
    public Pair<Vec3, ResourceKey<Level>> getHome() {
        return Pair.of(quickhomes_homePos, quickhomes_dimension);
    }
}
