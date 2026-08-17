package fr.mathip.azplugin.bukkit.module.playertag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.mathip.azplugin.bukkit.entity.AZPlayer;
import fr.mathip.azplugin.bukkit.entity.appearance.AZEntityTag;
import fr.mathip.azplugin.bukkit.entity.appearance.AZEntityTag.Rarity;
import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.module.Module;

public class PlayerTagModule implements Module, Listener {

    private boolean enable;
    private String CONFIG = "player-tag";

    public PlayerTagModule() {
        enable = false;
    }

    // Module playertag : donne un tag de rareté au joueur selon sa permission.
    // Attention, le module est OFF par défaut, faut l'activer dans la config.
    @EventHandler
    private void onJoint(PlayerJoinEvent event) {
        AZPlayer azPlayer = Main.getInstance().getAZManager().getPlayer(event.getPlayer());
        handleRarityTag(azPlayer);
    }

    private void handleRarityTag(AZPlayer azPlayer) {
        Player player = azPlayer.getPlayer();
        // Aucune permission de rareté = pas de tag, on s'en va
        if (player.hasPermission("azplugin.rarity")) {
        } else {
            return;
        }
        // Ordre important : on teste les raretés de la plus haute à la plus basse,
        // sinon un joueur avec la perm ultimate aurait la rareté "uncommon" par exemple
        if (player.hasPermission("azplugin.rarity.ultimate")) {
            azPlayer.setTag(AZEntityTag.builder().rarity(Rarity.ULTIMATE).build());
        } else if (player.hasPermission("azplugin.rarity.cosmic")) {
            azPlayer.setTag(AZEntityTag.builder().rarity(Rarity.COSMIC).build());
        } else if (player.hasPermission("azplugin.rarity.mythic")) {
            azPlayer.setTag(AZEntityTag.builder().rarity(Rarity.MYTHIC).build());
        } else if (player.hasPermission("azplugin.rarity.legendary")) {
            azPlayer.setTag(AZEntityTag.builder().rarity(Rarity.LEGENDARY).build());
        } else if (player.hasPermission("azplugin.rarity.epic")) {
            azPlayer.setTag(AZEntityTag.builder().rarity(Rarity.EPIC).build());
        } else if (player.hasPermission("azplugin.rarity.rare")) {
            azPlayer.setTag(AZEntityTag.builder().rarity(Rarity.RARE).build());
        } else if (player.hasPermission("azplugin.rarity.uncommon")) {
            azPlayer.setTag(AZEntityTag.builder().rarity(Rarity.UNCOMMON).build());
        }
        // On attend un peu que le joueur soit bien spawn côté launcher avant de renvoyer le tag
        Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                () -> azPlayer.flush(),
                10L);
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }

    // Activer/désactiver le module enregistre ou désenregistre les listeners,
    // comme ça zéro overhead quand le module est off.
    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
        if (enable) {
            Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        } else {
            HandlerList.unregisterAll(this);
        }
    }

    @Override
    public String getConfigSection() {
        return CONFIG;
    }
}
