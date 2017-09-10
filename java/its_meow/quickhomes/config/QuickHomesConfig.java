package its_meow.quickhomes.config;

import org.apache.logging.log4j.Level;

import its_meow.quickhomes.QuickHomesMod;
import its_meow.quickhomes.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

public class QuickHomesConfig {
	
	private static final String cM = "misc";
	
	public static void readConfig(){
		Configuration cfg = CommonProxy.config;
		try {
			cfg.load();
			initConfig(cfg);
		} catch (Exception e1) {
			QuickHomesMod.logger.log(Level.ERROR, "Problem Loading Config!!", e1);
		} finally {
		if(cfg.hasChanged()){
			cfg.save();
		}
	}
	}
	
	public static void initConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(cM, "Misc. Configuration");
	}
	
}
