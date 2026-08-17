package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.entity.AZPlayer;
import fr.mathip.azplugin.bukkit.entity.appearance.AZEntityModel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AZModelOffset implements AZCommand {
    @Override
    public String name() {
        return "modeloffset";
    }

    @Override
    public String permission() {
        return "azplugin.command.modeloffset";
    }

    @Override
    public String description() {
        return "Decale le modele du joueur (X Y Z)";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/az modeloffset <x> <y> <z> [joueur]");
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
            // On remet les offsets à 0 mais on garde le modèle actuel et les eye heights
            AZEntityModel current = azTarget.getModel();
            AZEntityModel updated = new AZEntityModel(
                    current != null ? current.getModelId() : -1,
                    0, 0, 0,
                    Float.NaN, Float.NaN, Float.NaN, Float.NaN
            );
            if (updated.isNull()) {
                azTarget.setModel(null);
            } else {
                azTarget.setModel(updated);
            }
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> azTarget.flush(), 1);
            sender.sendMessage("§a[AZPlugin]§e Offset du modèle réinitialisé pour §f" + target.getName());
            return;
        }
        if (args.length < 4) {
            sender.sendMessage("§c/az modeloffset <x> <y> <z> [joueur]");
            return;
        }
        float x, y, z;
        try {
            x = Float.parseFloat(args[1]);
            y = Float.parseFloat(args[2]);
            z = Float.parseFloat(args[3]);
        } catch (NumberFormatException ex) {
            sender.sendMessage("§cLes coordonnées doivent être des nombres.");
            return;
        }
        Player target = getTarget(sender, args, 4);
        if (target == null) return;
        AZPlayer azTarget = Main.getAZManager().getPlayer(target);
        if (azTarget == null) {
            sender.sendMessage("§cErreur: Impossible de trouver le joueur.");
            return;
        }
        // On garde le modèle et les eye heights déjà réglés, on change que les offsets
        AZEntityModel current = azTarget.getModel();
        AZEntityModel updated = new AZEntityModel(
                current != null ? current.getModelId() : -1,
                x, y, z,
                current != null ? current.getEyeHeightStand() : Float.NaN,
                current != null ? current.getEyeHeightSneak() : Float.NaN,
                current != null ? current.getEyeHeightSleep() : Float.NaN,
                current != null ? current.getEyeHeightElytra() : Float.NaN
        );
        azTarget.setModel(updated.isNull() ? null : updated);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> azTarget.flush(), 1);
        sender.sendMessage("§a[AZPlugin]§e Offset du modèle appliqué à §f" + target.getName()
                + "§a: X=§f" + x + "§a Y=§f" + y + "§a Z=§f" + z);
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
