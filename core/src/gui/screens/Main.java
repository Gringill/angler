package gui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by danny_000 on 12/23/2014.
 */
public abstract class Main {
    private static Stage stage;

    public static Stage getStage(int width, int height) {
        return (stage != null) ? stage : defineStage(width, height);
    }

    private static Stage defineStage(int width, int height) {
        stage = new Stage(new FitViewport(width, height));
        Actor testActor = new Actor() {
            private ShapeRenderer renderer = new ShapeRenderer();

            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.end();

                renderer.setProjectionMatrix(batch.getProjectionMatrix());
                renderer.setTransformMatrix(batch.getTransformMatrix());
                renderer.translate(getX(), getY(), 0);

                renderer.begin(ShapeRenderer.ShapeType.Filled);
                renderer.setColor(Color.BLUE);
                renderer.rect(0, 0, getWidth(), getHeight());
                renderer.end();

                batch.begin();
            }
        };
        testActor.setColor(Color.RED);
        testActor.setBounds(0, 0, 250, 250);
        testActor.setVisible(true);
        testActor.setTouchable(Touchable.enabled);
        testActor.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("down");
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("up");
            }
        });
        stage.addActor(testActor);
        return stage;
    }
}
