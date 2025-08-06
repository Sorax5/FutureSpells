package fr.soraxdubbing.futurespells.passive;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.spells.passive.PassiveListener;
import com.nisovin.magicspells.spells.passive.PassiveTrigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.soraxdubbing.futurespells.FutureSpells;

public class InventoryOpenListener extends PassiveListener {

    private final List<PassiveSpell> passiveSpells = new ArrayList<>();
    private final Set<String> inventoryNames = new HashSet<>();

    private static final Logger logger = FutureSpells.getInstance().getLogger();

    @Override
    public void registerSpell(PassiveSpell passiveSpell, PassiveTrigger passiveTrigger, String inventoryNamesString) {
        passiveSpells.add(passiveSpell);
        if (inventoryNamesString == null || inventoryNamesString.isEmpty()) {
            return;
        }
        String[] inventoryNamesArray = inventoryNamesString.split(",");
        for (String inventoryName : inventoryNamesArray) {
            this.inventoryNames.add(inventoryName.trim().toLowerCase());
        }
    }

    /**
     * Gère l'ouverture d'un inventaire et active les sorts passifs associés si nécessaire.
     * Les sorts sont activés si le joueur possède le sort et si le nom de l'inventaire correspond (ou si aucun nom n'est filtré).
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        try {
            Player player = (Player) event.getPlayer();
            Spellbook playerSpellbook = MagicSpells.getSpellbook(player);
            if (playerSpellbook == null) {
                return;
            }
            String openedInventoryName = event.getInventory().getName();
            if (openedInventoryName == null) return;
            String openedInventoryNameLower = openedInventoryName.toLowerCase();
            passiveSpells.stream()
                    .filter(playerSpellbook::hasSpell)
                    .filter(passiveSpell ->
                            inventoryNames.isEmpty()
                            || inventoryNames.contains(openedInventoryNameLower)
                            || player.getInventory().getName().equalsIgnoreCase(openedInventoryNameLower)
                    )
                    .forEach(passiveSpell -> passiveSpell.activate(player));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erreur dans InventoryOpenListener pour le joueur " + event.getPlayer().getName(), e);
        }
    }
}
