package fr.mathip.azplugin.bukkit;

import fr.mathip.azplugin.bukkit.entity.AZEntity;
import fr.mathip.azplugin.bukkit.entity.AZPlayer;
import fr.mathip.azplugin.bukkit.utils.PLSPPacketBuffer;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import pactify.client.api.mcprotocol.util.NotchianPacketUtil;
import pactify.client.api.plsp.PLSPPacket;
import pactify.client.api.plsp.PLSPPacketHandler;
import pactify.client.api.plsp.PLSPProtocol;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * Cerveau du plugin côté serveur : garde en mémoire les infos de chaque
 * joueur/entité et envoie les packets PLSP vers le launcher.
 * Un AZPlayer = un joueur connecté, créé au login et supprimé au quit.
 */
public class AZManager implements Listener, Closeable {
    private final Plugin plugin;
    private final Map<UUID, AZPlayer> players;
    // CopyOnWriteArrayList parce que l'entité peut mourir pendant qu'on itère dessus
    private final List<AZEntity> entities = new CopyOnWriteArrayList<>();

    public AZManager(final Plugin plugin) {
        this.players = new HashMap<UUID, AZPlayer>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        // Canal de communication avec le launcher, sans ça aucun packet ne part
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "PLSP");
    }

    // LOWEST pour bien stocker le joueur avant tout le reste
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        // On garde l'hostname (le launcher l'envoie sous forme d'IP custom)
        event.getPlayer().setMetadata("AZPlugin:hostname", new FixedMetadataValue(this.plugin, event.getHostname()));
        final AZPlayer AZPlayer;
        this.players.put(event.getPlayer().getUniqueId(), AZPlayer = new AZPlayer(event.getPlayer()));
        AZPlayer.init();
    }

    // Si le login est refusé (serveur plein, ban...), autant nettoyer tout de suite
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLoginMonitor(final PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            this.playerQuit(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final AZPlayer AZPlayer = this.getPlayer(event.getPlayer());
        AZPlayer.join();
    }

    public AZPlayer getPlayer(final Player player) {
        return this.players.get(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.playerQuit(event.getPlayer());
    }

    private void playerQuit(final Player player) {
        // Retirer de la map = l'état joueur est clean, plus aucune tâche ne le référence
        final AZPlayer AZPlayer = this.players.remove(player.getUniqueId());
        if (AZPlayer != null) {
            AZPlayer.free();
        }
    }

    public List<AZEntity> getEntyties() {
        return entities;
    }

    public List<AZPlayer> getAZPlayers() {
        return new ArrayList<>(players.values());
    }

    // Retourne l'entité si on la connait déjà (le launcher a déjà des infos dessus),
    // sinon null. A ne pas confondre avec getEntity qui en crée une nouvelle.
    public AZEntity getEntityOrNull(Entity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof Player) {
            return getPlayer((Player) entity);
        }
        for (AZEntity ret : this.entities) {
            if (entity.equals(ret.getEntity())) {
                return ret;
            }
        }
        return null;
    }

    // Ici on crée l'AZEntity si elle n'existe pas encore
    public AZEntity getEntity(Entity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof Player) {
            return getPlayer((Player) entity);
        }
        for (AZEntity ret : this.entities) {
            if (entity.equals(ret.getEntity())) {
                return ret;
            }
        }
        AZEntity azEntity = new AZEntity(entity);
        entities.add(azEntity);
        return azEntity;
    }

    // Envoi d'un packet PLSP au joueur : on écrit l'ID du packet puis ses données
    // dans le buffer, et on expédie le tout via le canal custom.
    public static void sendPLSPMessage(final Player player, final PLSPPacket<PLSPPacketHandler.ClientHandler> message) {
        if (player == null || message == null) {
            return;
        }
        try {
            final PLSPPacketBuffer buf = new PLSPPacketBuffer();
            final PLSPProtocol.PacketData<?> packetData = PLSPProtocol.getClientPacketByClass(message.getClass());
            if (packetData == null) {
                // Arrive quand on ajoute un packet sans l'enregistrer dans le protocole
                Main.getInstance().getLogger().warning("Unknown packet class: " + message.getClass().getName());
                return;
            }
            NotchianPacketUtil.writeString(buf, packetData.getId(), 32767);
            message.write(buf);
            player.sendPluginMessage(Main.getInstance(), "PLSP", buf.toBytes());
        } catch (Exception e) {
            Main.getInstance().getLogger().log(Level.WARNING,
                    "Exception sending PLSP message to " + player.getName() + ":", e);
        }
    }

    public void close() throws IOException {
        // Nettoyage au disable/reload, sinon le canal reste enregistré et plus rien ne marche
        HandlerList.unregisterAll(this);
        this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(this.plugin, "PLSP");
    }

    public Plugin getPlugin() {
        return this.plugin;
    }
}
