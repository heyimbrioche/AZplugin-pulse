package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.AZManager;
import fr.mathip.azplugin.bukkit.utils.AZChatComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketContainerTitle;

public class AZWindowTitle implements AZCommand {
    @Override
    public String name() {
        return "windowtitle";
    }

    @Override
    public String permission() {
        return "azplugin.command.windowtitle";
    }

    @Override
    public String description() {
        return "Change le titre d'un container";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c/az windowtitle <windowId> [index] <message...>");
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande est réservée aux joueurs.");
            return;
        }
        Player player = (Player) sender;
        int windowId;
        try {
            windowId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cL'ID doit être un nombre entier.");
            return;
        }
        // Index optionnel pour les containers à plusieurs titres (on l'a détecté en essayant de parser)
        int index = 0;
        int messageStart = 2;
        if (args.length >= 4) {
            try {
                index = Integer.parseInt(args[2]);
                messageStart = 3;
            } catch (NumberFormatException ex) {
                // Pas un index ? Alors c'est le début du titre directement
                messageStart = 2;
            }
        }
        StringBuilder titleBuilder = new StringBuilder();
        for (int i = messageStart; i < args.length; i++) {
            if (titleBuilder.length() > 0) titleBuilder.append(" ");
            titleBuilder.append(args[i]);
        }
        String titleText = titleBuilder.toString();
        if (titleText.isEmpty()) {
            sender.sendMessage("§cMessage vide.");
            return;
        }
        // Convertir les couleurs § en composant chat
        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(titleText));
        AZChatComponent component = new AZChatComponent(textComponent);

        PLSPPacketContainerTitle packet = new PLSPPacketContainerTitle();
        packet.setWindowId(windowId);
        packet.setTitle(component);
        packet.setIndex(index);
        AZManager.sendPLSPMessage(player, packet);
        sender.sendMessage("§a[AZPlugin]§e Titre du container modifié !");
    }
}
