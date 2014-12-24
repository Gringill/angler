package gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by danny_000 on 12/23/2014.
 */
public class Main extends Stage {
    private Table table;
    private ShapeRenderer shapeRenderer;

    public Main(int width, int height) {
        super(new FitViewport(width, height));
        TextureAtlas atlas = new TextureAtlas("uiskin.atlas");
        Skin skin = new Skin(Gdx.files.internal("core/assets/uiskin.json"), atlas);
        skin.addRegions(atlas);

        Label nameLabel = new Label("Name:", skin);
        TextField nameText = new TextField("", skin);
        Label addressLabel = new Label("Address:", skin);
        TextField addressText = new TextField("", skin);

        table = new Table();
        table.add(nameLabel);
        table.add(nameText).width(100);
        table.row();
        table.add(addressLabel);
        table.add(addressText).width(100);
        table.setFillParent(true);
        addActor(table);
        shapeRenderer = new ShapeRenderer();
    }
}
