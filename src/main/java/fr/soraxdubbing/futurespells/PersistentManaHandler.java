package fr.soraxdubbing.futurespells;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.ModifierSet;
import com.nisovin.magicspells.events.ManaChangeEvent;
import com.nisovin.magicspells.mana.ManaChangeReason;
import com.nisovin.magicspells.mana.ManaHandler;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.TimeUtil;
import com.nisovin.magicspells.util.compat.EventUtil;
import fr.soraxdubbing.futurespells.logic.ManaPlayer;
import fr.soraxdubbing.futurespells.logic.ManaPlayerManager;
import fr.soraxdubbing.futurespells.utils.Tick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersistentManaHandler extends ManaHandler {

    private final ManaPlayerManager manaPlayerManager;
    private final Logger logger;

    private Duration REGENERATION_DURATION;

    private String manaBarPrefix;
    private int manaBarSize;
    private ChatColor manaBarColorFull;
    private ChatColor manaBarColorEmpty;
    private final int manaBarToolSlot;

    private int defaultStartingMana;
    private final int defaultMaxMana;
    private final int defaultRegenAmount;

    private final boolean showManaOnUse;
    private final boolean showManaOnRegen;
    private final boolean showManaOnWoodTool;
    private final boolean showManaOnHungerBar;
    private final boolean showManaOnExperienceBar;

    private List<String> modifierList;
    private ModifierSet modifiers;

    private int taskId = -1;

    public PersistentManaHandler(ManaPlayerManager manaPlayerManager, MagicConfig config,Logger logger) {

        this.manaPlayerManager = manaPlayerManager;
        this.logger = logger;

        this.manaBarPrefix = config.getString("mana.mana-bar-prefix", "Mana:");
        this.manaBarSize = config.getInt("mana.mana-bar-size", 35);
        this.manaBarColorFull = ChatColor.getByChar(config.getString("mana.color-full", ChatColor.GREEN.getChar() + ""));
        this.manaBarColorEmpty = ChatColor.getByChar(config.getString("mana.color-empty", ChatColor.BLACK.getChar() + ""));
        this.manaBarToolSlot = config.getInt("mana.tool-slot", 8);

        this.REGENERATION_DURATION = Tick.toDuration(config.getInt("mana.regen-interval", TimeUtil.TICKS_PER_SECOND));

        this.defaultMaxMana = config.getInt("mana.default-max-mana", 100);
        this.defaultStartingMana = config.getInt("mana.default-starting-mana", this.defaultMaxMana);
        this.defaultRegenAmount = config.getInt("mana.default-regen-amount", 5);

        showManaOnUse = config.getBoolean("mana.show-mana-on-use", false);
        showManaOnRegen = config.getBoolean("mana.show-mana-on-regen", false);
        showManaOnWoodTool = config.getBoolean("mana.show-mana-on-wood-tool", true);
        showManaOnHungerBar = config.getBoolean("mana.show-mana-on-hunger-bar", false);
        showManaOnExperienceBar = config.getBoolean("mana.show-mana-on-experience-bar", false);

        modifierList = config.getStringList("mana.modifiers", null);

        this.taskId = MagicSpells.scheduleRepeatingTask(this::regenTask, Tick.fromDuration(REGENERATION_DURATION), Tick.fromDuration(REGENERATION_DURATION));
    }

    private void regenTask(){
        try {
            manaPlayerManager.getManaPlayers().stream()
                    .filter(manaPlayer ->  manaPlayer.getMana() < manaPlayer.getMaxMana())
                    .filter(manaPlayer -> {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(manaPlayer.getUuid());
                        return player != null && player.isOnline();
                    })
                    .forEach(manaPlayer -> {
                        Player player = Bukkit.getPlayer(manaPlayer.getUuid());
                        int oldMana = manaPlayer.getMana();
                        manaPlayer.setMana(manaPlayer.getMana() + manaPlayer.getRegenAmount());
                        manaPlayerManager.updateManaPlayer(manaPlayer);
                        ManaChangeEvent event = new ManaChangeEvent(player, oldMana, manaPlayer.getMana(), manaPlayer.getRegenAmount(), ManaChangeReason.REGEN);
                        EventUtil.call(event);
                        showMana(player, showManaOnRegen);
                    });
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error during mana regeneration task", e);
        }
    }

    @Override
    public void initialize() {
        try {
            if (modifierList == null || modifierList.isEmpty()) {
                return;
            }

            MagicSpells.debug(2, "Adding mana modifiers: " + modifierList);
            modifiers = new ModifierSet(this.modifierList);
            modifierList = null;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Could not initialize persistent mana handler", e);
        }
    }

    @Override
    public void createManaBar(Player player) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }

            MagicSpells.scheduleDelayedTask(() -> showMana(player), 11);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while creating mana bar for player " + player.getName(), e);
        }
    }

    @Override
    public boolean updateManaRankIfNecessary(Player player) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while updating mana rank for player " + player.getName(), e);
        }

        return false;
    }

    @Override
    public int getMaxMana(Player player) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }

            return manaPlayer.getMaxMana();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while getting max mana for player " + player.getName(), e);
        }

        return defaultMaxMana;
    }

    @Override
    public void setMaxMana(Player player, int i) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }
            manaPlayer.setMaxMana(i);
            manaPlayerManager.updateManaPlayer(manaPlayer);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while setting max mana for player " + player.getName(), e);
        }
    }

    @Override
    public int getRegenAmount(Player player) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }


            return manaPlayer.getRegenAmount();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while getting regen amount for player " + player.getName(), e);
        }

        return defaultRegenAmount;
    }

    @Override
    public void setRegenAmount(Player player, int i) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }


            manaPlayer.setRegenAmount(i);
            manaPlayerManager.updateManaPlayer(manaPlayer);
            this.showMana(player, showManaOnUse);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while setting regen amount for player " + player.getName(), e);
        }
    }

    @Override
    public int getMana(Player player) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }

            return manaPlayer.getMana();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while getting mana for player " + player.getName(), e);
        }

        return defaultStartingMana;
    }

    @Override
    public boolean hasMana(Player player, int i) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }

            return manaPlayer.getMana() >= i;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while checking mana for player " + player.getName(), e);
        }

        return false;
    }

    @Override
    public boolean removeMana(Player player, int i, ManaChangeReason manaChangeReason) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }

            int oldMana = manaPlayer.getMana();

            if (manaPlayer.getMana() >= i) {
                manaPlayer.setMana(manaPlayer.getMana() - i);
                manaPlayerManager.updateManaPlayer(manaPlayer);

                ManaChangeEvent event = new ManaChangeEvent(player, oldMana, manaPlayer.getMana(), manaPlayer.getMaxMana(), manaChangeReason);
                EventUtil.call(event);
                this.showMana(player, showManaOnUse);
                return true;
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while removing mana for player " + player.getName(), e);
        }

        return false;
    }

    @Override
    public boolean addMana(Player player, int i, ManaChangeReason manaChangeReason) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }

            int oldMana = manaPlayer.getMana();

            if (manaPlayer.getMana() + i <= manaPlayer.getMaxMana()) {
                manaPlayer.setMana(manaPlayer.getMana() + i);
                manaPlayerManager.updateManaPlayer(manaPlayer);

                ManaChangeEvent event = new ManaChangeEvent(player, oldMana, manaPlayer.getMana(), manaPlayer.getMaxMana(), manaChangeReason);
                EventUtil.call(event);
                this.showMana(player, showManaOnUse);
                return true;
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while adding mana for player " + player.getName(), e);
        }

        return false;
    }

    @Override
    public boolean setMana(Player player, int i, ManaChangeReason manaChangeReason) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
                manaPlayerManager.addManaPlayer(manaPlayer);
            }

            int oldMana = manaPlayer.getMana();

            if (i <= manaPlayer.getMaxMana()) {
                manaPlayer.setMana(i);
                manaPlayerManager.updateManaPlayer(manaPlayer);

                ManaChangeEvent event = new ManaChangeEvent(player, oldMana, manaPlayer.getMana(), manaPlayer.getMaxMana(), manaChangeReason);
                EventUtil.call(event);
                this.showMana(player, showManaOnUse);
                return true;
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while setting mana for player " + player.getName(), e);
        }

        return false;
    }

    @Override
    public void showMana(Player player, boolean showInChat) {
        try {
            ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

            if (manaPlayer == null) {
                manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
            }

            if(showInChat) {
                showManaInChat(player, manaPlayer);
            }

            if(showManaOnWoodTool) {
                showManaOnWoodTool(player, manaPlayer);
            }

            if(showManaOnHungerBar) {
                showManaOnHungerBar(player, manaPlayer);
            }

            if(showManaOnExperienceBar) {
                showManaOnExperienceBar(player, manaPlayer);
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while showing mana for player " + player.getName(), e);
        }
    }

    private void showManaInChat(Player player, ManaPlayer bar) {
        int segments = (int)(((double)bar.getMana()/(double)bar.getMaxMana()) * this.manaBarSize);
        StringBuilder text = new StringBuilder(MagicSpells.getTextColor() + manaBarPrefix + " {" + this.manaBarColorFull);
        int i = 0;
        for (; i < segments; i++) {
            text.append("=");
        }
        text.append(this.manaBarColorEmpty);
        for (; i < this.manaBarSize; i++) {
            text.append("=");
        }
        text.append(MagicSpells.getTextColor()).append("} [").append(bar.getMana()).append('/').append(bar.getMaxMana()).append(']');
        player.sendMessage(text.toString());
    }

    private void showManaOnWoodTool(Player player, ManaPlayer bar) {
        ItemStack item = player.getInventory().getItem(manaBarToolSlot);
        if (item == null) return;

        Material type = item.getType();
        if (type == Material.WOOD_AXE || type == Material.WOOD_HOE || type == Material.WOOD_PICKAXE || type == Material.WOOD_SPADE || type == Material.WOOD_SWORD) {
            int dur = 60 - (int)(((double)bar.getMana()/(double)bar.getMaxMana()) * 60);
            if (dur == 60) {
                dur = 59;
            } else if (dur == 0) {
                dur = 1;
            }
            item.setDurability((short)dur);
            player.getInventory().setItem(this.manaBarToolSlot, item);
        }
    }

    private void showManaOnHungerBar(Player player, ManaPlayer bar) {
        player.setFoodLevel(Math.round(((float)bar.getMana()/(float)bar.getMaxMana()) * 20));
        player.setSaturation(20);
    }

    private void showManaOnExperienceBar(Player player, ManaPlayer bar) {
        MagicSpells.getExpBarManager().update(player, bar.getMana(), (float)bar.getMana()/(float)bar.getMaxMana());
    }

    @Override
    public void turnOff() {
        try {
            manaPlayerManager.saveAll();
            MagicSpells.cancelTask(this.taskId);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while turning off PersistentManaHandler", e);
        }
    }

    @Override
    public ModifierSet getModifiers() {
        try {
            return modifiers;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while getting modifiers", e);
        }

        return new ModifierSet(new ArrayList<>());
    }
}
