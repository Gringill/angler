package view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Daniel on 1/3/15.
 */
public class DropdownMenu extends Table {
    private MenuBar parentMenu;
    private boolean active = true;
    private RunnableAction event;

    public DropdownMenu(MenuBar parent, Skin skin, String... menus) {
        parentMenu = parent;
        setSize(getPrefWidth(), getPrefHeight());
        for (String menu : menus) {
            TextButton textButton = new TextButton(menu, skin);
            textButton.setName(menu);
            textButton.getLabel().setAlignment(Align.left);
            textButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float X, float Y) {
                            parentMenu.event(DropdownMenu.this.getName() + "-" + textButton.getName());
                        }
                    });
            add(textButton).expandX().fillX().left().row();
        }
        setSize(getPrefWidth(), getPrefHeight());
        this.addListener(
                new InputListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        active = true;

                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        active = false;
                    }
                }
        );
        setVisible(false);
    }

    public void show(int x, int y) {
        active = true;
        setVisible(true);
        setPosition(x, (y - getHeight()));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!active) {
            setVisible(false);
        }
    }

    public void hide() {
        setVisible(false);
        active = false;
    }

    public Actor getNestedMenu(int index, String[] path) {
        if(index < path.length - 1) {
            Actor tempActor = findActor(path[index]);
            return (((DropdownMenu) tempActor).getNestedMenu(index + 1, path));
        } else {
            return findActor(path[index]);
        }
    }

    public void setEvent(RunnableAction event) {
        this.event = event;
    }
}
