package engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.esotericsoftware.minlog.Log;
import data.Entity;
import data.Level;
import gui.screens.GamePlay;
import gui.screens.StageManager;
import util.Util;
import util.Vector2;

import java.util.ArrayList;

/**
 * The core class of the Sheep Tag project. Contains only the game logic and
 * pertinent information as to the running instance of the game. All network
 * code is abstracted out of this class into the {@link engine.Net Net} class.
 * <p>
 * Responsible for creation of the initial LibGdx functionality, game state
 * creation, as well as maintaining the logic & render loop. In addition is the
 * first step in the level loading process.
 *
 * @author Daniel 'Gringill' Spofford
 */
public class Game extends ApplicationAdapter implements ContactListener {
    public static final String APP_VERSION = "0.0.1";
    public static final String APP_NAME = "Sheep Tag v" + APP_VERSION;
    public static final String ATLAS_NAME = "atlas0";
    public static final String COMMAND_MOVE = "COMMAND_MOVE";
    private StageManager stageManager;
    private int width, height;
    private ArrayList<Entity> selectedEntities = new ArrayList<>();
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private boolean drawBuildingPathing;
    private boolean drawGrid = true;
    private Util util = new Util(this);
    private GameInputHandler inputHandler;
    private Minimap minimap;
    private Editor editor;


    public Game(int width, int height) {
        this.width = width;
        this.height = height;
        minimap = new Minimap(this);
    }

    @Override
    public void create() {
        stageManager = new StageManager(width, height, this);
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(this);
        debugRenderer = new Box2DDebugRenderer();

        // TODO Handle input via stages

//        if (editor != null) {
//            editor.getCanvas().addComponentListener(new ComponentListener() {
//                @Override
//                public void componentResized(ComponentEvent e) {
//                    camera.setToOrtho(false, e.getComponent().getWidth(), e.getComponent().getHeight());
//                }
//
//                @Override
//                public void componentMoved(ComponentEvent e) {
//
//                }
//
//                @Override
//                public void componentShown(ComponentEvent e) {
//
//                }
//
//                @Override
//                public void componentHidden(ComponentEvent e) {
//
//                }
//            });
//        }
//        TODO Implement default level

//        Net.setupGameClient();


    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        processInput();

        stageManager.draw();
    }

    @Override
    public void resize(int width, int height) {
        if(stageManager != null) {
            stageManager.resize(width, height);
        }
    }

    public void setLevel(Level level) {
//        this.level = level;
//        getUtil().setRectSprite(new Sprite(level.getAtlas().findRegion("white_pixel")));
//        for (GameObject e : getLevel().getEntities()) {
//            getMinimap().registerEntity(e);
//        }
    }

    public Minimap getMinimap() {
        return minimap;
    }

    private void processInput() {
//        if (getLevel() != null) {
//            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
//                camera.zoom += 0.02;
//            }
//            if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
//                camera.zoom -= 0.02;
//            }
//            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//                if (camera.position.x > camera.viewportWidth / 2)
//                    camera.translate(-12, 0, 0);
//            }
//            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//                if (camera.position.x < (getLevel().getTileMap().getSize().width * 50) - (camera.viewportWidth / 2))
//                    camera.translate(12, 0, 0);
//            }
//            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//                if (camera.position.y > camera.viewportHeight / 2)
//                    camera.translate(0, -10, 0);
//            }
//            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
//                if (camera.position.y < (getLevel().getTileMap().getSize().height * 50) - (camera.viewportHeight / 2))
//                    camera.translate(0, 10, 0);
//            }
//            System.out.println(getLevel().getTileMap());
//            if (camera.position.y > (getLevel().getTileMap().getSize().height * 50) - (camera.viewportHeight / 2))
//                camera.position.set(camera.position.x, (getLevel().getTileMap().getSize().height * 50) - (camera.viewportHeight / 2), 0);
//            if (camera.position.y < camera.viewportHeight / 2)
//                camera.position.set(camera.position.x, camera.viewportHeight / 2, 0);
//            if (camera.position.x > (getLevel().getTileMap().getSize().width * 50) - (camera.viewportWidth / 2))
//                camera.position.set((getLevel().getTileMap().getSize().width * 50) - (camera.viewportWidth / 2), camera.position.y, 0);
//            if (camera.position.x < camera.viewportWidth / 2)
//                camera.position.set(camera.viewportWidth / 2, camera.position.y, 0);
//        }
    }

    public void select(Rectangle r) {
//        selectedEntities.clear();
//        for (Entity e : getLevel().getEntities()) {
//            if (r.contains(e.getPosition())) {
//                e.setSelected(true);
//                selectedEntities.add(e);
//            } else {
//                e.setSelected(false);
//            }
//        }
    }

    /**
     * Works with vector parameter directly
     *
     * @param entities
     * @param command
     * @param goal
     */
    public void issuePointCommand(ArrayList<Entity> entities, String command, Vector2 goal) {
        for (Entity e : entities) {
            e.issuePointCommand(command, goal);
        }
    }

    public World getWorld() {
        return world;
    }

    public ArrayList<Entity> getSelectedEntities() {
        return selectedEntities;
    }

    public boolean doDrawBuildingPathing() {
        return drawBuildingPathing;
    }

    public void setDrawBuildingPathing(boolean flag) {
        drawBuildingPathing = flag;
    }

    @Override
    public void beginContact(Contact contact) {
        Log.debug("Collision!");
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

//    public float getGameWidth() {
//        return (float) level.getTileMap().getSize().getWidth() * util.getTileSize();
//    }

    public float getGameHeight() {
        return 0;
    }

    public Util getUtil() {
        return util;
    }

    public GameInputHandler getInputHandler() {
        return inputHandler;
    }

//    public Vector2 getStretch() {
//        return new Vector2(width / Gdx.graphics.getWidth(), height / Gdx.graphics.getHeight());
//        return new Vector2(width / editor.getCanvas().getWidth(), height / editor.getCanvas().getHeight());
//    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public void dispose() {
        stageManager.dispose();
    }

    public StageManager getStageManager() {
        return stageManager;
    }

    public Level getLevel() {
        return ((GamePlay) getStageManager().getActiveStage()).getLevel();
    }

    public Camera getCamera() {
        return getStageManager().getActiveStage().getCamera();
    }
}