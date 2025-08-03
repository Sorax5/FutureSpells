package fr.soraxdubbing.futurespells.logic;

import fr.soraxdubbing.futurespells.FutureSpells;
import jdk.jfr.internal.LogLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ManaPlayerManager {
    private final List<ManaPlayer> manaPlayers;
    private final ManaPlayerRepository manaPlayerDao;
    private Logger logger;

    public ManaPlayerManager(ManaPlayerRepository manaPlayerDao, Logger logger) {
        this.manaPlayerDao = manaPlayerDao;
        this.manaPlayers = new ArrayList<>(manaPlayerDao.ReadAll());
        this.logger = logger;
    }

    public ManaPlayer getManaPlayer(String uuid) {
        try {
            for (ManaPlayer manaPlayer : manaPlayers) {
                if (!manaPlayer.getUuid().toString().equals(uuid)) {
                    continue;
                }

                return manaPlayer;
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while getting ManaPlayer: " + e.getMessage(), e);
        }

        logger.info("ManaPlayer not found");
        return null;
    }

    public void addManaPlayer(ManaPlayer manaPlayer) {
        try {
            if (this.manaPlayers.stream().anyMatch(manaPlayer1 -> manaPlayer1.getUuid().equals(manaPlayer.getUuid()))) {
                return;
            }

            manaPlayers.add(manaPlayer);
            manaPlayerDao.Write(manaPlayer);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while adding ManaPlayer: " + e.getMessage(), e);
        }
    }

    public void removeManaPlayer(ManaPlayer manaPlayer) {
        try {
            if (this.manaPlayers.stream().noneMatch(manaPlayer1 -> manaPlayer1.getUuid().equals(manaPlayer.getUuid()))) {
                return;
            }
            manaPlayers.remove(manaPlayer);
            manaPlayerDao.Delete(manaPlayer.getUuid().toString());
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while removing ManaPlayer: " + e.getMessage(), e);
        }
    }

    public void updateManaPlayer(ManaPlayer manaPlayer) {
        try {
            if (this.manaPlayers.stream().noneMatch(manaPlayer1 -> manaPlayer1.getUuid().equals(manaPlayer.getUuid()))) {
                logger.info("ManaPlayer not found");
                return;
            }
            manaPlayerDao.Write(manaPlayer);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while updating ManaPlayer: " + e.getMessage(), e);
        }
    }

    public List<ManaPlayer> getManaPlayers() {
        return manaPlayers;
    }

    public ManaPlayerRepository getManaPlayerDao() {
        return manaPlayerDao;
    }

    public void saveAll() {
        try {
            for (ManaPlayer manaPlayer : manaPlayers) {
                manaPlayerDao.Write(manaPlayer);
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while saving all ManaPlayers: " + e.getMessage(), e);
        }
    }

    public void deleteAll() {
        try {
            for (ManaPlayer manaPlayer : manaPlayers) {
                manaPlayerDao.Delete(manaPlayer.getUuid().toString());
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while deleting all ManaPlayers: " + e.getMessage(), e);
        }
    }

    public void loadAll() {
        try {
            manaPlayers.clear();
            manaPlayers.addAll(manaPlayerDao.ReadAll());
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while loading all ManaPlayers: " + e.getMessage(), e);
        }
    }

    public void updateAll() {
        try {
            for (ManaPlayer manaPlayer : manaPlayers) {
                manaPlayerDao.Write(manaPlayer);
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while updating all ManaPlayers: " + e.getMessage(), e);
        }
    }
}
