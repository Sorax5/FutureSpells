package fr.soraxdubbing.futurespells.logic;

import java.util.List;

/**
 * This interface is used to define the methods that will be used to interact with the database.
 */
public interface ManaPlayerRepository {
    void Write(ManaPlayer manaPlayer);
    ManaPlayer Read(String uuid);
    void Delete(String uuid);
    List<ManaPlayer> ReadAll();
    List<String> ReadAllUUID();
    boolean Exists(String uuid);
}
