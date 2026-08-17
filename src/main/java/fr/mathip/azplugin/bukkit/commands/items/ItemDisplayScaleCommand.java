package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;

public class ItemDisplayScaleCommand implements ItemCommand {
    @Override
    public String name() {
        return "displayscale";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Modifie l'échelle d'affichage de l'item";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item displayscale <nombre>");
            return;
        }
        try {
            // Scale = taille d'affichage dans l'inventaire (le launcher lit le "f")
            float scale = Float.parseFloat(args[2]);
            nbtItem.mergeCompound(new NBTContainer("{PacDisplay: {Scale: " + scale + "f}}"));
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e Scale modifié.");
        } catch (NumberFormatException e) {
            player.sendMessage("§cErreur : Le nombre est invalide.");
        }
    }
}
