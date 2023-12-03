package fr.soraxdubbing.futurespells.logic;

import fr.soraxdubbing.futurespells.FutureSpells;

import java.util.List;
import java.util.logging.Logger;

public class ManaPlayerManager {
    private final List<ManaPlayer> manaPlayers;
    private final ManaPlayerDao manaPlayerDao;
    private Logger logger;

    public ManaPlayerManager(ManaPlayerDao manaPlayerDao) {
        this.manaPlayerDao = manaPlayerDao;
        this.manaPlayers = manaPlayerDao.ReadAll();
        logger = FutureSpells.getInstance().getLogger();
    }

    public ManaPlayer getManaPlayer(String uuid) {
        for (ManaPlayer manaPlayer : manaPlayers) {
            if (manaPlayer.getUuid().toString().equals(uuid)) {
                return manaPlayer;
            }
        }
        logger.info("ManaPlayer not found");
        return null;
    }

    public void addManaPlayer(ManaPlayer manaPlayer) {
        if (this.manaPlayers.stream().anyMatch(manaPlayer1 -> manaPlayer1.getUuid().equals(manaPlayer.getUuid()))) {
            return;
        }
        manaPlayers.add(manaPlayer);
        manaPlayerDao.Write(manaPlayer);
    }

    public void removeManaPlayer(ManaPlayer manaPlayer) {
        if (this.manaPlayers.stream().noneMatch(manaPlayer1 -> manaPlayer1.getUuid().equals(manaPlayer.getUuid()))) {
            return;
        }
        manaPlayers.remove(manaPlayer);
        manaPlayerDao.Delete(manaPlayer.getUuid().toString());
    }

    public void updateManaPlayer(ManaPlayer manaPlayer) {
        if (this.manaPlayers.stream().noneMatch(manaPlayer1 -> manaPlayer1.getUuid().equals(manaPlayer.getUuid()))) {
            logger.info("ManaPlayer not found");
            return;
        }
        manaPlayerDao.Write(manaPlayer);
    }

    public List<ManaPlayer> getManaPlayers() {
        return manaPlayers;
    }

    public ManaPlayerDao getManaPlayerDao() {
        return manaPlayerDao;
    }

    public void saveAll() {
        for (ManaPlayer manaPlayer : manaPlayers) {
            manaPlayerDao.Write(manaPlayer);
        }
    }

    public void deleteAll() {
        for (ManaPlayer manaPlayer : manaPlayers) {
            manaPlayerDao.Delete(manaPlayer.getUuid().toString());
        }
    }

    public void loadAll() {
        manaPlayers.clear();
        manaPlayers.addAll(manaPlayerDao.ReadAll());
    }

    public void updateAll() {
        for (ManaPlayer manaPlayer : manaPlayers) {
            manaPlayerDao.Write(manaPlayer);
        }
    }
}
