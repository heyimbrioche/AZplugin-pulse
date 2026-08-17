package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.AZManager;
import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.utils.AZChatComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketChatMessage;

import java.util.UUID;

public class AZChatDynamic implements AZCommand {
    @Override
    public String name() {
        return "chatdynamic";
    }

    @Override
    public String permission() {
        return "azplugin.command.chatdynamic";
    }

    @Override
    public String description() {
        return "Démo de message dynamique dans le chat";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande est réservée aux joueurs.");
            return;
        }
        Player player = (Player) sender;
        int total = 5;
        if (args.length >= 2) {
            try {
                total = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage("§cNombre de secondes invalide.");
                return;
            }
        }
        if (total < 1) total = 1;
        if (total > 60) total = 60;

        final int secondsTotal = total;
        final UUID messageId = UUID.randomUUID();

        class CountdownState {
            int remaining = secondsTotal;
        }
        final CountdownState state = new CountdownState();

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                int r = state.remaining;
                if (r <= 0) {
                    // Compte à rebours fini : on retire le message du chat côté launcher
                    PLSPPacketChatMessage removePacket = new PLSPPacketChatMessage(
                            PLSPPacketChatMessage.Action.REMOVE, messageId, null
                    );
                    AZManager.sendPLSPMessage(player, removePacket);
                    player.sendMessage("§7[Demo] Message retiré.");
                    return;
                }
                String legacy = "§eTéléportation dans §c" + r + " §eseconde(s)... §7(1 ligne)";
                TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(legacy));
                AZChatComponent component = new AZChatComponent(textComponent);

                // Premier affichage = ADD, ensuite on remplace la même ligne (même UUID)
                PLSPPacketChatMessage.Action action = (r == secondsTotal)
                        ? PLSPPacketChatMessage.Action.ADD
                        : PLSPPacketChatMessage.Action.REPLACE;
                PLSPPacketChatMessage packet = new PLSPPacketChatMessage(action, messageId, component);
                AZManager.sendPLSPMessage(player, packet);

                state.remaining--;
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), this, 20L);
            }
        }, 0L);

        sender.sendMessage("§a[AZPlugin]§e Démo: compte à rebours sur une ligne PLSP (§f" + secondsTotal + "§as).");
    }
}
