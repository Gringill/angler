package view.stages;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import controller.TileMap;

/**
 * Created by danny_000 on 12/23/2014.
 */
public class Splash extends Stage {
    private StageManager stageManager;
    private Table rootTable;

    public Splash(int width, int height, StageManager stageManager) {
        super(new FitViewport(width, height));
        this.stageManager = stageManager;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle("core/assets/fonts/Mecha/Mecha.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 24;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        parameter.genMipMaps = true;

        BitmapFont font24 = generator.generateFont(parameter);
        generator.dispose();

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.up = stageManager.getSkin().getDrawable("default-round-large");
        tbs.fontColor = Color.WHITE;
        tbs.down = stageManager.getSkin().getDrawable("default-round-down");
        tbs.downFontColor = Color.YELLOW;
        tbs.overFontColor = Color.YELLOW;
        tbs.font = font24;

        TextButton play = new TextButton("Play", tbs);
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageManager.setStage(StageManager.STAGE_GAMEPLAY);
            }
        });

        TextButton worldEditor = new TextButton("World Editor", tbs);
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