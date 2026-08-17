package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.AZManager;
import fr.mathip.azplugin.bukkit.utils.AZChatComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pactify.client.api.plprotocol.model.gui.PactifyScoreboardOperation;
import pactify.client.api.plsp.packet.client.PLSPPacketScoreboardSidebar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AZSidebar implements AZCommand {
    @Override
    public String name() {
        return "sidebar";
    }

    @Override
    public String permission() {
        return "azplugin.command.sidebar";
    }

    @Override
    public String description() {
        return "Gère la sidebar du launcher";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/az sidebar clear | /az sidebar line <uuid> <message...>");
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande est réservée aux joueurs.");
            return;
        }
        Player player = (Player) sender;
        String mode = args[1].toLowerCase();

        if ("clear".equals(mode)) {
            // "clear" = on vire toutes les lignes de la sidebar
            List<PactifyScoreboardOperation> ops = new ArrayList<>();
            ops.add(new PactifyScoreboardOperation.RemoveAllLines());
            PLSPPacketScoreboardSidebar packet = new PLSPPacketScoreboardSidebar(ops);
            AZManager.sendPLSPMessage(player, packet);
            player.sendMessage("§a[AZPlugin]§e Sidebar effacée.");
            return;
        }

        if ("line".equals(mode)) {
            if (args.length < 4) {
                sender.sendMessage("§c/az sidebar line <uuid> <message...>");
                return;
            }
            // Chaque ligne a un UUID propre, comme ça on peut la modifier sans toucher aux autres
            UUID lineId;
            try {
                lineId = UUID.fromString(args[2]);
            } catch (IllegalArgumentException ex) {
                player.sendMessage("§cUUID invalide.");
                return;
            }
            StringBuilder msgBuilder = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                if (msgBuilder.length() > 0) msgBuilder.append(" ");
                msgBuilder.append(args[i]);
            }
            String message = msgBuilder.toString();
            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(message));
            AZChatComponent component = new AZChatComponent(textComponent);

            List<PactifyScoreboardOperation> ops = new ArrayList<>();
            ops.add(new PactifyScoreboardOperation.SetLine(
                    lineId, (short) 0,
                    PactifyScoreboardOperation.Alignment.LEFT,
                    component
            ));
            PLSPPacketScoreboardSidebar packet = new PLSPPacketScoreboardSidebar(ops);
            AZManager.sendPLSPMessage(player, packet);
            player.sendMessage("§a[AZPlugin]§e Ligne sidebar ajoutée.");
            return;
        }

        sender.sendMessage("§cUtilise clear ou line.");
    }
}
