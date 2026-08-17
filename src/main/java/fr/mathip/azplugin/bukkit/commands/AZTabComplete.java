package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.commands.items.ItemCommand;
import fr.mathip.azplugin.bukkit.config.ConfigManager;
import fr.mathip.azplugin.bukkit.config.PopupConfig;
import fr.mathip.azplugin.bukkit.handlers.PLSPPlayerModel;
import fr.mathip.azplugin.bukkit.handlers.PLSPWorldEnv;
import fr.mathip.azplugin.bukkit.packets.PacketPopup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AZTabComplete implements TabCompleter {
    // Autocomplétion de /az. Tout est géré au cas par cas selon la commande
    // et la position de l'argument (on regarde pas mal args.length).
    // Faut penser à ajouter ici chaque nouvelle commande qui a des arguments.
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (s.equalsIgnoreCase("az") && commandSender.hasPermission("azplugin.*")) {
            if (args.length == 1) {
                // Premier argument = le nom d'une commande
                List<String> completion = new ArrayList<>();
                for (AZCommand azCommand : CommandManager.getInstance().getCommands().values()) {
                    if (azCommand.name().startsWith(args[0])) {
                        completion.add(azCommand.name());
                    }
                }
                return completion;
            }
            // --- model ---
            if (args.length == 2 && args[0].equalsIgnoreCase("model")) {
                List<String> completion = new ArrayList<>();
                for (PLSPPlayerModel plspPlayerModel : PLSPPlayerModel.values()) {
                    if (plspPlayerModel.name().startsWith(args[1].toUpperCase())) {
                        completion.add(plspPlayerModel.name());
                    }
                }
                return completion;
            }
            // --- worldenv ---
            if (args.length == 2 && args[0].equalsIgnoreCase("worldenv")) {
                List<String> completion = new ArrayList<>();
                for (PLSPWorldEnv plspWorldEnv : PLSPWorldEnv.values()) {
                    if (plspWorldEnv.name().startsWith(args[1].toUpperCase())) {
                        completion.add(plspWorldEnv.name());
                    }
                }
                return completion;
            }
            // --- summon ---
            if (args.length == 2 && args[0].equalsIgnoreCase("summon")) {
                List<String> completion = new ArrayList<>();
                for (EntityType entityType : EntityType.values()) {
                    if (entityType.name().startsWith(args[1].toUpperCase())) {
                        completion.add(entityType.name());
                    }
                }
                return completion;
            }
            // --- popup ---
            if (args.length == 2 && args[0].equalsIgnoreCase("popup")) {
                List<String> completion = new ArrayList<>();
                for (PacketPopup popup : ConfigManager.getInstance().getPopupConfig().popups) {
                    if (popup.getName().startsWith(args[1])) {
                        completion.add(popup.getName());
                    }
                }
                return completion;
            }
            // --- stretch ---
            if (args.length == 2 && args[0].equalsIgnoreCase("stretch")) {
                List<String> completion = new ArrayList<>();
                completion.add("reset");
                completion.add("2");
                completion.add("0.5");
                completion.add("1.5");
                return completion;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("stretch") && !"reset".equalsIgnoreCase(args[1])) {
                List<String> completion = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                        completion.add(player.getName());
                    }
                }
                return completion;
            }
            // --- modeloffset ---
            if (args.length == 2 && args[0].equalsIgnoreCase("modeloffset")) {
                List<String> completion = new ArrayList<>();
                completion.add("reset");
                completion.add("0");
                completion.add("1");
                completion.add("-1");
                return completion;
            }
            if (args.length >= 4 && args[0].equalsIgnoreCase("modeloffset") && !"reset".equalsIgnoreCase(args[1])) {
                List<String> completion = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                        completion.add(player.getName());
                    }
                }
                return completion;
            }
            // --- eyeheight ---
            if (args.length == 2 && args[0].equalsIgnoreCase("eyeheight")) {
                List<String> completion = new ArrayList<>();
                completion.add("reset");
                completion.add("1.62");
                completion.add("3");
                completion.add("0.5");
                return completion;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("eyeheight") && !"reset".equalsIgnoreCase(args[1])) {
                List<String> completion = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                        completion.add(player.getName());
                    }
                }
                return completion;
            }
            // --- loadscreen ---
            if (args.length == 2 && args[0].equalsIgnoreCase("loadscreen")) {
                List<String> completion = new ArrayList<>();
                completion.add("open");
                completion.add("close");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completion.add(player.getName());
                    }
                }
                return completion;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("loadscreen")) {
                String a1 = args[1];
                String a2 = args[2];
                if (isPlayerName(a1)) {
                    // loadscreen <joueur> <open|close|?>
                    List<String> completion = new ArrayList<>();
                    completion.add("open");
                    completion.add("close");
                    return completion;
                } else if ("open".equalsIgnoreCase(a1)) {
                    // loadscreen open <ticks>
                    List<String> completion = new ArrayList<>();
                    completion.add("0");
                    completion.add("20");
                    completion.add("60");
                    completion.add("100");
                    return completion;
                }
            }
            if (args.length == 4 && args[0].equalsIgnoreCase("loadscreen") && !isPlayerName(args[1])) {
                // loadscreen <joueur> open <ticks>
                List<String> completion = new ArrayList<>();
                completion.add("0");
                completion.add("20");
                completion.add("60");
                completion.add("100");
                return completion;
            }
            // --- sidebar ---
            if (args.length == 2 && args[0].equalsIgnoreCase("sidebar")) {
                List<String> completion = new ArrayList<>();
                completion.add("clear");
                completion.add("line");
                return completion;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("sidebar") && "line".equalsIgnoreCase(args[1])) {
                List<String> completion = new ArrayList<>();
                completion.add("00000000-0000-0000-0000-000000000000");
                return completion;
            }
            // --- windowtitle ---
            if (args.length == 2 && args[0].equalsIgnoreCase("windowtitle")) {
                List<String> completion = new ArrayList<>();
                completion.add("101");
                completion.add("102");
                completion.add("103");
                return completion;
            }
            // --- chatdynamic ---
            if (args.length == 2 && args[0].equalsIgnoreCase("chatdynamic")) {
                List<String> completion = new ArrayList<>();
                completion.add("3");
                completion.add("5");
                completion.add("10");
                completion.add("15");
                return completion;
            }
            // --- challenge ---
            if (args.length == 2 && args[0].equalsIgnoreCase("challenge")) {
                List<String> completion = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completion.add(player.getName());
                    }
                }
                return completion;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("challenge")) {
                List<String> completion = new ArrayList<>();
                completion.add("5");
                completion.add("10");
                completion.add("15");
                completion.add("30");
                return completion;
            }
            // --- item ---
            if (args.length >= 2 && args[0].equalsIgnoreCase("item")) {
                List<String> completion = new ArrayList<>();
                if (args.length == 2) {
                    // Nom de la sous-commande item
                    for (ItemCommand itemCommand : CommandManager.getInstance().getItemCommands().values()) {
                        if (itemCommand.name().startsWith(args[1])) {
                            completion.add(itemCommand.name());
                        }
                    }
                    return completion;
                }
                // Au-delà, on délègue à la sous-commande elle-même (elle connaît ses args)
                if (!(commandSender instanceof Player)) {
                    return null;
                }
                for (ItemCommand itemCommand : CommandManager.getInstance().getItemCommands().values()) {
                    if (itemCommand.name().equalsIgnoreCase(args[1])) {
                        return itemCommand.suggest((Player) commandSender, args);
                    }
                }
            }
        }
        return null;
    }

    private boolean isPlayerName(String arg) {
        if (arg == null || arg.isEmpty()) return false;
        return Bukkit.getPlayer(arg) != null;
    }
}
