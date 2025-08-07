package fr.soraxdubbing.futurespells.buff;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.soraxdubbing.futurespells.CastData;
import fr.soraxdubbing.futurespells.FutureSpells;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.SpellFilter;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.events.SpellApplyDamageEvent;

/**
 * Spell qui augmente les dégâts infligés par le joueur.
 */
public class DamageEmpowerSpell extends BuffSpell {

    // Utilisation de ConcurrentHashMap pour la sécurité des threads
    private final Map<UUID, CastData> entities;

    private SpellFilter filter;
    private Float damageMultiplier;

    /**
     * Constructeur du sort DamageEmpowerSpell
     */
    public DamageEmpowerSpell(MagicConfig config, String spellName) {
        super(config, spellName);

        damageMultiplier = getConfigFloat("damage-multiplier", 1.5F);

        List<String> spells = getConfigStringList("spells", null);
        List<String> deniedSpells = getConfigStringList("denied-spells", null);
        List<String> tagList = getConfigStringList("spell-tags", null);
        List<String> deniedTagList = getConfigStringList("denied-spell-tags", null);

        filter = new SpellFilter(spells, deniedSpells, tagList, deniedTagList);
        entities = new ConcurrentHashMap<>();
    }

    /**
     * Applique le buff au joueur
     */
    @Override
    public boolean castBuff(Player player, float v, String[] strings) {
        try {
            entities.put(player.getUniqueId(), new CastData(v, strings));
        } catch (Exception e) {
            FutureSpells.getInstance().getLogger().log(Level.WARNING, "Erreur lors du cast de damage-empower-spell pour le joueur " + player.getName(), e);
            return false;
        }
        return true;
    }

    /**
     * Retire le buff du joueur
     */
    public void removeBuff(Player player) {
        entities.remove(player.getUniqueId());
    }

    /**
     * Récupère les données de cast pour un joueur
     */
    public Optional<CastData> getCastData(Player player) {
        return Optional.ofNullable(entities.get(player.getUniqueId()));
    }

    @Override
    public boolean isActive(Player player) {
        return entities.containsKey(player.getUniqueId());
    }

    @Override
    protected void turnOffBuff(Player player) {
        try {
            entities.remove(player.getUniqueId());
        }
        catch (Exception e) {
            FutureSpells.getInstance().getLogger().log(Level.WARNING, "Error in turning off damage-empower-spell for player " + player.getName(), e);
        }
    }

    @Override
    protected void turnOff() {
        try {
            entities.clear();
        }
        catch (Exception e) {
            logger.log(Level.WARNING, "Error in turning off damage-empower-spell for all players", e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpellApplyDamage(SpellApplyDamageEvent event) {
        try {
            Player caster = event.getCaster();
            if (!caster.isOnline()) {
                return;
            }

            if (!isActive(caster)) {
                return;
            }

            if (!filter.check(event.getSpell())) {
                return;
            }

            addUseAndChargeCost(caster);

            CastData data = entities.get(caster.getUniqueId());

            float dmgM = this.damageMultiplier * data.power();
            this.damageMultiplier = dmgM;
            event.applyDamageModifier(dmgM);
        }
        catch (Exception e) {
            FutureSpells.getInstance().getLogger().log(Level.WARNING, "Error in handling SpellApplyDamageEvent for DamageEmpowerSpell", e);
        }
    }

    public Map<UUID, CastData> getEntities() {
        return entities;
    }

    public SpellFilter getFilter() {
        return filter;
    }

    public void setFilter(SpellFilter filter) {
        this.filter = filter;
    }
}
