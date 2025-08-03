package fr.soraxdubbing.futurespells.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.soraxdubbing.futurespells.logic.ManaPlayer;
import fr.soraxdubbing.futurespells.logic.ManaPlayerRepository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonManaPlayerRepository implements ManaPlayerRepository {
    private final File folder;
    private final Gson gson;
    private final Logger logger;

    public JsonManaPlayerRepository(File folder, Logger logger) {
        this.folder = folder;
        this.logger = logger;

        if (!folder.exists() && !folder.mkdirs()) {
            logger.severe("Could not create folder: " + folder.getAbsolutePath());
        }

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void Write(ManaPlayer manaPlayer) {
        try {
            File file = getFile(manaPlayer.getUuid().toString());
            String json = gson.toJson(manaPlayer);

            if (file.exists()) {
                file.delete();
            }

            try(FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            } catch (Exception e) {
                throw new  RuntimeException(e);
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Could not write ManaPlayer to file", e);
        }

    }

    @Override
    public ManaPlayer Read(String uuid) {
        try {
            File file = getFile(uuid);
            ManaPlayer manaPlayer = null;

            try(FileReader fileReader = new FileReader(file)){
                manaPlayer = gson.fromJson(fileReader, ManaPlayer.class);
            }
            catch (Exception e){
                throw new  RuntimeException(e);
            }
            return manaPlayer;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Could not read ManaPlayer from file", e);
        }

        return null;
    }

    @Override
    public void Delete(String uuid) {
        try {
            File file = getFile(uuid);
            file.delete();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Could not delete ManaPlayer file", e);
        }
    }

    @Override
    public List<ManaPlayer> ReadAll() {
        try {
            List<ManaPlayer> manaPlayers = new ArrayList<>();
            List<String> uuids = ReadAllUUID();

            for(String uuid : uuids) {
                manaPlayers.add(Read(uuid));
            }

            for(ManaPlayer manaPlayer : manaPlayers) {
                System.out.println(manaPlayer.toString());
            }

            return manaPlayers;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Could not read all ManaPlayers from files", e);
        }

        return new ArrayList<>();
    }

    @Override
    public List<String> ReadAllUUID() {
        try {
            List<String> uuids = new ArrayList<>();
            File[] files = folder.listFiles();

            if(files == null) {
                return uuids;
            }

            for(File file : files) {
                uuids.add(file.getName().replace(".json", ""));
            }

            return uuids;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Could not read all UUIDs from files", e);
        }

        return new ArrayList<>();
    }

    @Override
    public boolean Exists(String uuid) {
        try {
            File file = getFile(uuid);
            return file.exists();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Could not check if ManaPlayer file exists", e);
        }
        return false;
    }

    private File getFile(String uuid) {
        return new File(folder, uuid + ".json");
    }
}
