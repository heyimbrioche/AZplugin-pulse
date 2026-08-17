package fr.mathip.azplugin.bukkit.module;

// Contrat pour tous les modules : activable/désactivable depuis la config,
    // chaque module connait sa propre section.
public interface Module {

    boolean isEnabled();

    void setEnable(boolean enable);

    String getConfigSection();

}
