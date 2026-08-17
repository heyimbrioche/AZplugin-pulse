package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemPacMenuCommand implements ItemCommand {
    @Override
    public String name() {
        return "pacmenu";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Modifie les propriétés du menu Pac";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item pacmenu background <true|false> | state <MODE|reset> | slothints <0-127|reset> | reset | resetall");
            return;
        }
        String what = args[2].toLowerCase();
        // "" = compound racine de l'item, le tag PacMenu vit dedans
        NBTCompound tag = nbtItem.getOrCreateCompound("");

        if ("resetall".equals(what)) {
            // Vire tout le tag PacMenu d'un coup
            tag.removeKey("PacMenu");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e Tag PacMenu retiré.");
            return;
        }

        if ("reset".equals(what) && args.length < 4) {
            NBTCompound menu = tag.getCompound("PacMenu");
            if (menu == null) {
                player.sendMessage("§7Aucun PacMenu.");
                return;
            }
            menu.clearNBT();
            tag.removeKey("PacMenu");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e PacMenu vidé et retiré.");
            return;
        }

        if ("background".equals(what)) {
            if (args.length < 4) {
                player.sendMessage("§c/az item pacmenu background <true|false>");
                return;
            }
            // Faut un booléen, le launcher n'accepte rien d'autre
            String v = args[3].toLowerCase();
            if (!"true".equals(v) && !"false".equals(v)) {
                player.sendMessage("§cUtilise true ou false.");
                return;
            }
            tag.getOrCreateCompound("PacMenu").setBoolean("Background", Boolean.parseBoolean(v));
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e PacMenu.Background mis à jour.");
            return;
        }

        if ("state".equals(what)) {
            if (args.length < 4) {
                player.sendMessage("§c/az item pacmenu state <DISABLED|NORMAL|HOVER|ACTIVE|reset>");
                return;
            }
            if ("reset".equalsIgnoreCase(args[3])) {
                NBTCompound m = tag.getCompound("PacMenu");
                if (m != null) {
                    m.removeKey("State");
                }
                player.setItemInHand(nbtItem.getItem());
                player.sendMessage("§a[AZPlugin]§e PacMenu.State retiré.");
                return;
            }
            String st = args[3].toUpperCase();
            // Les états sont fixés par le launcher, pas de valeur custom possible
            if (!"DISABLED".equals(st) && !"NORMAL".equals(st) && !"HOVER".equals(st) && !"ACTIVE".equals(st)) {
                player.sendMessage("§cÉtat invalide (DISABLED NORMAL HOVER ACTIVE).");
                return;
            }
            tag.getOrCreateCompound("PacMenu").setString("State", st);
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e PacMenu.State: §f" + st);
            return;
        }

        if ("slothints".equals(what)) {
            if (args.length < 4) {
                player.sendMessage("§c/az item pacmenu slothints <0-127|reset>");
                return;
            }
            if ("reset".equalsIgnoreCase(args[3])) {
                NBTCompound m = tag.getCompound("PacMenu");
                if (m != null) {
                    m.removeKey("SlotHints");
                }
                player.setItemInHand(nbtItem.getItem());
                player.sendMessage("§a[AZPlugin]§e PacMenu.SlotHints retiré.");
                return;
            }
            int hint;
            try {
                hint = Integer.parseInt(args[3]);
            } catch (NumberFormatException ex) {
                player.sendMessage("§cNombre invalide.");
                return;
            }
            // SlotHints = bitset de 7 bits indiquant quels slots le menu peut prendre
            if (hint < 0 || hint > 127) {
                player.sendMessage("§cUtilise 0-127.");
                return;
            }
            tag.getOrCreateCompound("PacMenu").setByte("SlotHints", (byte) hint);
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e PacMenu.SlotHints: §f" + hint);
            return;
        }

        player.sendMessage("§cSous-commande pacmenu inconnue.");
    }

    @Override
    public List<String> suggest(Player player, String[] args) {
        List<String> completion = new ArrayList<>();
        if (args.length == 3) {
            completion.add("background");
            completion.add("state");
            completion.add("slothints");
            completion.add("reset");
            completion.add("resetall");
            return completion;
        }
        if (args.length == 4) {
            String sub = args[2].toLowerCase();
            if ("background".equals(sub)) {
                completion.add("true");
                completion.add("false");
                return completion;
            }
            if ("state".equals(sub)) {
                completion.add("DISABLED");
                completion.add("NORMAL");
                completion.add("HOVER");
                completion.add("ACTIVE");
                completion.add("reset");
                return completion;
            }
            if ("slothints".equals(sub)) {
                completion.add("reset");
                completion.add("0");
                completion.add("6");
                completion.add("16");
                return completion;
            }
        }
        return null;
    }
}
