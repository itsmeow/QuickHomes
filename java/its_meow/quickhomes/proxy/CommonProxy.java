package its_meow.quickhomes.proxy;


import java.io.File;
import its_meow.quickhomes.EventHandler;
import its_meow.quickhomes.config.QuickHomesConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

	public static Configuration config;
	public void preInit(FMLPreInitializationEvent e) {
		/* Config Creation and initation */
		File directory = e.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "quickhomes.cfg")); 
        QuickHomesConfig.readConfig();
        QuickHomesConfig.initConfig(config); //TODO: Remove if becomes obsolete.
        /* Register Event Handler */
        MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
	
	public void init(FMLInitializationEvent e) {
	}
	
	public void postInit(FMLPostInitializationEvent e){
		if(config.hasChanged()){
			config.save();
		}
	}
	

	
	
	
}
