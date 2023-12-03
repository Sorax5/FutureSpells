package fr.soraxdubbing.futurespells.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.soraxdubbing.futurespells.logic.ManaPlayer;
import fr.soraxdubbing.futurespells.logic.ManaPlayerDao;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class JsonManaPlayerDao implements ManaPlayerDao {

    private final String path;
    private final Gson gson;

    public JsonManaPlayerDao(String path) {
        this.path = path;

        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }

        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void Write(ManaPlayer manaPlayer) {
        File file = getFile(manaPlayer.getUuid().toString());
        String json = gson.toJson(manaPlayer);

        if (file.exists()) {
            file.delete();
        }

        try(FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ManaPlayer Read(String uuid) {
        File file = getFile(uuid);
        ManaPlayer manaPlayer = null;
        try(FileReader fileReader = new FileReader(file)){
            manaPlayer = gson.fromJson(fileReader, ManaPlayer.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return manaPlayer;
    }

    @Override
    public void Delete(String uuid) {
        File file = getFile(uuid);
        file.delete();
    }

    @Override
    public List<ManaPlayer> ReadAll() {
        List<ManaPlayer> manaPlayers = new ArrayList<>();
        List<String> uuids = ReadAllUUID();

        for(String uuid : uuids) {
            manaPlayers.add(Read(uuid));
        }

        // print all manaPlayers
        for(ManaPlayer manaPlayer : manaPlayers) {
            System.out.println(manaPlayer.toString());
        }

        return manaPlayers;
    }

    @Override
    public List<String> ReadAllUUID() {
        List<String> uuids = new ArrayList<>();

        File folder = new File(path);

        if(!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles();

        if(files == null) {
            return uuids;
        }

        for(File file : files) {
            uuids.add(file.getName().replace(".json", ""));
        }

        return uuids;
    }

    @Override
    public boolean Exists(String uuid) {
        File file = getFile(uuid);
        return file.exists();
    }

    private File getFile(String uuid) {
        return new File(path + File.separator + uuid + ".json");
    }
}
