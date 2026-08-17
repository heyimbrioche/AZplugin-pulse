package fr.mathip.azplugin.bukkit.commands.items;

import java.util.List;

import org.bukkit.entity.Player;

import de.tr7zw.changeme.nbtapi.NBTItem;

// Même principe que AZCommand mais pour les sous-commandes de /az item.
// La différence : on reçoit l'item en main déjà converti en NBTItem,
// c'est la commande qui décide de le modifier ou pas.
public interface ItemCommand {
    public abstract String name();

    public abstract String permission();

    public abstract String description();

    public abstract void execute(Player player, NBTItem nbtItem, String[] args);

    // Suggestion d'arguments pour la tab completion (optionnel)
    default List<String> suggest(Player player, String[] args) {
        return null;
    }

}
