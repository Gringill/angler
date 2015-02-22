package model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import view.stages.Editor;
import controller.Game;
import controller.TileMap;
import model.util.Util;
import view.stages.Gameplay;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Level implements Json.Serializable {
    public static final String LEVEL_FILE_TYPE = "stlvl";
    public TextureAtlas atlas = new TextureAtlas("core/assets/atlas0.atlas");
    private DefaultListModel<GameObject> mod_entity = new DefaultListModel<>();
    private DefaultListModel<GameObject> mod_tile = new DefaultListModel<>();
    private World world;

    /**
     * If the level has been saved before this will contain that location.
     */
    private File lastSave;

    private TileMap tilemap;
    /**
     * A working list of entities to be updated and drawn with every cycle.
     */
    private ArrayList<Entity> entities = new ArrayList<>();

    /**
     * Required for JSON. Should not be used otherwise unless you know what you
     * are doing.
     */
    public Level() {

    }

    private Level(Game game, TileMap tilemap, Gameplay gameplay) {
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(game);

        if(tilemap == null) {
            this.tilemap = TileMap.createDefaultTileMap(gameplay, this);
        } else {
            this.tilemap = tilemap;
        }
    }

    /**
     * @param name   of the level
     * @param width  in grid units
     * @param height in grid units
     * @return the created level
     */
    public static Level createLevel(Editor editor, String name, Integer width, Integer height) {
        Level level = new Level(editor.getGame(), null, editor);
        editor.getGame().setLevel(level);
        level.getTileMap().connectToGame(editor);
        defineDefaultData();
        return level;
    }

    /**
     * Convenience method that creates a new level with a predefined default set
     * of attributes.
     */
    public static Level getDefaultLevel(Game game, Gameplay gameplay) {
        Level level = new Level(game, null, gameplay);
        return level;
    }

    private static void defineDefaultData() {
//        editor.initializeObjectEditor();
//        editor.getObjectEditor().newTile("Grass", null, NodeMap.PATH_GROUND, new Color(0, 255, 0));
//        editor.getObjectEditor().newTile("Water", "water", NodeMap.PATH_SWIMMING, new Color(0, 0, 255));
    }

    /**
     * Parses a Sheep Tag level file from the standard Json format.
     *
     * @param selectedFile expected to be in Json format.
     * @return the target file as a built level object
     */
    public static Level deserializeLevel(File selectedFile) {
        FileHandle file = new FileHandle(selectedFile.getAbsolutePath());
        String jsonText = file.readString();
        JsonValue jsonData = new JsonReader().parse(jsonText);
        Level level = new Json().readValue(Level.class, jsonData);
        return level;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Dimension getSize() {
        return tilemap.getSize();
    }

    public GameObject addEntity(Entity entity) {
        entities.add(entity);
        return entity;
    }

    public void removeEntity(GameObject e) {
        entities.remove(e);
    }

    /**
     * Show the file chooser dialog for saving.
     */
    public void saveAs(JFrame parent) {
        final JFileChooser fc = new JFileChooser();
        File file;
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Sheep Tag Level";
            }

            @Override
            public boolean accept(File f) {
                String extension = Util.getExtension(f);
                if (extension != null) {
                    if (extension.equals(Level.LEVEL_FILE_TYPE)) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showSaveDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            if (file.toString().endsWith("." + LEVEL_FILE_TYPE) == false) {
                file = new File(file.toString() + "." + LEVEL_FILE_TYPE);
            }
            save(parent, file);
        }
    }

    /**
     * If the <code>target</code> parameter is null and the {@link #lastSave}
     * field is not null it will save. Otherwise it will default to the
     * {@link #saveAs(JFrame parent)} method.
     *
     * @param saveAsOverrideTarget when populated causes the {@link #saveAs(JFrame parent)} method to be
     *                             called instead
     */
    public void save(JFrame parent, File saveAsOverrideTarget) {
        if (saveAsOverrideTarget != null) {
            Json json = new Json();
            String s = json.prettyPrint(this);
            (new FileHandle(saveAsOverrideTarget)).writeString(s, false);
            lastSave = saveAsOverrideTarget;
        } else {
            if (lastSave != null) {
                Json json = new Json();
                String s = json.toJson(this);
                (new FileHandle(lastSave)).writeString(s, false);
            } else {
                saveAs(parent);
            }
        }
    }

    public DefaultListModel<GameObject> getTileModel() {
        return mod_tile;
    }

    public DefaultListModel<GameObject> getEntityModel() {
        return mod_entity;
    }

    @Override
    public void write(Json json) {
        json.writeValue("mod_entity", getEntityModel());

        json.writeValue("mod_tile", getTileModel());

        json.writeValue("entities", entities);

        json.writeValue("tiledef", tilemap.getTiledef());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void read(Json json, JsonValue jsonData) {
        tilemap = new TileMap(json.readValue(String[][].class, jsonData.get("tiledef")));
        mod_entity = json.readValue(DefaultListModel.class, jsonData.get("mod_entity"));
        mod_tile = json.readValue(DefaultListModel.class, jsonData.get("mod_tile"));
        entities = json.readValue(ArrayList.class, GameObject.class, jsonData.get("entities"));
    }
    public World getWorld() {
        return world;
    }
    public TileMap getTileMap() {
        return tilemap;
    }
}
