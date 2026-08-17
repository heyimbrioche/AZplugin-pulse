package fr.mathip.azplugin.bukkit.module;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.module.playertag.PlayerTagModule;
import lombok.Getter;

@Getter
public class ModuleManager {
    private Main plugin;

    private PlayerTagModule playerTagModule;

    public ModuleManager(Main plugin) {
        this.plugin = plugin;
        register();
    }

    // Active/désactive les modules selon la section de la config
    public void loadConfig(ConfigurationSection config) {
        playerTagModule.setEnable(config.getBoolean(playerTagModule.getConfigSection()));
    }

    // Nouveaux modules = une ligne ici + une ligne dans loadConfig
    private void register() {
        playerTagModule = new PlayerTagModule();
    }

}
