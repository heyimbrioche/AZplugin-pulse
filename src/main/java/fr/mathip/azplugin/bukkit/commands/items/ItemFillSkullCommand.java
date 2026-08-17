package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.mathip.azplugin.bukkit.utils.AZColor;
import org.bukkit.entity.Player;

public class ItemFillSkullCommand implements ItemCommand {
    @Override
    public String name() {
        return "fillskull";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.fillskull";
    }

    @Override
    public String description() {
        return "Remplit le fond du crâne avec une couleur";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item fillskull <couleur|reset>");
            return;
        }
        if ("reset".equalsIgnoreCase(args[2])) {
            // mergeCompound ne sait pas retirer une clé, on passe par le compound directement
            nbtItem.getOrCreateCompound("PacDisplay").removeKey("FillSkull");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e FillSkull retiré.");
            return;
        }
        try {
            int color = AZColor.get0xAARRGGBB(args[2]);
            nbtItem.mergeCompound(new NBTContainer("{PacDisplay: {FillSkull: " + color + "}}"));
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e FillSkull appliqué.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cErreur : Couleur invalide.");
        }
    }
}
