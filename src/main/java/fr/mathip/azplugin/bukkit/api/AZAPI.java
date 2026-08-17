package fr.mathip.azplugin.bukkit.api;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.mathip.azplugin.bukkit.Main;
import fr.mathip.azplugin.bukkit.entity.AZEntity;
import fr.mathip.azplugin.bukkit.entity.AZPlayer;

/**
 * API publique pour les plugins tiers.
 * Tout passe par Main.getAZManager(), on ne fait que du "relay" ici
 * pour que les autres plugins aient pas à toucher aux classes internes.
 */
public class AZAPI {

    /**
     * Récupère l'AZPlayer (wrapper) d'un joueur.
     *
     * @param player le joueur Bukkit
     * @return l'AZPlayer, ou null si pas trouvé (rare)
     */
    public static AZPlayer getPlayer(Player player) {
        return Main.getAZManager().getPlayer(player);
    }

    /**
     * Tous les AZPlayers actuellement connectés.
     *
     * @return la liste des AZPlayers
     */
    public static List<AZPlayer> getPlayers() {
        return Main.getAZManager().getAZPlayers();
    }

    /**
     * Récupère l'AZEntity d'une entité.
     * Retourne null si l'entité n'est pas suivie (pas encore spawn par ex).
     *
     * @param entity l'entité Bukkit
     * @return l'AZEntity, ou null si pas suivie
     */
    public static AZEntity getEntityOrNull(Entity entity) {
        return Main.getAZManager().getEntityOrNull(entity);
    }

    /**
     * Comme getEntityOrNull, sauf que si l'entité n'existe pas encore
     * elle est créée et enregistrée au passage.
     *
     * @param entity l'entité Bukkit
     * @return l'AZEntity
     */
    public static AZEntity getEntity(Entity entity) {
        return Main.getAZManager().getEntity(entity);
    }

    /**
     * La liste de toutes les entités suivies.
     *
     * @return liste des AZEntities
     */
    public static List<AZEntity> getEntities() {
        return Main.getAZManager().getEntyties();
    }
}
