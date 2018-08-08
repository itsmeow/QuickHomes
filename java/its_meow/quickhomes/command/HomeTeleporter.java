package its_meow.quickhomes.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class HomeTeleporter extends Teleporter{
	private final WorldServer worldServerInstance;
	private final Random random;
	private boolean coordCalc;

	public HomeTeleporter(WorldServer worldIn, boolean doCoordCalc) {
		super(worldIn);
		this.worldServerInstance = worldIn;
		this.random = new Random(worldIn.getSeed());
		this.coordCalc = doCoordCalc;
	}

	@Override
	public void placeInPortal(Entity entityIn, float rotationYaw){
		if(coordCalc) {
			EntityPlayerMP senderMP = (EntityPlayerMP) entityIn;
			MinecraftServer server = entityIn.getServer();
			File dataDir = new File(server.getDataDirectory().getAbsolutePath());
			EntityPlayer senderP = (EntityPlayer) entityIn;

			QHWorldStorage sd = QHWorldStorage.get(senderP.world);

			int[] data = sd.data.getIntArray(senderP.getUniqueID().toString());

			int destWorldId = data[0];
			int posX = data[1];
			int posY = data[2];
			int posZ = data[3];
			
			entityIn.setLocationAndAngles(posX, posY, posZ, entityIn.rotationYaw, 0.0F);
			entityIn.motionX = 0.0D;
			entityIn.motionY = 0.0D;
			entityIn.motionZ = 0.0D;

		} else {
			//BlockPos pos = entityIn.getEntityWorld().getSpawnPoint(); //TO\DO: Move to TPD class, change to DESTINATION world not CURRENT.
			//entityIn.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), entityIn.rotationYaw, 0.0F);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entityIn, float p_180620_2_) {
		return false;
	}

}