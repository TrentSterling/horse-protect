package net.chiisana.horseprotect;

import net.chiisana.horseprotect.listener.EntityEventListener;
import org.bukkit.plugin.java.JavaPlugin;

public class HorseProtect extends JavaPlugin {
	private static HorseProtect instance;

	public static HorseProtect getInstance() {
		if (instance == null) { instance = new HorseProtect(); }
		return instance;
	}

	public void onEnable() {
		instance = this;

		this.getServer().getPluginManager().registerEvents(new EntityEventListener(), this);
	}
}
