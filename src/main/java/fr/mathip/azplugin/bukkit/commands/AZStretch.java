package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.entity.AZPlayer;
import fr.mathip.azplugin.bukkit.entity.appearance.AZEntityScale;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AZStretch implements AZCommand {
    @Override
    public String name() {
        return "stretch";
    }

    @Override
    public String permission() {
        return "azplugin.command.stretch";
    }

    @Override
    public String description() {
        return "Change le stretch (W H D) d'un joueur";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c/az stretch <width> <height> [depth] [joueur]");
            return;
        }
        // "reset" remet le stretch par défaut (scale à null = le launcher reprend ses valeurs)
        if ("reset".equalsIgnoreCase(args[1])) {
            Player target = args.length >= 3 ? Bukkit.getPlayer(args[2]) : null;
            if (target == null && sender instanceof Player) {
                target = (Player) sender;
            }
            if (target == null) {
                sender.sendMessage("§cCe joueur est hors-ligne !");
                return;
            }
            AZPlayer azTarget = Main.getAZManager().getPlayer(target);
            azTarget.setScale(null);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> azTarget.flush(), 1);
            sender.sendMessage("§a[AZPlugin]§e Stretch réinitialisé pour §f" + target.getName());
            return;
        }
        float renderW, renderH, renderD;
        try {
            renderW = Float.parseFloat(args[1]);
            renderH = Float.parseFloat(args[2]);
            // Pas de depth précisé ? On reprend la width, plus simple pour l'utilisateur
            renderD = args.length >= 4 && !isOnlinePlayer(args[3]) ? Float.parseFloat(args[3]) : renderW;
        } catch (NumberFormatException ex) {
            sender.sendMessage("§cLes valeurs doivent être des nombres.");
            return;
        }
        // Si le 3e (ou 4e) argument est un nom de joueur en ligne, c'est lui la cible
        int targetArgIdx = args.length >= 4 && !isOnlinePlayer(args[3]) ? 4 : 3;
        Player target = getTarget(sender, args, targetArgIdx);
        if (target == null) return;
        AZPlayer azTarget = Main.getAZManager().getPlayer(target);
        AZEntityScale scale = new AZEntityScale();
        // On renseigne tout : le rendu ET la hitbox, sinon le joueur tape à côté
        scale.setRenderWidth(renderW);
        scale.setRenderHeight(renderH);
        scale.setRenderDepth(renderD);
        scale.setBboxWidth(renderW);
        scale.setBboxHeight(renderH);
        scale.setItemInHandWidth(renderW);
        scale.setItemInHandHeight(renderH);
        scale.setItemInHandDepth(renderD);
        // On compense le nametag pour qu'il garde une taille lisible, même tout étiré
        scale.setNameTags(1.0F / Math.max(renderW, 0.01F));
        azTarget.setScale(scale);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> azTarget.flush(), 1);
        sender.sendMessage("§a[AZPlugin]§e Stretch appliqué à §f" + target.getName()
                + "§a: W=§f" + renderW + "§a H=§f" + renderH + "§a D=§f" + renderD);
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
