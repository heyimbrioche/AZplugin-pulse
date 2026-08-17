package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.AZManager;
import fr.mathip.azplugin.bukkit.entity.AZPlayer;
import fr.mathip.azplugin.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketUiAction;

public class AZLoadScreen implements AZCommand {
    @Override
    public String name() {
        return "loadscreen";
    }

    @Override
    public String permission() {
        return "azplugin.command.loadscreen";
    }

    @Override
    public String description() {
        return "Ouvre/ferme l'écran de chargement";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/az loadscreen [joueur] <open|close> [ticks]");
            return;
        }
        Player target;
        String modeRaw;
        int tickArgIndex;

        // Deux syntaxes : /az loadscreen <open|close> (soi-même) ou /az loadscreen <joueur> <open|close>
        if (isMode(args[1])) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c/az loadscreen <joueur> <open|close> [ticks]");
                return;
            }
            target = (Player) sender;
            modeRaw = args[1];
            tickArgIndex = 2;
        } else if (args.length >= 3 && isMode(args[2])) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cCe joueur est hors-ligne !");
                return;
            }
            modeRaw = args[2];
            tickArgIndex = 3;
        } else {
            sender.sendMessage("§c/az loadscreen [joueur] <open|close> [ticks]");
            return;
        }

        String mode = modeRaw.toLowerCase();
        if ("open".equals(mode)) {
            int ticks = 0;
            if (args.length > tickArgIndex) {
                try {
                    ticks = Integer.parseInt(args[tickArgIndex]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§cLa durée doit être un nombre entier (ticks).");
                    return;
                }
            }
            // OPEN_LOAD : le launcher affiche l'écran de chargement pendant X ticks
            PLSPPacketUiAction packet = new PLSPPacketUiAction("OPEN_LOAD", String.valueOf(ticks));
            AZManager.sendPLSPMessage(target, packet);
            sender.sendMessage("§a[AZPlugin]§e Écran de chargement ouvert pour §f" + target.getName());
            return;
        }

        if ("close".equals(mode)) {
            PLSPPacketUiAction packet = new PLSPPacketUiAction("CLOSE_LOAD", "");
            AZManager.sendPLSPMessage(target, packet);
            sender.sendMessage("§a[AZPlugin]§e Écran de chargement fermé pour §f" + target.getName());
            return;
        }

        sender.sendMessage("§cUtilise open ou close.");
    }

    private boolean isMode(String arg) {
        if (arg == null) return false;
        String m = arg.toLowerCase();
        return "open".equals(m) || "close".equals(m);
    }
}
