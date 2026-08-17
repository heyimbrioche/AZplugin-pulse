package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;

public class ItemDisplayRotationCommand implements ItemCommand {
    @Override
    public String name() {
        return "displayrot";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Modifie la rotation de l'item dans l'inventaire";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item displayrot <degres|reset>");
            return;
        }
        NBTCompound pacDisplay = nbtItem.getOrCreateCompound("PacDisplay");
        if ("reset".equalsIgnoreCase(args[2])) {
            pacDisplay.removeKey("Rotation");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e Rotation retirée.");
            return;
        }
        try {
            // Rotation en degrés, sens horaire
            float rot = Float.parseFloat(args[2]);
            pacDisplay.setFloat("Rotation", rot);
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e Rotation mise à jour.");
        } catch (NumberFormatException e) {
            player.sendMessage("§cErreur : Le nombre est invalide.");
        }
    }
}
