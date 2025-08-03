package fr.soraxdubbing.futurespells;

import co.aikar.commands.PaperCommandManager;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.events.MagicSpellsLoadedEvent;
import com.nisovin.magicspells.mana.ManaHandler;
import fr.soraxdubbing.futurespells.logic.ManaPlayerManager;
import fr.soraxdubbing.futurespells.commands.ManaCommands;
import fr.soraxdubbing.futurespells.commands.SpellsCommands;
import fr.soraxdubbing.futurespells.storage.JsonManaPlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class FutureSpells extends JavaPlugin implements Listener {

    private static FutureSpells instance;

    private ManaPlayerManager manaPlayerManager;
    private PaperCommandManager paperCommandManager;
    private ManaHandler manaHandler;

    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().warning("[FutureSpells] Failed to create the directory!");
        }

        File manaFolder = new File(getDataFolder().getAbsolutePath(), "players");

        manaPlayerManager = new ManaPlayerManager(new JsonManaPlayerRepository(manaFolder, getLogger()));
        this.manaHandler = new PersistentManaHandler(manaPlayerManager, MagicSpells.plugin.getMagicConfig(), getLogger());
        MagicSpells.setManaHandler(this.manaHandler);

        getServer().getPluginManager().registerEvents(this, this);
        setupCommands();
    }

    @Override
    public void onDisable() {
        this.manaPlayerManager.saveAll();
    }

    private void setupCommands() {
        paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.enableUnstableAPI("help");
        paperCommandManager.registerDependency(ManaHandler.class, this.manaHandler);

        paperCommandManager.getCommandCompletions().registerAsyncCompletion("spells", context -> {
            try {
                String spellName = context.getInput();

                return MagicSpells.spells().stream()
                        .map(Spell::getName)
                        .filter(name -> name.toLowerCase().startsWith(spellName.toLowerCase()))
                        .collect(Collectors.toList());
            }
            catch (Exception e) {
                getLogger().severe("Error while completing spells: " + e.getMessage());
                throw new IllegalArgumentException("Error while completing spells.");
            }
        });

        paperCommandManager.getCommandContexts().registerContext(Spell.class, bukkitCommandExecutionContext -> {
            try {
                String spellName = bukkitCommandExecutionContext.popFirstArg();
                Spell spell = MagicSpells.getSpellByInGameName(spellName);

                if (spell == null) {
                    spell = MagicSpells.getSpellByInternalName(spellName);
                }

                if (spell == null) {
                    throw new IllegalArgumentException("Spell not found: " + spellName);
                }

                return spell;
            }
            catch (Exception e) {
                getLogger().severe("Error while getting spell context: " + e.getMessage());
                throw new IllegalArgumentException("Error while getting spell context.");
            }
        });

        paperCommandManager.registerCommand(new ManaCommands());
        paperCommandManager.registerCommand(new SpellsCommands());
    }

    @EventHandler
    public void OnMagicSpellsLoaded(MagicSpellsLoadedEvent event){
        getLogger().info("ManaHandler class name: " + MagicSpells.getManaHandler().getClass().getName());

        PersistentManaHandler persistentManaHandler = new PersistentManaHandler(manaPlayerManager, MagicSpells.plugin.getMagicConfig(), getLogger());
        MagicSpells.setManaHandler(persistentManaHandler);

        this.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                persistentManaHandler.showMana(onlinePlayer);
            }
        }, 20L);
    }

    public static FutureSpells getInstance() {
        return instance;
    }

    public ManaPlayerManager getManaPlayerManager() {
        return manaPlayerManager;
    }
}
