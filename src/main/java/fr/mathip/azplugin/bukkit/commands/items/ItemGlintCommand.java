package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.mathip.azplugin.bukkit.utils.AZColor;
import org.bukkit.entity.Player;

public class ItemGlintCommand implements ItemCommand {
    @Override
    public String name() {
        return "glint";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Active/désactive le glint (effet enchantement)";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item glint <on|off> [couleur]");
            return;
        }
        String mode = args[2].toLowerCase();
        if ("on".equals(mode)) {
            if (args.length >= 4) {
                try {
                    // Glint activé + couleur custom
                    int color = AZColor.get0xAARRGGBB(args[3]);
                    nbtItem.mergeCompound(new NBTContainer("{PacDisplay: {Glint: 1b, GlintColor: " + color + "}}"));
                    player.setItemInHand(nbtItem.getItem());
                    player.sendMessage("§a[AZPlugin]§e Glint activé avec couleur.");
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cErreur : Couleur invalide.");
                }
            } else {
                nbtItem.mergeCompound(new NBTContainer("{PacDisplay: {Glint: 1b}}"));
                player.setItemInHand(nbtItem.getItem());
                player.sendMessage("§a[AZPlugin]§e Glint activé.");
            }
            return;
        }
        if ("off".equals(mode)) {
            // On retire les clés une par une (mergeCompound ne sait pas supprimer)
            nbtItem.getOrCreateCompound("PacDisplay").removeKey("Glint");
            nbtItem.getOrCreateCompound("PacDisplay").removeKey("GlintColor");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e Glint désactivé.");
            return;
        }
        player.sendMessage("§cUtilise on ou off.");
    }
}
