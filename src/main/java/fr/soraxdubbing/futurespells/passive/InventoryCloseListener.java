package fr.soraxdubbing.futurespells.passive;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.spells.passive.PassiveListener;
import com.nisovin.magicspells.spells.passive.PassiveTrigger;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.Util;
import fr.soraxdubbing.futurespells.FutureSpells;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoryCloseListener extends PassiveListener {

    private final List<PassiveSpell> spells = new ArrayList<PassiveSpell>();
    private final Set<String> inventoryNames = new HashSet<>();

    private final static Logger logger = FutureSpells.getInstance().getLogger();

    @Override
    public void registerSpell(PassiveSpell passiveSpell, PassiveTrigger passiveTrigger, String inventoryNamesString) {
        spells.add(passiveSpell);
        if (inventoryNamesString == null || inventoryNamesString.isEmpty()) {
            return;
        }

        String[] inventoryNamesArray = inventoryNamesString.split(",");
        for (String inventoryName : inventoryNamesArray) {
            this.inventoryNames.add(inventoryName.trim().toLowerCase());
        }
    }

    /**
     * Gère la fermeture d'un inventaire et active les sorts passifs associés si nécessaire.
     * Les sorts sont activés si le joueur possède le sort et si le nom de l'inventaire correspond (ou si aucun nom n'est filtré).
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        try {
            Player player = (Player) event.getPlayer();
            Spellbook playerSpellbook = MagicSpells.getSpellbook(player);
            if (playerSpellbook == null) {
                return;
            }

            String closedInventoryName = event.getInventory().getName();
            if (closedInventoryName == null) {
                return;
            }

            String closedInventoryNameLower = closedInventoryName.toLowerCase();
            spells.stream()
                    .filter(playerSpellbook::hasSpell)
                    .filter(passiveSpell -> inventoryNames.isEmpty() || inventoryNames.contains(closedInventoryNameLower))
                    .forEach(passiveSpell -> passiveSpell.activate(player));
        }
        catch (Exception e) {
            logger.log(Level.WARNING, "Erreur dans InventoryCloseListener pour le joueur " + event.getPlayer().getName(), e);
        }
    }
}
