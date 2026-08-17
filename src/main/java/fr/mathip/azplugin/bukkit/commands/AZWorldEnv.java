package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.AZManager;
import fr.mathip.azplugin.bukkit.entity.AZPlayer;
import fr.mathip.azplugin.bukkit.handlers.PLSPWorldEnv;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketWorldEnv;

public class AZWorldEnv implements AZCommand{
    @Override
    public String name() {
        return "worldenv";
    }

    @Override
    public String permission() {
        return "azplugin.command.worldenv";
    }

    @Override
    public String description() {
        return "Change le ciel d'un joueur";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player target;
        PLSPWorldEnv worldEnv;
        try {
            worldEnv = PLSPWorldEnv.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cErreur: L'environnement '" + args[1] + "' est invalide ! Utilisez NORMAL, NETHER ou THE_END.");
            return;
        }


        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage("§cCe joueur est hors-ligne !");
                return;
            }
        } else {
            target = (Player) sender;
            if (target == null) {
                sender.sendMessage("§cErreur: Vous devez être un joueur pour executer cette commande");
                return;
            }
        }
        AZPlayer azPlayer = Main.getAZManager().getPlayer(target);

        // On envoie direct le packet au launcher, pas besoin de passer par l'AZPlayer
        PLSPPacketWorldEnv worldEnvPacket = new PLSPPacketWorldEnv();
        worldEnvPacket.setName(target.getWorld().getName());
        worldEnvPacket.setType(worldEnv.name());

        AZManager.sendPLSPMessage(target, worldEnvPacket);
        sender.sendMessage("§achangement de d'environnement effectué !");

    }
}
