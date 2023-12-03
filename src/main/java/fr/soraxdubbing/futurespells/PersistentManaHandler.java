package fr.soraxdubbing.futurespells;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.ModifierSet;
import com.nisovin.magicspells.events.ManaChangeEvent;
import com.nisovin.magicspells.mana.ManaBar;
import com.nisovin.magicspells.mana.ManaChangeReason;
import com.nisovin.magicspells.mana.ManaHandler;
import com.nisovin.magicspells.mana.ManaRank;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.TimeUtil;
import com.nisovin.magicspells.util.compat.EventUtil;
import fr.soraxdubbing.futurespells.logic.ManaPlayer;
import fr.soraxdubbing.futurespells.logic.ManaPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class PersistentManaHandler extends ManaHandler {

    private final ManaPlayerManager manaPlayerManager;

    private String manaBarPrefix;
    private int manaBarSize;
    private ChatColor manaBarColorFull;
    private ChatColor manaBarColorEmpty;
    private final int manaBarToolSlot;

    private int regenInterval;
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

    public PersistentManaHandler(ManaPlayerManager manaPlayerManager, MagicConfig config) {

        this.manaPlayerManager = manaPlayerManager;

        this.manaBarPrefix = config.getString("mana.mana-bar-prefix", "Mana:");
        this.manaBarSize = config.getInt("mana.mana-bar-size", 35);
        this.manaBarColorFull = ChatColor.getByChar(config.getString("mana.color-full", ChatColor.GREEN.getChar() + ""));
        this.manaBarColorEmpty = ChatColor.getByChar(config.getString("mana.color-empty", ChatColor.BLACK.getChar() + ""));
        this.manaBarToolSlot = config.getInt("mana.tool-slot", 8);

        this.regenInterval = config.getInt("mana.regen-interval", TimeUtil.TICKS_PER_SECOND);
        this.defaultMaxMana = config.getInt("mana.default-max-mana", 100);
        this.defaultStartingMana = config.getInt("mana.default-starting-mana", this.defaultMaxMana);
        this.defaultRegenAmount = config.getInt("mana.default-regen-amount", 5);

        showManaOnUse = config.getBoolean("mana.show-mana-on-use", false);
        showManaOnRegen = config.getBoolean("mana.show-mana-on-regen", false);
        showManaOnWoodTool = config.getBoolean("mana.show-mana-on-wood-tool", true);
        showManaOnHungerBar = config.getBoolean("mana.show-mana-on-hunger-bar", false);
        showManaOnExperienceBar = config.getBoolean("mana.show-mana-on-experience-bar", false);

        modifierList = config.getStringList("mana.modifiers", null);

        this.taskId = MagicSpells.scheduleRepeatingTask(new Runnable() {
            @Override
            public void run() {
                for (ManaPlayer manaPlayer : manaPlayerManager.getManaPlayers()) {
                    if(manaPlayer.getMana() < manaPlayer.getMaxMana()) {
                        Player player = Bukkit.getPlayer(manaPlayer.getUuid());
                        int oldMana = manaPlayer.getMana();
                        manaPlayer.setMana(manaPlayer.getMana() + manaPlayer.getRegenAmount());
                        manaPlayerManager.updateManaPlayer(manaPlayer);
                        ManaChangeEvent event = new ManaChangeEvent(player, oldMana, manaPlayer.getMana(), manaPlayer.getRegenAmount(), ManaChangeReason.REGEN);
                        EventUtil.call(event);
                        showMana(player, showManaOnRegen);
                    }
                }
            }
        }, this.regenInterval, this.regenInterval);
    }


    @Override
    public void initialize() {
        if (modifierList != null && !modifierList.isEmpty()) {
            MagicSpells.debug(2, "Adding mana modifiers: " + modifierList);
            modifiers = new ModifierSet(this.modifierList);
            modifierList = null;
        }
    }

    @Override
    public void createManaBar(Player player) {
        ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

        if (manaPlayer == null) {
            manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
            manaPlayerManager.addManaPlayer(manaPlayer);
        }

        MagicSpells.scheduleDelayedTask(() -> showMana(player), 11);
    }

    @Override
    public boolean updateManaRankIfNecessary(Player player) {
        ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

        if (manaPlayer == null) {
            manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
            manaPlayerManager.addManaPlayer(manaPlayer);
        }

        return false;
    }

    @Override
    public int getMaxMana(Player player) {
        ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

        if (manaPlayer == null) {
            manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
            manaPlayerManager.addManaPlayer(manaPlayer);
        }

        return manaPlayer.getMaxMana();
    }

    @Override
    public void setMaxMana(Player player, int i) {
        ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

        if (manaPlayer == null) {
            manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
            manaPlayerManager.addManaPlayer(manaPlayer);
        }
        manaPlayer.setMaxMana(i);
        manaPlayerManager.updateManaPlayer(manaPlayer);
    }

    @Override
    public int getRegenAmount(Player player) {
        ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

        if (manaPlayer == null) {
            manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
            manaPlayerManager.addManaPlayer(manaPlayer);
        }


        return manaPlayer.getRegenAmount();
    }

    @Override
    public void setRegenAmount(Player player, int i) {
        ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

        if (manaPlayer == null) {
            manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
            manaPlayerManager.addManaPlayer(manaPlayer);
        }


        manaPlayer.setRegenAmount(i);
        manaPlayerManager.updateManaPlayer(manaPlayer);
        this.showMana(player, showManaOnUse);
    }

    @Override
    public int getMana(Player player) {
        ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

        if (manaPlayer == null) {
            manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
            manaPlayerManager.addManaPlayer(manaPlayer);
        }

        return manaPlayer.getMana();
    }

    @Override
    public boolean hasMana(Player player, int i) {
        ManaPlayer manaPlayer = manaPlayerManager.getManaPlayer(player.getUniqueId().toString());

        if (manaPlayer == null) {
            manaPlayer = new ManaPlayer(player.getUniqueId(), defaultMaxMana, this.defaultRegenAmount);
            manaPlayerManager.addManaPlayer(manaPlayer);
        }

        return manaPlayer.getMana() >= i;
    }

    @Override
    public boolean removeMana(Player player, int i, ManaChangeReason manaChangeReason) {
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

        return false;
    }

    @Override
    public boolean addMana(Player player, int i, ManaChangeReason manaChangeReason) {
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

        return false;
    }

    @Override
    public boolean setMana(Player player, int i, ManaChangeReason manaChangeReason) {
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

        return false;
    }

    @Override
    public void showMana(Player player, boolean showInChat) {
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

    private void showManaInChat(Player player, ManaPlayer bar) {
        int segments = (int)(((double)bar.getMana()/(double)bar.getMaxMana()) * this.manaBarSize);
        String text = MagicSpells.getTextColor() + manaBarPrefix + " {" + this.manaBarColorFull;
        int i = 0;
        for (; i < segments; i++) {
            text += "=";
        }
        text += this.manaBarColorEmpty;
        for (; i < this.manaBarSize; i++) {
            text += "=";
        }
        text += MagicSpells.getTextColor() + "} [" + bar.getMana() + '/' + bar.getMaxMana() + ']';
        player.sendMessage(text);
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
        manaPlayerManager.saveAll();
        MagicSpells.cancelTask(this.taskId);
    }

    @Override
    public ModifierSet getModifiers() {
        return modifiers;
    }
}
