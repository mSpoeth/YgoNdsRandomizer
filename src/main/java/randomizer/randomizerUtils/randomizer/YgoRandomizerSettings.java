package randomizer.randomizerUtils.randomizer;

import org.json.JSONException;
import org.json.JSONObject;
import randomizer.randomizerUtils.FileTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


/**
 * The randomizer settings.
 */
public class YgoRandomizerSettings {

    /**
     * The enum for all pack randomization options.
     */
    public enum PackRandomization {
        NoChange,
        RandomizeWithRarity,
        RandomizeCompletely
    }

    /**
     * The enum for all structure deck randomization options.
     */
    public enum StructureDeckRandomization {
        NoChange,
        Randomize
    }

    private String seed = "";

    private int packRandomization = 0;
    private int structureDeckRandomization = 0;

    /**
     * Default constructor, but sets the seed to current time in ms
     */
    public YgoRandomizerSettings() {
        setSeed("");
    }

    /**
     * Load settings from file.
     *
     * @param settingsFile the settings file
     * @return the settings
     * @throws IOException the io exception
     */
    public static YgoRandomizerSettings loadFromFile(File settingsFile) throws IOException {
        byte[] bytes = Files.readAllBytes(settingsFile.toPath());
        String json = FileTools.getBytesToString(bytes);

        return getFromJSON(json);
    }

    /**
     * Save to file.
     *
     * @param settingsFile the file to save to
     * @throws IOException the io exception
     */
    public void saveToFile(File settingsFile) throws IOException {
        String json = toJSON();

        Files.write(settingsFile.toPath(), json.getBytes());
    }

    /**
     * Get settings from json.
     *
     * @param jsonString the json string
     * @return The settings read from the json
     */
    public static YgoRandomizerSettings getFromJSON(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            YgoRandomizerSettings settings = new YgoRandomizerSettings();

            settings.setSeed(json.getString("seed"));
            settings.setPackRandomization(json.getInt("packRandomization"));
            settings.setStructureDeckRandomization(json.getInt("structureDeckRandomization"));

            return settings;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * To json string.
     *
     * @return the string
     */
    public String toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("seed", seed);
            json.put("packRandomization", packRandomization);
            json.put("structureDeckRandomization", structureDeckRandomization);

            return json.toString(4);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sets seed.
     *
     * @param seed the seed
     */
    public void setSeed(String seed) {
        if (seed.isEmpty()) seed = String.valueOf(System.currentTimeMillis());
        this.seed = seed;
    }

    /**
     * Gets seed.
     *
     * @return the seed
     */
    public String getSeed() {
        return seed;
    }

    /**
     * Gets seed as long.
     *
     * @return the seed as a long
     */
    public long getSeedAsLong() {
        return FileTools.getStringToSeed(seed);
    }

    /**
     * Sets pack randomization.
     *
     * @param packRandomization the pack randomization
     */
    public void setPackRandomization(int packRandomization) {
        this.packRandomization = packRandomization;
    }

    /**
     * Sets pack randomization.
     *
     * @param packRandomization the pack randomization
     */
    public void setPackRandomization(PackRandomization packRandomization) {
        this.packRandomization = packRandomization.ordinal();
    }

    /**
     * Sets structure deck randomization.
     *
     * @param structureDeckRandomization the structure deck randomization
     */
    public void setStructureDeckRandomization(int structureDeckRandomization) {
        this.structureDeckRandomization = structureDeckRandomization;
    }

    /**
     * Sets structure deck randomization.
     *
     * @param structureDeckRandomization the structure deck randomization
     */
    public void setStructureDeckRandomization(StructureDeckRandomization structureDeckRandomization) {
        this.structureDeckRandomization = structureDeckRandomization.ordinal();
    }

    /**
     * Gets pack setting.
     *
     * @return the pack setting
     */
    public PackRandomization getPackSetting() {
        return PackRandomization.values()[packRandomization];
    }

    /**
     * Gets structure deck setting.
     *
     * @return the structure deck setting
     */
    public StructureDeckRandomization getStructureDeckSetting() {
        return StructureDeckRandomization.values()[structureDeckRandomization];
    }
}
