package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.mathip.azplugin.bukkit.utils.AZColor;
import org.bukkit.entity.Player;

public class ItemGlintColorCommand implements ItemCommand {
    @Override
    public String name() {
        return "glintcolor";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Modifie la couleur du glint";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item glintcolor <couleur|reset>");
            return;
        }
        if ("reset".equalsIgnoreCase(args[2])) {
            // "reset" = retour à la couleur de glint par défaut
            nbtItem.getOrCreateCompound("PacDisplay").removeKey("GlintColor");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e GlintColor retiré.");
            return;
        }
        try {
            int color = AZColor.get0xAARRGGBB(args[2]);
            nbtItem.mergeCompound(new NBTContainer("{PacDisplay: {GlintColor: " + color + "}}"));
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e GlintColor appliqué.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cErreur : Couleur invalide.");
        }
    }
}
