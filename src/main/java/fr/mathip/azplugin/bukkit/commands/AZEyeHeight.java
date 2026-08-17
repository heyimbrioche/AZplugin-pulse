package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.entity.AZPlayer;
import fr.mathip.azplugin.bukkit.entity.appearance.AZEntityModel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AZEyeHeight implements AZCommand {
    @Override
    public String name() {
        return "eyeheight";
    }

    @Override
    public String permission() {
        return "azplugin.command.eyeheight";
    }

    @Override
    public String description() {
        return "Change la hauteur des yeux du joueur";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/az eyeheight <stand> [sneak] [joueur]");
            return;
        }
        if ("reset".equalsIgnoreCase(args[1])) {
            Player target = getTarget(sender, args, 2);
            if (target == null) return;
            AZPlayer azTarget = Main.getAZManager().getPlayer(target);
            if (azTarget == null) {
                sender.sendMessage("§cErreur: Impossible de trouver le joueur.");
                return;
            }
            // Remise à zéro : on garde le modèle et les offsets, on vide les eye heights
            AZEntityModel current = azTarget.getModel();
            AZEntityModel updated = new AZEntityModel(
                    current != null ? current.getModelId() : -1,
                    current != null ? current.getOffsetX() : 0,
                    current != null ? current.getOffsetY() : 0,
                    current != null ? current.getOffsetZ() : 0,
                    Float.NaN, Float.NaN, Float.NaN, Float.NaN
            );
            if (updated.isNull()) {
                azTarget.setModel(null);
            } else {
                azTarget.setModel(updated);
            }
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> azTarget.flush(), 1);
            sender.sendMessage("§a[AZPlugin]§e EyeHeight réinitialisé pour §f" + target.getName());
            return;
        }
        float stand, sneak;
        try {
            stand = Float.parseFloat(args[1]);
            // Pas de sneak précisé ? On prend ~85% du stand, approximation vanilla
            sneak = args.length >= 3 && !isOnlinePlayer(args[2]) ? Float.parseFloat(args[2]) : stand * 0.85F;
        } catch (NumberFormatException ex) {
            sender.sendMessage("§cLes hauteurs doivent être des nombres.");
            return;
        }
        // Si le 3e argument est un joueur en ligne, c'est lui la cible
        int targetArgIdx = args.length >= 3 && !isOnlinePlayer(args[2]) ? 3 : 2;
        Player target = getTarget(sender, args, targetArgIdx);
        if (target == null) return;
        AZPlayer azTarget = Main.getAZManager().getPlayer(target);
        if (azTarget == null) {
            sender.sendMessage("§cErreur: Impossible de trouver le joueur.");
            return;
        }
        AZEntityModel current = azTarget.getModel();
        AZEntityModel updated = new AZEntityModel(
                current != null ? current.getModelId() : -1,
                current != null ? current.getOffsetX() : 0,
                current != null ? current.getOffsetY() : 0,
                current != null ? current.getOffsetZ() : 0,
                stand, sneak, Float.NaN, Float.NaN
        );
        azTarget.setModel(updated.isNull() ? null : updated);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> azTarget.flush(), 1);
        sender.sendMessage("§a[AZPlugin]§e EyeHeight appliqué à §f" + target.getName()
                + "§a: stand=§f" + stand + "§a sneak=§f" + sneak);
    }

    private boolean isOnlinePlayer(String arg) {
        return Bukkit.getPlayer(arg) != null;
    }

    private Player getTarget(CommandSender sender, String[] args, int index) {
        Player target;
        if (args.length > index) {
            target = Bukkit.getPlayer(args[index]);
            if (target == null) {
                sender.sendMessage("§cCe joueur est hors-ligne !");
                return null;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage("§cErreur: Vous devez être un joueur pour exécuter cette commande");
            return null;
        }
        return target;
    }
}
