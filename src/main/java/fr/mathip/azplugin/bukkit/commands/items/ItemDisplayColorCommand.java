package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.mathip.azplugin.bukkit.utils.AZColor;
import org.bukkit.entity.Player;

public class ItemDisplayColorCommand implements ItemCommand {
    @Override
    public String name() {
        return "displaycolor";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Modifie la couleur de l'item dans l'inventaire";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item displaycolor <couleur|reset>");
            return;
        }
        NBTCompound pacDisplay = nbtItem.getOrCreateCompound("PacDisplay");
        if ("reset".equalsIgnoreCase(args[2])) {
            pacDisplay.removeKey("Color");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e Couleur retirée.");
            return;
        }
        try {
            // La couleur de l'item dans l'inventaire, pas celle du rendu 3D
            int color = AZColor.get0xAARRGGBB(args[2]);
            pacDisplay.setInteger("Color", color);
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e Couleur appliquée.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cErreur : Couleur invalide.");
        }
    }
}
