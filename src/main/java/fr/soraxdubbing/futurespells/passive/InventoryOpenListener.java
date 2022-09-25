package fr.soraxdubbing.futurespells.passive;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.spells.passive.PassiveListener;
import com.nisovin.magicspells.spells.passive.PassiveTrigger;
import com.nisovin.magicspells.util.OverridePriority;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

public class InventoryOpenListener extends PassiveListener {

    List<PassiveSpell> spells = new ArrayList<PassiveSpell>();

    @Override
    public void registerSpell(PassiveSpell passiveSpell, PassiveTrigger passiveTrigger, String s) {
        spells.add(passiveSpell);
    }

    @OverridePriority
    @EventHandler
    public void onInventoryClose(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Spellbook spellbook = MagicSpells.getSpellbook(player);
        for (PassiveSpell spell : spells) {
            if (spellbook.hasSpell(spell)) {
                spell.activate(player);
            }
        }
    }
}
