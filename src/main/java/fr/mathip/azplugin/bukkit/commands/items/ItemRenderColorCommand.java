package fr.mathip.azplugin.bukkit.commands.items;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.mathip.azplugin.bukkit.utils.AZColor;
import org.bukkit.entity.Player;

public class ItemRenderColorCommand implements ItemCommand {
    @Override
    public String name() {
        return "rendercolor";
    }

    @Override
    public String permission() {
        return "azplugin.command.item.rendercolor";
    }

    @Override
    public String description() {
        return "Modifie les couleurs du rendu (fill ombre contour)";
    }

    @Override
    public void execute(Player player, NBTItem nbtItem, String[] args) {
        if (args.length < 5) {
            player.sendMessage("§c/az item rendercolor <fill> <ombre> <contour>");
            return;
        }
        try {
            // Le tableau Color = [fill, ombre, contour] dans cet ordre, le launcher lit les 3
            int fill = AZColor.get0xAARRGGBB(args[2]);
            int ombre = AZColor.get0xAARRGGBB(args[3]);
            int contour = AZColor.get0xAARRGGBB(args[4]);
            nbtItem.mergeCompound(new NBTContainer(
                    "{PacRender: {Color: [I;" + fill + "," + ombre + "," + contour + "]}}"
            ));
            // setItemInHand : éviter setItemMeta qui strip les enchantements en 1.8
            player.setItemInHand(nbtItem.getItem());
            player.sendMessage("§a[AZPlugin]§e Couleurs du rendu mises à jour.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cErreur : Couleur(s) hex invalide(s).");
        }
    }
}
