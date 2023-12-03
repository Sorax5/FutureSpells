package fr.soraxdubbing.futurespells.buff;


import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;

import fr.soraxdubbing.futurespells.CastData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.SpellFilter;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.events.SpellApplyDamageEvent;

public class DamageEmpowerSpell extends BuffSpell {

    private final Map<UUID, CastData> entities;

    private SpellFilter filter;

    private Float damageMultiplier;

    public DamageEmpowerSpell(MagicConfig config, String spellName) {
        super(config, spellName);

        damageMultiplier = getConfigFloat("damage-multiplier", 1.5F);

        List<String> spells = getConfigStringList("spells", null);
        List<String> deniedSpells = getConfigStringList("denied-spells", null);
        List<String> tagList = getConfigStringList("spell-tags", null);
        List<String> deniedTagList = getConfigStringList("denied-spell-tags", null);

        filter = new SpellFilter(spells, deniedSpells, tagList, deniedTagList);

        entities = new HashMap<>();
    }

    @Override
    public boolean castBuff(Player player, float v, String[] strings) {
        entities.put(player.getUniqueId(), new CastData(v, strings));
        return true;
    }

    @Override
    public boolean isActive(Player player) {
        return entities.containsKey(player.getUniqueId());
    }

    @Override
    protected void turnOffBuff(Player player) {
        entities.remove(player.getUniqueId());
    }

    @Override
    protected void turnOff() {
        entities.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpellApplyDamage(SpellApplyDamageEvent event) {
        Player caster = event.getCaster();
        if (!isActive(caster)) return;
        if (!filter.check(event.getSpell())) return;

        addUseAndChargeCost(caster);

        CastData data = entities.get(caster.getUniqueId());
        float dmgM = this.damageMultiplier * data.power();
        this.damageMultiplier = dmgM;
        event.applyDamageModifier(dmgM);
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
