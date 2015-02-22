package view.widgets;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.Level;
import view.panel.FileChooser;
import view.stages.Editor;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Daniel on 1/3/15.
 */
public class MenuBar extends Table {
    private Stage parentStage;
    private HashMap<String, DropdownMenu> ddmenus = new HashMap();
    private Skin skin;

    public MenuBar(Stage parentStage, Skin skin, String... menus) {
        this.parentStage = parentStage;
        this.skin = skin;
        setHeight(25);
        for (String menu : menus) {
            TextButton textButton = new TextButton(menu, skin);
            textButton.setName(menu);
            textButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float X, float Y) {
                            if (event.getTarget().getParent() instanceof TextButton) {
                                TextButton button = (TextButton) event.getTarget().getParent();
                                Actor menubar = button.getParent();
                                int x = (int) (button.getX());
                                int y = (int) (menubar.getY());
                                DropdownMenu tempMenu = ddmenus.get(button.getText().toString());
                                ddmenus.values().forEach(m -> m.hide());
                                if (tempMenu != null) {
                                    tempMenu.show(x, y);
                                }
                            }
                        }
                    });
            add(textButton);
        }
    }

    public void defineMenu(String parentMenu, String... submenus) {
        DropdownMenu ddm = new DropdownMenu(this, skin, submenus);
        ddm.setName(parentMenu);
        getStage().addActor(ddm);
        ddmenus.put(parentMenu, ddm);
    }

    public void event(String eid) {
        switch (eid.toUpperCase()) {
            case "FILE-OPEN":
                File someValue = FileChooser.instantiate();
                if(someValue != null) {
                    if(parentStage instanceof Editor) {
                        Editor editor = (Editor) parentStage;
                        editor.openLevel(Level.deserializeLevel(someValue));
                    }
                }

                break;
            case "FILE-SAVE":

                break;
        }
    }

}
