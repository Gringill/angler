package gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.FitViewport;
import data.GameObject;
import data.Level;
import data.Tile;

/**
 * Created by danny_000 on 12/23/2014.
 */
public class GamePlay extends Stage {
    protected StageManager stageManager;
    private Table rootTable;
    private Level level;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer sr;

    public GamePlay(int width, int height, StageManager stageManager) {
        super(new FitViewport(width, height));
        this.stageManager = stageManager;
        level = Level.getDefaultLevel(stageManager.getGame());
        camera = new OrthographicCamera(width, height);
        batch = new SpriteBatch(1000);
        sr = new ShapeRenderer();
        rootTable = new Table();
        rootTable.setFillParent(true);
        addActor(rootTable);
    }

    @Override
    public void draw() {
        // Render Game
        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(camera.position.x - Gdx.graphics.getWidth() / 2f, camera.position.y - Gdx.graphics.getHeight() / 2f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
        if (level != null) {
            sr.setProjectionMatrix(camera.combined);
            batch.begin();
            for (Tile[] ta : level.getTileMap().getTiles()) {
                for (Tile t : ta) {
                    t.update();
                    t.draw(batch);
                }
            }

//            if (editor.getSelectedTile() != null) {
//                editor.getSelectedTile().drawSelection(batch);
//            }

            for (GameObject e : level.getEntities()) {
                e.update();
                e.draw(batch);
            }

//            if (drawGrid) {
//                for (int y = 0; y < Gdx.graphics.getHeight() / 50; y++) {
//                    getUtil().drawLine(batch, 1, (y * 50), Gdx.graphics.getWidth(), (y * 50), 1);
//                }
//                for (int x = 0; x < Gdx.graphics.getWidth() / 50; x++) {
//                    getUtil().drawLine(batch, (x * 50), 1, (x * 50), Gdx.graphics.getHeight(), 1);
//                }
//            }

            batch.flush();
            batch.end();


            sr.begin(ShapeRenderer.ShapeType.Line);

            for (GameObject e : level.getEntities()) {
                if (e.isSelected()) {
                    sr.circle(e.getX(), e.getY(), e.getSize() / 2f);
                }
            }

//            if (inputHandler.getDragBeginPoint() != null) {
//                Vector2 mouseLocalCoords = new Vector2(Gdx.input.getX(), Gdx.input.getY());
//                Vector2 mouseWorldCoords = getUtil().getMouseWorldCoords(mouseLocalCoords, true);
//                mouseWorldCoords.x += (camera.position.x - camera.viewportWidth / 2);
//                mouseWorldCoords.y += (camera.position.y - camera.viewportHeight / 2);
//                Vector2 begin = inputHandler.getDragBeginPoint();
//                sr.rect(begin.x, begin.y, mouseWorldCoords.x - begin.x, mouseWorldCoords.y - begin.y);
//            }

//            debugRenderer.render(world, camera.combined.cpy().scale(util.getTileSize(), util.getTileSize(), 1));
//            world.step(1 / 60f, 6, 2);
            sr.flush();
            sr.end();
        }

        ScissorStack.popScissors();

        //

        super.draw();
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public OrthographicCamera getCamera() {
        return camera;
    }
}