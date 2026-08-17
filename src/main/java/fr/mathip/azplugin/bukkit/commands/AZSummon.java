package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.entity.AZEntity;
import fr.mathip.azplugin.bukkit.entity.appearance.AZEntityScale;
import fr.mathip.azplugin.bukkit.handlers.PLSPPlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class AZSummon implements AZCommand {
    @Override
    public String name() {
        return "summon";
    }

    @Override
    public String permission() {
        return "azplugin.command.summon";
    }

    @Override
    public String description() {
        return "Invoque une entité avec une taille diférente";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        float size;
        Location location;
        Player player = (Player) sender;
        // Sans coordonnées : on invoque là où se trouve le sender
        if (args.length == 3) {
            if (player == null) {
                sender.sendMessage("Usage: /az summon <entity> <taille> <x> <y> <z>");
                return;
            } else {
                location = player.getLocation();
            }
        } else if (args.length >= 6) {
            // Avec coordonnées explicites
            try {
                float x = Float.parseFloat(args[3]);
                float y = Float.parseFloat(args[4]);
                float z = Float.parseFloat(args[5]);
                World world;
                if (player != null) {
                    world = player.getWorld();
                } else {
                    // Pas de joueur en face, on prend le monde principal
                    world = Bukkit.getWorld("world");
                }
                location = new Location(world, x, y, z);

            } catch (NumberFormatException e) {
                sender.sendMessage("§cErreur: La location est invalide");
                return;
            }

        } else {
            sender.sendMessage("§cUsage: /az summon <entité> <taille> [<x> <y> <z>]");
            return;
        }
        try {
            size = Float.parseFloat(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cErreur: La taille est invalide");
            return;
        }
        PLSPPlayerModel model;
        try {
            // Le modèle PLSP contient l'ID de l'entité vanilla à spawn
            model = PLSPPlayerModel.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cErreur: Le modèle '" + args[1] + "' est invalide !");
            return;
        }
        // On spawn une vraie entité, puis on lui applique la taille custom
        Entity entity = location.getWorld().spawnEntity(location,
                EntityType.fromId(model.getId()));
        if (entity == null) {
            sender.sendMessage("§cErreur: Impossible de créer l'entité !");
            return;
        }
        AZEntity azEntity = Main.getAZManager().getEntity(entity);
        azEntity.setScale(new AZEntityScale(size));
        azEntity.flush();
        sender.sendMessage("§a[AZPlugin]§e entité crée avec succès");

    }
}
