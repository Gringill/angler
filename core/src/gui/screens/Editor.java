package gui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 * Created by danny_000 on 12/23/2014.
 */
public class Editor extends GamePlay {
    private Table rootTable;

    public Editor(int width, int height, StageManager stageManager) {
        super(width, height, stageManager);
        this.stageManager = stageManager;

//        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle(null, null, null, new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("ic_entity")), null, null);

//        ImageButton brushType = new ImageButton(imageButtonStyle);
        ImageButton brushType = new ImageButton(new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("ic_entity")));
        ImageButton brushMode = new ImageButton(new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("circle_empty")));
        ImageButton logicMode = new ImageButton(new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("gear")), new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("soccer")));

        Table toolbar = new Table();

        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pm1.setColor(.2f, .2f, .2f, 1f);
        pm1.fill();
        toolbar.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
        toolbar.pad(10);
        toolbar.add(brushType).width(40).height(40);
        toolbar.add(brushMode).width(40).height(40);
        toolbar.add(logicMode).width(40).height(40);

        rootTable = new Table();
        rootTable.add(toolbar).expandX();
        rootTable.setFillParent(true);
        addActor(rootTable);
    }

    @Override
    public void draw() {

        super.draw();
    }
}