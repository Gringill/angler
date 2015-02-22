package view.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import controller.Game;
import model.*;
import model.util.Logger;
import model.util.Util;
import view.panel.EntityEditor;
import view.panel.InputAttribute;
import view.widgets.MenuBar;

/**
 * Created by danny_000 on 12/23/2014.
 */
public class Editor extends Gameplay {
    private EntityEditor obj_editor;
    private boolean drawGrid = true;
    private Box2DDebugRenderer debugRenderer;
    private Entity entityCursor;
    private Game game;
    private Tile selectedTile;
    private Entity draggedEntity;
    private Vector2 dragBeginPoint;
    public boolean stepFlag;

//    private ImageButton object;
    ImageButton brushType;

    private ImageButton selection;
    private ImageButton input;
    private CheckBox pathing;
    private CheckBox grid;
    private Label subtext;

    public Editor(int width, int height, StageManager stageManager) {
        super(width, height, stageManager);
        debugRenderer = new Box2DDebugRenderer();
        this.stageManager = stageManager;

        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGBA4444);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle("core/assets/fonts/Mecha/Mecha.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 16;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        parameter.genMipMaps = true;

        BitmapFont font16 = generator.generateFont(parameter);
        generator.dispose();

        MenuBar menubar = new MenuBar(this, stageManager.getSkin(), "File", "View", "Menu");
        pm1.setColor(.5f, .5f, .5f, 1f);
        pm1.fill();
        menubar.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
        menubar.align(Align.left);

        ImageButtonStyle ibs = new ImageButtonStyle();
        ibs.up = new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("ic_entity"));
        ibs.checked = new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("ic_terrain"));
        brushType = new ImageButton(ibs);

        ibs = new ImageButtonStyle();
        ibs.up = new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("ic_select"));
        ibs.checked = new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("ic_move"));
        ImageButton brushMode = new ImageButton(ibs);

        ibs = new ImageButtonStyle();
        ibs.up = new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("ic_play"));
        ibs.checked = new TextureRegionDrawable(stageManager.getGameAtlas().findRegion("ic_pause"));
        ImageButton logicMode = new ImageButton(ibs);

        Table toolbar = new Table();
        pm1.setColor(.2f, .2f, .2f, 1f);
        pm1.fill();
        toolbar.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
        toolbar.align(Align.left);
        toolbar.add(brushType).width(50).height(50).padTop(10).padBottom(10).padLeft(10).padRight(10);
        toolbar.add(brushMode).width(50).height(50).padRight(10);
        toolbar.add(logicMode).width(50).height(50);

        rootTable = new Table();
        rootTable.align(Align.top);
        rootTable.add(menubar).fillX().expandX().top().row();
        rootTable.add(toolbar).fillX().expandX().top().row();
        rootTable.setFillParent(true);

        addActor(rootTable);

        menubar.defineMenu("File", "Save", "Open");
        menubar.defineMenu("View", "V-Menu-1", "V-Menu-2");
        menubar.defineMenu("Menu", "M-Menu-1", "M-Menu-2");
    }

    public EntityEditor getObjectEditor() {
        return obj_editor;
    }

    public void initializeObjectEditor() {
        if (obj_editor == null) {
            obj_editor = new EntityEditor(this);
        } else {
            Logger.log("Attempted to initialize the Object Editor after it has already been initialized.", "Editor", false);
        }
    }

    @Override
    public void draw() {
        // Render Game
        if (getLevel() != null) {
            if (camera.position.y > (getLevel().getTileMap().getSize().height * 50) - (camera.viewportHeight / 2))
                camera.position.set(camera.position.x, (getLevel().getTileMap().getSize().height * 50) - (camera.viewportHeight / 2), 0);
            if (camera.position.y < camera.viewportHeight / 2)
                camera.position.set(camera.position.x, camera.viewportHeight / 2, 0);
            if (camera.position.x > (getLevel().getTileMap().getSize().width * 50) - (camera.viewportWidth / 2))
                camera.position.set((getLevel().getTileMap().getSize().width * 50) - (camera.viewportWidth / 2), camera.position.y, 0);
            if (camera.position.x < camera.viewportWidth / 2)
                camera.position.set(camera.viewportWidth / 2, camera.position.y, 0);
        }

        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(camera.position.x - Gdx.graphics.getWidth() / 2f, camera.position.y - Gdx.graphics.getHeight() / 2f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
        batch.setProjectionMatrix(camera.combined);
        if (getLevel() != null) {
            sr.setProjectionMatrix(camera.combined);
            batch.begin();
            for (Tile[] ta : getLevel().getTileMap().getTiles()) {
                for (Tile t : ta) {
                    t.update();
                    t.draw(batch);
                }
            }
        }

        if (selectedTile != null) {
            selectedTile.drawSelection(batch);
        }

        for (GameObject e : getLevel().getEntities()) {
            e.update();
            e.draw(batch);
        }

        if (drawGrid) {
            Util.drawLine(batch, 1, (5 * 50), Gdx.graphics.getWidth(), (5 * 50), 1);
            for (int y = 0; y < Gdx.graphics.getHeight() / 50; y++) {
                Util.drawLine(batch, 1, (y * 50), Gdx.graphics.getWidth(), (y * 50), 1);
            }
            for (int x = 0; x < Gdx.graphics.getWidth() / 50; x++) {
                Util.drawLine(batch, (x * 50), 1, (x * 50), Gdx.graphics.getHeight(), 1);
            }
        }

        batch.flush();
        batch.end();


        sr.begin(ShapeRenderer.ShapeType.Line);

        for (GameObject e : getLevel().getEntities()) {
            if (e.isSelected()) {
                sr.circle(e.getX(), e.getY(), e.getSize() / 2f);
            }
        }

        if (dragBeginPoint != null) {
            Vector2 mouseLocalCoords = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector2 mouseWorldCoords = Util.getMouseWorldCoords(this, mouseLocalCoords, true);
            mouseWorldCoords.x += (camera.position.x - camera.viewportWidth / 2);
            mouseWorldCoords.y += (camera.position.y - camera.viewportHeight / 2);
            Vector2 begin = dragBeginPoint;
            sr.rect(begin.x, begin.y, mouseWorldCoords.x - begin.x, mouseWorldCoords.y - begin.y);
        }

        debugRenderer.render(getLevel().getWorld(), camera.combined.cpy().scale(Util.getTileSize(), Util.getTileSize(), 1));
        getLevel().getWorld().step(1 / 60f, 6, 2);

        sr.flush();
        sr.end();
        ScissorStack.popScissors();
    }

    public Level openLevel(Level level) {
        game.setLevel(level);

        level.getTileMap().connectToGame(this);
        initializeObjectEditor();
        for (Entity e : level.getEntities()) {
            e.setSprite(new Sprite(level.getAtlas().findRegion(Util.pullRegionFromTexture(e.getTexture()))));
            e.setGame(game);
            e.initializePathFinder();
        }
        for (Tile[] ta : level.getTileMap().getTiles()) {
            for (Tile t : ta) {
                t.setSprite(new Sprite(level.getAtlas().findRegion(Util.pullRegionFromTexture(t.getTexture()))));
                t.setGame(game);
            }
        }
        if (level != null) {
            level.getEntities().remove(entityCursor);
            entityCursor = null;
        }
        entityCursor = new Entity(game, "", 0, 0);
        getMinimap().updateSize();
        return level;
    }

    public Game getGame() {
        return game;
    }

    public Entity getEntityCursor() {
        return entityCursor;
    }

    public GameObject getDraggedEntity() {
        return draggedEntity;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public void setEntityCursor(Entity e) {
        entityCursor = e;
    }



    public void setDraggedEntity(Entity e) {
        draggedEntity = e;
    }

    public Vector2 getDragBeginPoint() {
        return dragBeginPoint;
    }

    public void setDragBeginPoint(Vector2 dragBeginPoint) {
        this.dragBeginPoint = dragBeginPoint;
    }

    public void setSelectedTile(Tile selectedTile) {
        this.selectedTile = selectedTile;
    }

    public void select(Rectangle r) {
        game.select(r);
        setSubText(getSelectedEntities().size() + " units selected");
    }

    public void setSubText(String newText) {
        subtext.setText(newText);
    }


    public boolean doDrawPathing() {
        return pathing.isChecked();
    }

    public boolean isPaintMode() {
        return selection.isChecked();
    }

    public boolean isSelectMode() {
        return !pathing.isChecked();
    }

    public boolean isObjectMode() {
        return brushType.isChecked();
    }

    public boolean doEdit() {
        return !input.isChecked();
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ENTER:
                if (getSelectedEntities().size() > 0) {
                    InputAttribute.showDialog(this, this.getSelectedEntities().get(0));
                } else {
                    if (this.getSelectedTile() != null) {
                        InputAttribute.showDialog(this, this.getSelectedTile());
                    }
                }
                break;
            case Input.Keys.NUM_1:
                System.out.println("Num1");
                getCamera().position.set(480, 270, 0);
                break;
            case Input.Keys.NUM_2:
                getCamera().position.set(480, 810, 0);
                break;
            case Input.Keys.DEL:
                for (GameObject e : this.getSelectedEntities()) {
                    this.getLevel().getEntities().remove(e);
                    this.getMinimap().unregisterEntity(e);
                }
                break;
            case Input.Keys.B:
                for (Entity e : this.getSelectedEntities()) {
                    e.generatePhysicsBody(this.getLevel());
                }
                break;
            case Input.Keys.S:
                this.stepFlag = true;
                break;
            case Input.Keys.A:
                camera.zoom += 0.02;
                break;
            case Input.Keys.Q:
                camera.zoom -= 0.02;
                break;
            case Input.Keys.LEFT:
                if (camera.position.x > camera.viewportWidth / 2)
                    camera.translate(-12, 0, 0);
                break;
            case Input.Keys.RIGHT:
                if (camera.position.x < (getLevel().getTileMap().getSize().width * 50) - (camera.viewportWidth / 2))
                    camera.translate(12, 0, 0);
                break;
            case Input.Keys.UP:
                if (camera.position.y < (getLevel().getTileMap().getSize().height * 50) - (camera.viewportHeight / 2))
                    camera.translate(0, 10, 0);
                break;
            case Input.Keys.DOWN:
                if (camera.position.y > camera.viewportHeight / 2)
                    camera.translate(0, -10, 0);
                break;
        }
        return false;
    }

    //    public JMenuBar defineEditorMenuBar() {
//        JMenuBar menuBar;
//        JMenu menu;
//        JMenuItem menuItem;
//        ActionListener action_listener;
//
//        action_listener = (e -> {
//            switch (e.getActionCommand()) {
//                case ACTION_NEW_LEVEL:
//                    new InputLevelCreation(Editor.this);
//                    break;
//                case ACTION_SAVE:
//                    game.getLevel().save(Editor.this, null);
//                    break;
//                case ACTION_OPEN:
//                    openLevelFromFile();
//                    break;
//                case ACTION_SAVE_AS:
//                    game.getLevel().saveAs(Editor.this);
//                    break;
//                case ACTION_ENTITY_EDITOR:
//                    openEntityEditor();
//                    break;
//                case ACTION_ENTITY_PAINTER:
//                    openEntityPainter();
//                    break;
//                case ACTION_LOGGER_SHOW:
//                    Logger.showWindow();
//                    break;
//                default:
//                    break;
//            }
//        });
//
//        menuBar = new JMenuBar();
//        menu = new JMenu("File");
//        menu.setMnemonic(KeyEvent.VK_F);
//
//        menuItem = new JMenuItem("New level..", KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand(ACTION_NEW_LEVEL);
//        menuItem.addActionListener(action_listener);
//        menu.add(menuItem);
//
//        menuItem = new JMenuItem("Save..", KeyEvent.VK_S);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand(ACTION_SAVE);
//        menuItem.addActionListener(action_listener);
//        menu.add(menuItem);
//
//        menuItem = new JMenuItem("Save as..", KeyEvent.VK_S);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
//        menuItem.setActionCommand(ACTION_SAVE_AS);
//        menuItem.addActionListener(action_listener);
//        menu.add(menuItem);
//
//        menuItem = new JMenuItem("Open", KeyEvent.VK_O);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand(ACTION_OPEN);
//        menuItem.addActionListener(action_listener);
//        menu.add(menuItem);
//
//        menuBar.add(menu);
//
//        menu = new JMenu("Window");
//        menu.setMnemonic(KeyEvent.VK_E);
//
//        menuItem = new JMenuItem("Open Object Editor", KeyEvent.VK_O);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
//        menuItem.setActionCommand(ACTION_ENTITY_EDITOR);
//        menuItem.addActionListener(action_listener);
//        menu.add(menuItem);
//
//        menuItem = new JMenuItem("Open Object Painter", KeyEvent.VK_P);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
//        menuItem.setActionCommand(ACTION_ENTITY_PAINTER);
//        menuItem.addActionListener(action_listener);
//        menu.add(menuItem);
//
//        menuItem = new JMenuItem("Open Logger", KeyEvent.VK_L);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
//        menuItem.setActionCommand(ACTION_LOGGER_SHOW);
//        menuItem.addActionListener(action_listener);
//        menu.add(menuItem);
//
//        menuBar.add(menu);
//        return menuBar;
//    }

    private void processInput() {

    }
}