package fr.soraxdubbing.futurespells.passive;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.spells.passive.PassiveListener;
import com.nisovin.magicspells.spells.passive.PassiveTrigger;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.Util;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryCloseListener extends PassiveListener {

    List<PassiveSpell> spells = new ArrayList<PassiveSpell>();
    private final Set<String> inventoryNames = new HashSet<>();

    @Override
    public void registerSpell(PassiveSpell passiveSpell, PassiveTrigger passiveTrigger, String var) {
        spells.add(passiveSpell);
        if (var == null || var.isEmpty()) return;

        String[] split = var.split(",");
        for (String s : split) {
            inventoryNames.add(s.trim());
        }
    }

    @OverridePriority
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Spellbook spellbook = MagicSpells.getSpellbook(player);

        for (PassiveSpell spell : spells) {
            if (spellbook.hasSpell(spell)) {
                if(event.getInventory().getName() == event.getPlayer().getInventory().getName()){
                    spell.activate(player);
                }
            }
        }
    }
}
