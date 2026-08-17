package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;

public class ItemTextCommand implements ItemCommand {
    @Override
    public String name() {
        return "text";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.text";
    }

    @Override
    public String description() {
        return "Transforme l'item en text";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c/az item text <text>");
            return;
        }
        // On recolle tous les mots après "text" pour avoir le texte complet
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String arg : args) {
            if (count > 1) {
                sb.append(" ").append(arg);
            }
            count++;
        }
        // Sprite EMOJI + le texte en SpriteData : l'item devient du texte rendu
        nbtItem.mergeCompound(
                new NBTContainer("{PacDisplay: {Sprite: \"EMOJI\", SpriteData: \"" + sb.toString() + "\"}}"));
        // setItemInHand : éviter setItemMeta qui strip les enchantements en 1.8
        player.setItemInHand(nbtItem.getItem());
        player.sendMessage("§a[AZPlugin]§e Texte appliqué sur l'item.");
    }
}
