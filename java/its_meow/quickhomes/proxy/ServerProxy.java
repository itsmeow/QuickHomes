package its_meow.quickhomes.proxy;

import java.io.File;

import its_meow.quickhomes.Ref;
import its_meow.quickhomes.command.CommandHome;
import its_meow.quickhomes.command.CommandSetHome;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;


public class ServerProxy extends CommonProxy {
	
	public void preInit(FMLPreInitializationEvent e) {
	}
	
	public void init(FMLInitializationEvent e) {
		System.out.println("Starting QuickHomes mod version " + Ref.VERSION + "...");
	}
	
	public void postInit(FMLPostInitializationEvent e) {
		System.out.println("QuickHomes mod version " + Ref.VERSION + " loaded!");
	}
	
}
