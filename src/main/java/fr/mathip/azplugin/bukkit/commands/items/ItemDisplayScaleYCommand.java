package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;

public class ItemDisplayScaleYCommand implements ItemCommand {
    @Override
    public String name() {
        return "displayscaley";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Modifie l'échelle Y de l'item dans l'inventaire";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item displayscaley <nombre|reset>");
            return;
        }
        NBTCompound pacDisplay = nbtItem.getOrCreateCompound("PacDisplay");
        if ("reset".equalsIgnoreCase(args[2])) {
            pacDisplay.removeKey("ScaleY");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e ScaleY retiré.");
            return;
        }
        try {
            // Étirement vertical seulement
            float sy = Float.parseFloat(args[2]);
            pacDisplay.setFloat("ScaleY", sy);
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e ScaleY mis à jour.");
        } catch (NumberFormatException e) {
            player.sendMessage("§cErreur : Le nombre est invalide.");
        }
    }
}
