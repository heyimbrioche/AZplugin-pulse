package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.AZManager;
import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.utils.AZChatComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketChatMessage;
import pactify.client.api.plsp.packet.client.PLSPPacketPopupAlert;

import java.util.UUID;

public class AZChallenge implements AZCommand {
    @Override
    public String name() {
        return "challenge";
    }

    @Override
    public String permission() {
        return "azplugin.command.challenge";
    }

    @Override
    public String description() {
        return "Lance un challenge sur un joueur (popup + compte à rebours)";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c/az challenge <joueur> <secondes>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("§cCe joueur est hors-ligne.");
            return;
        }
        int seconds;
        try {
            seconds = Integer.parseInt(args[2]);
        } catch (NumberFormatException ex) {
            sender.sendMessage("§cLes secondes doivent être un entier.");
            return;
        }
        if (seconds < 1) {
            sender.sendMessage("§cLe challenge doit durer au moins 1 seconde.");
            return;
        }
        // Au-delà de 60s ça devient du spam inutile, on plafonne
        if (seconds > 60) seconds = 60;

        // Popup de début côté launcher
        TextComponent popupText = new TextComponent(TextComponent.fromLegacyText("§eChallenge AZ: commence!"));
        AZChatComponent popupComponent = new AZChatComponent(popupText);
        PLSPPacketPopupAlert popupPacket = new PLSPPacketPopupAlert();
        popupPacket.setText(popupComponent);
        AZManager.sendPLSPMessage(target, popupPacket);

        final int secondsTotal = seconds;
        final UUID messageId = UUID.randomUUID();
        // L'état du compte à rebours vit dans un objet, la tâche s'auto-relance
        // chaque seconde tant que ce n'est pas fini (scheduleSyncDelayedTask en bas)
        class CountdownState {
            int remaining = secondsTotal;
        }
        final CountdownState state = new CountdownState();

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                // Le joueur peut se déco pendant le challenge, on abandonne
                if (!target.isOnline()) return;
                int r = state.remaining;
                if (r <= 0) {
                    // Message de fin : on retire la ligne dynamique puis on met le "terminé"
                    PLSPPacketChatMessage removePacket = new PLSPPacketChatMessage(
                            PLSPPacketChatMessage.Action.REMOVE, messageId, null
                    );
                    AZManager.sendPLSPMessage(target, removePacket);
                    TextComponent doneText = new TextComponent(TextComponent.fromLegacyText("§aChallenge terminé! ✅"));
                    AZChatComponent doneComponent = new AZChatComponent(doneText);
                    PLSPPacketChatMessage donePacket = new PLSPPacketChatMessage(
                            PLSPPacketChatMessage.Action.ADD, messageId, doneComponent
                    );
                    AZManager.sendPLSPMessage(target, donePacket);
                    target.sendMessage("§aChallenge terminé!");
                    return;
                }
                String legacy = "§eChallenge: §c" + r + " §eseconde(s)...";
                TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(legacy));
                AZChatComponent component = new AZChatComponent(textComponent);

                // ADD la première fois, REPLACE ensuite pour garder le même message à jour
                PLSPPacketChatMessage.Action action = (r == secondsTotal)
                        ? PLSPPacketChatMessage.Action.ADD
                        : PLSPPacketChatMessage.Action.REPLACE;
                PLSPPacketChatMessage packet = new PLSPPacketChatMessage(action, messageId, component);
                AZManager.sendPLSPMessage(target, packet);

                state.remaining--;
                // 20 ticks = 1 seconde, et on se relance nous-même
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), this, 20L);
            }
        });

        sender.sendMessage("§a[AZPlugin]§e Challenge lancé pour §f" + target.getName()
                + "§a (§f" + secondsTotal + "s§a).");
    }
}
