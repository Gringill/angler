package gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by danny_000 on 12/23/2014.
 */
public class Splash extends Stage {
    private StageManager stageManager;
    private Table rootTable;

    public Splash(int width, int height, StageManager stageManager) {
        super(new FitViewport(width, height));
        this.stageManager = stageManager;

        TextButton play = new TextButton("Play", stageManager.getSkin());
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageManager.setStage(StageManager.STAGE_GAMEPLAY);
            }
        });
        TextButton worldEditor = new TextButton("World Editor", stageManager.getSkin());
        worldEditor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageManager.setStage(StageManager.STAGE_EDITOR);
            }
        });

        rootTable = new Table();
        rootTable.add(play).padBottom(25);
        rootTable.row();
        rootTable.add(worldEditor);
        rootTable.setFillParent(true);
        addActor(rootTable);
    }
}