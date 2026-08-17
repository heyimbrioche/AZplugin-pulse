package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;

public class ItemDisplayTranslateXCommand implements ItemCommand {
    @Override
    public String name() {
        return "displaytx";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.pacdisplay";
    }

    @Override
    public String description() {
        return "Décale horizontalement l'item dans l'inventaire";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item displaytx <nombre|reset>");
            return;
        }
        NBTCompound pacDisplay = nbtItem.getOrCreateCompound("PacDisplay");
        if ("reset".equalsIgnoreCase(args[2])) {
            // Attention la clé s'appelle "TranslatX" sans le e, faute de frappe
            // dans le launcher, faut pas la "corriger" sinon plus rien ne marche
            pacDisplay.removeKey("TranslatX");
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e TranslatX retiré.");
            return;
        }
        try {
            float tx = Float.parseFloat(args[2]);
            pacDisplay.setFloat("TranslatX", tx);
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e TranslatX mis à jour.");
        } catch (NumberFormatException e) {
            player.sendMessage("§cErreur : Le nombre est invalide.");
        }
    }
}
