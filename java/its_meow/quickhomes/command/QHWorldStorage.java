package its_meow.quickhomes.command;

import java.util.HashMap;

import its_meow.quickhomes.Ref;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class QHWorldStorage extends WorldSavedData {
	private static final String DATA_NAME = Ref.MOD_ID + "_HomesData";
	public NBTTagCompound data = new NBTTagCompound();
	
	public QHWorldStorage() {
		super(DATA_NAME);
	}
	
	public QHWorldStorage(String s) {
		super(s);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		data = nbt;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = data;
		return compound;
	}
	
	
	public static QHWorldStorage get(World world) {
		QHWorldStorage save = (QHWorldStorage) world.getMapStorage().getOrLoadData(QHWorldStorage.class, DATA_NAME);
		if(save == null) {
			save = new QHWorldStorage();
			world.getMapStorage().setData(DATA_NAME, save);
		}
		return save;
	}
}

