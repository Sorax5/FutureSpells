package fr.soraxdubbing.futurespells.logic;

import java.io.Serializable;
import java.util.UUID;

/**
 * Stock the mana of a player
 */
public class ManaPlayer {
    private final UUID uuid;
    private int mana;
    private int maxMana;
    private int regenAmount;

    /**
     * Constructor
     * @param uuid The UUID of the player
     */
    public ManaPlayer(UUID uuid, int maxMana, int regenAmount) {
        this.uuid = uuid;
        this.mana = maxMana;
        this.maxMana = maxMana;
        this.regenAmount = regenAmount;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public int getRegenAmount() {
        return regenAmount;
    }

    public void setRegenAmount(int regenAmount) {
        this.regenAmount = regenAmount;
    }

    @Override
    public String toString() {
        return "ManaPlayer{" +
                "uuid=" + uuid +
                ", mana=" + mana +
                ", maxMana=" + maxMana +
                ", regenAmount=" + regenAmount +
                '}';
    }
}
