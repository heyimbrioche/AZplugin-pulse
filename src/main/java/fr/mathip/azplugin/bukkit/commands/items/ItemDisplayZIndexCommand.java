package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;

public class ItemDisplayZIndexCommand implements ItemCommand {
    @Override
    public String name() {
        return "displayzindex";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Modifie l'ordre d'affichage de l'item dans l'inventaire";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item displayzindex <nombre|reset>");
            return;
        }
        NBTCompound pacDisplay = nbtItem.getOrCreateCompound("PacDisplay");
        if ("reset".equalsIgnoreCase(args[2])) {
            pacDisplay.removeKey("ZIndex");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e ZIndex retiré.");
            return;
        }
        try {
            // ZIndex = profondeur d'affichage, plus haut = au-dessus des autres items
            float z = Float.parseFloat(args[2]);
            pacDisplay.setFloat("ZIndex", z);
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e ZIndex mis à jour.");
        } catch (NumberFormatException e) {
            player.sendMessage("§cErreur : Le nombre est invalide.");
        }
    }
}
