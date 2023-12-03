package fr.soraxdubbing.futurespells;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import app.ashcon.intake.fluent.DispatcherNode;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.events.MagicSpellsLoadedEvent;
import fr.soraxdubbing.futurespells.command.ManaCommands;
import fr.soraxdubbing.futurespells.command.SpellsCommands;
import fr.soraxdubbing.futurespells.command.providers.MagicSpellsModule;
import fr.soraxdubbing.futurespells.logic.ManaPlayerManager;
import fr.soraxdubbing.futurespells.storage.JsonManaPlayerDao;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class FutureSpells extends JavaPlugin implements Listener {

    private static FutureSpells instance;

    private ManaPlayerManager manaPlayerManager;

    @Override
    public void onLoad() {
        BasicBukkitCommandGraph cmdGraph = new BasicBukkitCommandGraph(new MagicSpellsModule());

        DispatcherNode chakra = cmdGraph.getRootDispatcherNode().registerNode("chakra");
        chakra.registerCommands(new ManaCommands());

        DispatcherNode spells = cmdGraph.getRootDispatcherNode().registerNode("spells");
        spells.registerCommands(new SpellsCommands());

        // REGISTER COMMANDS
        BukkitIntake bukkitIntake = new BukkitIntake(this, cmdGraph);
        bukkitIntake.register();
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        getLogger().info("FutureSpells is loading...");

        // get manaHandler class name
        getLogger().info("ManaHandler class name: " + MagicSpells.getManaHandler().getClass().getName());

        manaPlayerManager = new ManaPlayerManager(new JsonManaPlayerDao(getDataFolder().getAbsolutePath() + "/players"));
        PersistentManaHandler persistentManaHandler = new PersistentManaHandler(manaPlayerManager, MagicSpells.plugin.getMagicConfig());
        MagicSpells.setManaHandler(persistentManaHandler);

        getLogger().info("ManaHandler class name: " + MagicSpells.getManaHandler().getClass().getName());

        this.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            getLogger().info("ManaHandler class name: " + MagicSpells.getManaHandler().getClass().getName());
        }, 20L);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.manaPlayerManager.saveAll();
    }

    @EventHandler
    public void OnMagicSpellsLoaded(MagicSpellsLoadedEvent event){
        getLogger().info("ManaHandler class name: " + MagicSpells.getManaHandler().getClass().getName());

        PersistentManaHandler persistentManaHandler = new PersistentManaHandler(manaPlayerManager, MagicSpells.plugin.getMagicConfig());
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
