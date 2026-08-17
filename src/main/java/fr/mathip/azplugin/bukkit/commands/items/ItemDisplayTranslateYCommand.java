package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;

public class ItemDisplayTranslateYCommand implements ItemCommand {
    @Override
    public String name() {
        return "displayty";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Décale verticalement l'item dans l'inventaire";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item displayty <nombre|reset>");
            return;
        }
        NBTCompound pacDisplay = nbtItem.getOrCreateCompound("PacDisplay");
        if ("reset".equalsIgnoreCase(args[2])) {
            // "reset" = on retire la clé, le launcher revient au décalage par défaut
            pacDisplay.removeKey("TranslateY");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e TranslateY retiré.");
            return;
        }
        try {
            float ty = Float.parseFloat(args[2]);
            pacDisplay.setFloat("TranslateY", ty);
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e TranslateY mis à jour.");
        } catch (NumberFormatException e) {
            player.sendMessage("§cErreur : Le nombre est invalide.");
        }
    }
}
