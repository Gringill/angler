package gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.HashSet;

/**
 * Created by Daniel Spofford on 12/23/2014.
 */
public class StageManager {
    public final static String STAGE_MAIN = "stage_main";
    private Stage activeStage;
    private HashSet<Stage> activeStages = new HashSet();
    private Main main;
    private int width;
    private int height;

    public StageManager(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setStage(String stageName) {
        switch (stageName) {
            case STAGE_MAIN:
                activeStage = (main != null) ? main : (main = new Main(width, height));
                activeStages.add(activeStage);
                break;

            default:
                activeStage = new Stage();
                activeStages.add(activeStage);
                break;
        }
        Gdx.input.setInputProcessor(activeStage);
    }

    public void draw() {
        activeStage.act(Gdx.graphics.getDeltaTime());
        activeStage.draw();
    }

    public void resize(int width, int height) {
        activeStage.getViewport().update(width, height, true);
    }

    public void dispose() {
        activeStages.stream().forEach(s -> s.dispose());
    }
}