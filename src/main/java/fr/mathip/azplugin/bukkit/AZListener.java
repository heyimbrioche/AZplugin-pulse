package fr.mathip.azplugin.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.mathip.azplugin.bukkit.config.ConfigManager;
import fr.mathip.azplugin.bukkit.entity.AZEntity;
import fr.mathip.azplugin.bukkit.entity.AZPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AZListener implements Listener {

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        // Plus besoin de regarder ses chunks s'il n'est plus là
        Main main = Main.getInstance();
        Player p = e.getPlayer();
        main.playersSeeChunks.remove(p);
    }

    @EventHandler
    void onJoint(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Main main = Main.getInstance();
        ConfigManager config = ConfigManager.getInstance();

        // Selon qu'il a le launcher ou non, on exécute pas les mêmes commandes
        // (pratique pour donner des items ou le stuff de base au bon moment)
        if (AZPlayer.hasAZLauncher(player)) {
            if (config.getJoinWithAZCommands() != null) {
                config.getJoinWithAZCommands().forEach(command -> {
                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                            command.replaceAll("%player%", player.getName()));
                });
            }
        } else {
            if (config.getJoinWithoutAZCommands() != null) {
                config.getJoinWithoutAZCommands().forEach(command -> {
                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                            command.replaceAll("%player%", player.getName()));
                });
            }
        }
        if (main.isUpdate && config.isUpdateMessage() && player.hasPermission("azplugin.update")) {
            // On prévient que du monde a une maj à faire, mais faut pas spammer tout le serveur
            player.sendMessage("§6Une nouvelle version du §bAZPlugin§6 a été détecté !");
            player.sendMessage("§bhttps://www.spigotmc.org/resources/azplugin.115548/");
        }
    }

    @EventHandler
    void onDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Player) return;
        // Une entité morte n'a plus besoin d'être suivie, on la sort de la liste
        Main.getAZManager().getEntyties().removeIf(azEntity -> entity.equals(azEntity.getEntity()));
    }

    // Intercepte les packets de spawn pour envoyer les infos (scale, tag, etc.)
    // au joueur qui va voir l'entité apparaître
    public AZListener(Main main) {
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new PacketAdapter(main, PacketType.Play.Server.SPAWN_ENTITY,
                        PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB,
                        PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        int entityId = event.getPacket().getIntegers().read(0);
                        Player player = event.getPlayer();
                        Entity entity = event.getPacket().getEntityModifier(player.getWorld()).read(0);
                        if (entity == null) return;
                        AZEntity azEntity = main.getAZManager().getEntityOrNull(entity);
                        if (azEntity != null) {
                            // flush envoie un packet PLSP : interdiction de le faire pendant
                            // que le packet spawn est en cours d'envoi, on défère au tick suivant
                            Bukkit.getScheduler().runTask(main, () -> azEntity.flush(player));
                        }
                    }
                });
    }

}
