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

	public HomeTeleporter(WorldServer worldIn) {
		super(worldIn);
		this.worldServerInstance = worldIn;
		this.random = new Random(worldIn.getSeed());
	}

	@Override
	public void placeInPortal(Entity entityIn, float rotationYaw){
		EntityPlayerMP senderMP = (EntityPlayerMP) entityIn;
		MinecraftServer server = entityIn.getServer();
		File dataDir = new File(server.getDataDirectory().getAbsolutePath());
		EntityPlayer senderP = (EntityPlayer) entityIn;
		File lDataDir = new File(dataDir.getAbsolutePath() + "\\quickhomes");
		File pDataFile = new File(lDataDir.getAbsolutePath() + "\\" + senderP.getUniqueID() + ".txt");
		Scanner sc;
		try {
			sc = new Scanner(pDataFile);
			sc.nextLine();
			int posX = Integer.parseInt(sc.nextLine());
			int posY = Integer.parseInt(sc.nextLine());
			int posZ = Integer.parseInt(sc.nextLine());
			sc.close();
			entityIn.setLocationAndAngles(posX, posY, posZ, entityIn.rotationYaw, 0.0F);
			entityIn.motionX = 0.0D;
			entityIn.motionY = 0.0D;
			entityIn.motionZ = 0.0D;
		} catch(Exception e) {}
	}

	@Override
	public boolean placeInExistingPortal(Entity entityIn, float p_180620_2_) {
		return false;
	}

	private boolean isPositionClear(BlockPos pos) {
		return true;
	}


	public class PortalPosition extends BlockPos{
		public long lastUpdateTime;

		public PortalPosition(BlockPos pos, long lastUpdate){
			super(pos.getX(), pos.getY(), pos.getZ());
			this.lastUpdateTime = lastUpdate;
		}
	}

}