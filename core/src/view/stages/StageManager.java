package view.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import controller.Game;

import java.util.HashSet;

/**
 * Created by Daniel Spofford on 12/23/2014.
 */
public class StageManager {
    public final static String STAGE_MAIN = "stage_main";
    public final static String STAGE_GAMEPLAY = "stage_gameplay";
    public final static String STAGE_EDITOR = "stage_editor";

    private Stage activeStage;
    private Game game;
    private int height;
    private int width;

    private TextureAtlas gameAtlas = new TextureAtlas("core/assets/atlas0.atlas");
    private TextureAtlas uiAtlas = new TextureAtlas("core/assets/uiskin.atlas");

    private Skin skin = new Skin(Gdx.files.internal("core/assets/uiskin.json"));
    private HashSet<Stage> activeStages = new HashSet();

    private Gameplay stageGamePlay;
    private Editor stageEditor;
    private Splash stageMain;

    public StageManager(int width, int height, Game game) {
        this.game = game;
        this.width = width;
        this.height = height;

        skin.addRegions(uiAtlas);
        setStage(STAGE_MAIN);
    }

    public void setStage(String stageName) {
        switch (stageName) {
            case STAGE_MAIN:
                activeStage = (stageMain != null) ? stageMain : (stageMain = new Splash(width, height, this));
                activeStages.add(activeStage);
                Gdx.input.setInputProcessor(stageMain);
                break;

            case STAGE_GAMEPLAY:
                activeStage = (stageGamePlay != null) ? stageGamePlay : (stageGamePlay = new Gameplay(width, height, this));
                activeStages.add(activeStage);
                Gdx.input.setInputProcessor(stageGamePlay);
                break;

            case STAGE_EDITOR:
                activeStage = (stageEditor != null) ? stageEditor : (stageEditor = new Editor(width, height, this));
                activeStages.add(activeStage);
                Gdx.input.setInputProcessor(stageEditor);
                break;

            default:
                activeStage = new Stage();
                activeStages.add(activeStage);
                Gdx.input.setInputProcessor(activeStage);
                break;
        }
    }

    public void draw() {
        activeStage.act(Gdx.graphics.getDeltaTime());
        activeStage.draw();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        activeStage.getViewport().update(width, height, true);
    }

    public void dispose() {
        activeStages.stream().forEach(s -> s.dispose());
    }

    public Stage getActiveStage() {
        return activeStage;
    }

    public Game getGame() {
        return game;
    }

    public TextureAtlas getGameAtlas() {
        return gameAtlas;
    }

    public Skin getSkin() {
        return skin;
    }
}