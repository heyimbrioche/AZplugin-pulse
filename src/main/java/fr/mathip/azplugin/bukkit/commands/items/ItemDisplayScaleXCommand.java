package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;

public class ItemDisplayScaleXCommand implements ItemCommand {
    @Override
    public String name() {
        return "displayscalex";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Modifie l'échelle X de l'item dans l'inventaire";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item displayscalex <nombre|reset>");
            return;
        }
        NBTCompound pacDisplay = nbtItem.getOrCreateCompound("PacDisplay");
        if ("reset".equalsIgnoreCase(args[2])) {
            pacDisplay.removeKey("ScaleX");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e ScaleX retiré.");
            return;
        }
        try {
            // Étirement horizontal seulement (le Y se règle avec displayscaley)
            float sx = Float.parseFloat(args[2]);
            pacDisplay.setFloat("ScaleX", sx);
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e ScaleX mis à jour.");
        } catch (NumberFormatException e) {
            player.sendMessage("§cErreur : Le nombre est invalide.");
        }
    }
}
