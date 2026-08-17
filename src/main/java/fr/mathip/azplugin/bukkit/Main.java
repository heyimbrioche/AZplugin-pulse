package fr.mathip.azplugin.bukkit;

import fr.mathip.azplugin.bukkit.config.ConfigManager;
import fr.mathip.azplugin.bukkit.module.ModuleManager;
import fr.mathip.azplugin.bukkit.packets.PacketWindow;
import lombok.Getter;
import fr.mathip.azplugin.bukkit.commands.*;
import fr.mathip.azplugin.bukkit.commands.items.*;

import org.bukkit.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe principale du plugin.
 * On passe beaucoup par le singleton getInstance() pour accéder à AZManager
 * et aux différents managers, c'est plus simple que de tout se balader en paramètres.
 */
public final class Main extends JavaPlugin {

    static public Main instance;

    // Gère les joueurs/entités liés au launcher (packets PLSP)
    private static AZManager AZManager;

    // Liste des joueurs ayant activé /az seechunks (les chunks du pack)
    public List<Player> playersSeeChunks;
    private BukkitTask bukkitTask;

    // Vrai si une version plus récente existe sur SpigotMC
    public boolean isUpdate;

    private CommandManager commandManager;

    private ConfigManager configManager;

    @Getter
    private ModuleManager moduleManager;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Metrics sert juste aux stats bStats, ID 21554 = notre ressource
        Metrics metrics = new Metrics(this, 21554);
        instance = this;
        saveDefaultConfig();
        // Les modules (ex: playertag) sont chargés avant la config pour pas avoir de NPE
        moduleManager = new ModuleManager(this);
        new ConfigManager(this);
        getServer().getPluginManager().registerEvents(new PacketWindow(this), this);
        AZManager = new AZManager(this);
        commandManager = new CommandManager();
        getCommand("az").setExecutor(commandManager);
        getCommand("az").setTabCompleter(new AZTabComplete());
        Bukkit.getPluginManager().registerEvents(new AZListener(this), this);
        playersSeeChunks = new ArrayList<>();
        setCommands();
        // Vérif de mise à jour sur SpigotMC, en bloquant pour que isUpdate soit dispo tout de suite
        isUpdate = new AZUpdate(this, 115548).checkForUpdate();
        if (isUpdate) {
            getLogger().info("Une nouvelle version du plugin a été détecté !");
            getLogger().info(
                    "Il est recommendé de le mettre à jour ici: https://www.spigotmc.org/resources/azplugin.115548/");
        }
    }

    private void setCommands() {
        // Commandes "racines" du plugin, accessible via /az <commande>
        commandManager.addCommand(new AZList());
        commandManager.addCommand(new AZSize());
        commandManager.addCommand(new AZStretch());
        commandManager.addCommand(new AZModel());
        commandManager.addCommand(new AZOpacity());
        commandManager.addCommand(new AZWorldEnv());
        commandManager.addCommand(new AZVignette());
        commandManager.addCommand(new AZSeechunks());
        commandManager.addCommand(new AZTag());
        commandManager.addCommand(new AZSubTag());
        commandManager.addCommand(new AZSupTag());
        commandManager.addCommand(new AZSummon());
        commandManager.addCommand(new AZPopup());
        commandManager.addCommand(new AZReload());
        commandManager.addCommand(new AZModelOffset());
        commandManager.addCommand(new AZEyeHeight());
        commandManager.addCommand(new AZWindowTitle());
        commandManager.addCommand(new AZSidebar());
        commandManager.addCommand(new AZLoadScreen());
        commandManager.addCommand(new AZChatDynamic());
        commandManager.addCommand(new AZChallenge());
        commandManager.addCommand(new AZItemCommand());
        // Sous-commandes de /az item, enregistrées séparément dans un autre manager
        commandManager.addItemCommand(new ItemRenderCommand());
        commandManager.addItemCommand(new ItemSpriteCommand());
        commandManager.addItemCommand(new ItemArmorCommand());
        commandManager.addItemCommand(new ItemTextCommand());
        commandManager.addItemCommand(new ItemRarityCommand());
        commandManager.addItemCommand(new ItemRenderColorCommand());
        commandManager.addItemCommand(new ItemFillSkullCommand());
        commandManager.addItemCommand(new ItemGlintCommand());
        commandManager.addItemCommand(new ItemGlintColorCommand());
        commandManager.addItemCommand(new ItemDisplayScaleCommand());
        commandManager.addItemCommand(new ItemDisplayTranslateYCommand());
        commandManager.addItemCommand(new ItemDisplayTranslateXCommand());
        commandManager.addItemCommand(new ItemDisplayColorCommand());
        commandManager.addItemCommand(new ItemDisplayRotationCommand());
        commandManager.addItemCommand(new ItemDisplayZIndexCommand());
        commandManager.addItemCommand(new ItemDisplayScaleXCommand());
        commandManager.addItemCommand(new ItemDisplayScaleYCommand());
        commandManager.addItemCommand(new ItemPacMenuCommand());
    }

    public static AZManager getAZManager() {
        return AZManager;
    }

    public String getPluginVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public void onDisable() {
        try {
            if (AZManager != null) {
                // Ferme proprement le canal PLSP + les listeners, sinon le serveur peut se plaindre au reload
                AZManager.close();
            }
        } catch (IOException e) {
            getLogger().severe("Error closing AZManager: " + e.getMessage());
        }
        Bukkit.getScheduler().cancelTasks(this);
    }
}
