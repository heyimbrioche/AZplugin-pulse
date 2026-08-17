package fr.mathip.azplugin.bukkit.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fr.mathip.azplugin.bukkit.config.ConfigManager;
import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.entity.AZPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class PacketWindow implements Listener {
    private static final Map<UUID, Integer> windowId = new HashMap<>();
    public static final Set<UUID> customWindow = new HashSet<>();

    public PacketWindow(Main plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.TRANSACTION) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                UUID uuid = event.getPlayer().getUniqueId();
                PacketContainer packet = event.getPacket();
                if (AZPlayer.hasAZLauncher(player)) {
                    if (customWindow.contains(uuid)) {
                        boolean getAccepted = packet.getBooleans().read(0);
                        if (!getAccepted) {
                            packet.getBooleans().write(0, true);
                        }
                        event.setPacket(packet);
                    }
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.OPEN_WINDOW) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                UUID uuid = event.getPlayer().getUniqueId();
                PacketContainer packet = event.getPacket();
                if (AZPlayer.hasAZLauncher(player)) {
                    int currentId = windowId.getOrDefault(uuid, 100);
                    currentId++;
                    if (currentId >= 201) {
                        currentId -= 100;
                    }
                    windowId.put(uuid, currentId);
                    if (customWindow.contains(uuid)) {
                        packet.getIntegers().write(0, currentId);
                        event.setPacket(packet);
                    }
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.CLOSE_WINDOW) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                UUID uuid = event.getPlayer().getUniqueId();
                PacketContainer packet = event.getPacket();
                if (AZPlayer.hasAZLauncher(player)) {
                    if (customWindow.contains(uuid)) {
                        packet.getIntegers().write(0, windowId.get(uuid)-100);
                        event.setPacket(packet);
                        remove(player);
                    }
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.WINDOW_CLICK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                UUID uuid = event.getPlayer().getUniqueId();
                PacketContainer packet = event.getPacket();
                if (AZPlayer.hasAZLauncher(player)) {
                    if (customWindow.contains(uuid)) {
                        packet.getIntegers().write(0, windowId.get(uuid)-100);
                        event.setPacket(packet);
                    }
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                UUID uuid = event.getPlayer().getUniqueId();
                PacketContainer packet = event.getPacket();
                if (AZPlayer.hasAZLauncher(player)) {
                    if (customWindow.contains(uuid)) {
                        packet.getIntegers().write(0, windowId.get(uuid));
                        event.setPacket(packet);
                    }
                }
            }
        });

        // SET_SLOT doit être réécrit comme WINDOW_ITEMS : sans cela, les plugins
        // qui mettent à jour un inventaire spécial en place (setItem sur
        // l'inventaire ouvert) envoient des packets avec le mauvais window id,
        // ignorés par le launcher (ex : interface de trade qui ne s'actualise pas).
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_SLOT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                UUID uuid = event.getPlayer().getUniqueId();
                PacketContainer packet = event.getPacket();
                if (AZPlayer.hasAZLauncher(player)) {
                    if (customWindow.contains(uuid)) {
                        packet.getIntegers().write(0, windowId.get(uuid));
                        event.setPacket(packet);
                    }
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.WINDOW_DATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                UUID uuid = event.getPlayer().getUniqueId();
                PacketContainer packet = event.getPacket();
                if (AZPlayer.hasAZLauncher(player)) {
                    if (customWindow.contains(uuid)) {
                        packet.getIntegers().write(0, windowId.get(uuid));
                        event.setPacket(packet);
                    }
                }
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = event.getPlayer().getUniqueId();
        if (AZPlayer.hasAZLauncher(player)) {
            windowId.put(uuid, 100);
            remove(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = event.getPlayer().getUniqueId();
        if (AZPlayer.hasAZLauncher(player)) {
            windowId.remove(uuid);
            remove(player);
        }
    }
    @EventHandler
    void OnInventoryOpen(InventoryOpenEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player p = (Player) e.getPlayer();
            List<String> specialCharacters = ConfigManager.getInstance().getSpecialInventoryCharacters();
            boolean special = false;
            if (specialCharacters != null) {
                for (String character : specialCharacters) {
                    if (e.getInventory().getTitle().contains(character)) {
                        special = true;
                        break;
                    }
                }
            }
            if (special) {
                PacketWindow.customWindow.add(p.getUniqueId());
            } else {
                // Inventaire non spécial : on sort le joueur de customWindow. Sans
                // cela, une fenêtre spéciale fermée CÔTÉ SERVEUR (closeInventory
                // d'un plugin, fin de trade...) laissait le joueur "custom" : la
                // fenêtre suivante (ex: /vhead) devenait transparente à tort sur
                // le launcher jusqu'à la prochaine fermeture côté client.
                PacketWindow.customWindow.remove(p.getUniqueId());
            }
        }
    }

    private static void closeInventoryReceive(Player player) {
        UUID uuid = player.getUniqueId();
        if (customWindow.contains(uuid)) {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Client.CLOSE_WINDOW);
            packet.getIntegers().write(0, windowId.get(uuid)-100);
            try {
                ProtocolLibrary.getProtocolManager().receiveClientPacket(player, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void remove(Player player) {
        UUID uuid = player.getUniqueId();
        if (AZPlayer.hasAZLauncher(player)) {
            customWindow.remove(uuid);
        }
    }
}
