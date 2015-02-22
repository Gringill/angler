package controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.esotericsoftware.minlog.Log;
import model.Entity;
import model.Level;
import view.stages.Gameplay;
import view.stages.StageManager;
import model.util.Util;
import model.Vector2;

import java.util.ArrayList;

/**
 * The core class of the Sheep Tag project. Contains only the game logic and
 * pertinent information as to the running instance of the game. All network
 * code is abstracted out of this class into the {@link controller.Net Net} class.
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

    public Game(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void create() {
        stageManager = new StageManager(width, height, this);

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
        stageManager.draw();
    }

    @Override
    public void resize(int width, int height) {
        if(stageManager != null) {
            this.width = width;
            this.height = height;
            stageManager.resize(width, height);
        }
    }

    public void setLevel(Level level) {
//        this.level = level;
//        getUtil().setRectSprite(new Sprite(level.getAtlas().findRegion("white_pixel")));

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

//    public Vector2 getStretch() {
//        return new Vector2(width / Gdx.graphics.getWidth(), height / Gdx.graphics.getHeight());
//        return new Vector2(width / editor.getCanvas().getWidth(), height / editor.getCanvas().getHeight());
//    }


    public void dispose() {
        stageManager.dispose();
    }

    public StageManager getStageManager() {
        return stageManager;
    }

}