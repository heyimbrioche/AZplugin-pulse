package fr.mathip.azplugin.bukkit.commands;


import org.bukkit.command.CommandSender;

// Contrat pour les commandes /az : nom, permission, description et l'action.
// Les arguments sont livrés tels quels par le CommandManager.
public interface AZCommand {

    public abstract String name();

    public abstract String permission();

    public abstract String description();

    public abstract void execute(CommandSender sender, String[] args);


}
