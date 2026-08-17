package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.entity.AZPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AZList implements AZCommand{
    @Override
    public String name() {
        return "list";
    }

    @Override
    public String permission() {
        return "azplugin.command.list";
    }

    @Override
    public String description() {
        return "Obtient la liste des joueurs qui sont sur le AZ Launcher ou non";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // On sépare les joueurs en deux camps : ceux avec le launcher, les autres
        List<String> pactifyList = new ArrayList<>();
        List<String> vanillaList = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (AZPlayer.hasAZLauncher(player)) {
                pactifyList.add(player.getName());
                continue;
            }
            vanillaList.add(player.getName());
        }
        // Tri alphabétique histoire d'avoir une liste propre
        pactifyList.sort(String::compareToIgnoreCase);
        vanillaList.sort(String::compareToIgnoreCase);
        sender.sendMessage(ChatColor.YELLOW + "Les joueurs qui utilisent le AZ launcher: " + (
                pactifyList.isEmpty() ? (ChatColor.GRAY + "(Aucun)") : (ChatColor.GREEN + String.join(", ", (Iterable)pactifyList))));
        sender.sendMessage(ChatColor.YELLOW + "Les joueurs qui n'utilisent pas le AZ launcher: " + (
                vanillaList.isEmpty() ? (ChatColor.GRAY + "(Aucun)") : (ChatColor.RED + String.join(", ", (Iterable)vanillaList))));

    }
}
