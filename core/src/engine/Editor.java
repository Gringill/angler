package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import data.Entity;
import data.GameObject;
import data.Level;
import data.Tile;
import gui.panel.EntityEditor;
import gui.panel.InputLevelCreation;
import util.Logger;
import util.Util;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class Editor extends JFrame {
    private static final String ACTION_SAVE = "SAVE";
    private static final String ACTION_NEW_LEVEL = "NEW_LEVEL";
    private static final String ACTION_OPEN = "OPEN";
    private static final String ACTION_SAVE_AS = "SAVE_AS";
    private static final String ACTION_ENTITY_EDITOR = "ENTITY_EDITOR";
    private static final String ACTION_ENTITY_PAINTER = "ENTITY_PAINTER";
    private static final String ACTION_LOGGER_SHOW = "LOGGER_SHOW";
    private static final String FRAME_NAME = "Sheep Tag Editor";

    public boolean stepFlag = true;
    private Game game;
    private Tile selectedTile;
    private Entity draggedEntity;
    private Entity entityCursor;
    private EntityEditor obj_editor;

    private Canvas canvas;
    private JToggleButton btn_toggleObject = new JToggleButton("");
    private JToggleButton btn_toggleSelection = new JToggleButton("");
    private JToggleButton btn_toggleInput = new JToggleButton("");
    private JCheckBox cb_pathing = new JCheckBox("Draw Pathing");
    private JCheckBox cb_grid = new JCheckBox("Draw Grid");

    private JLabel lbl_subtext = new JLabel(" ");
    private EditorInputHandler inputHandler;

    public Editor() {
        super(Editor.FRAME_NAME);
        defineKeyBindings();
        getContentPane().setLayout(new BorderLayout());

        // Build Interface
        defineNorth();
        defineSouth();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(defineEditorMenuBar());
    }

    private void defineNorth() {
        JPanel pan_north = new JPanel();
        pan_north.setLayout(new BoxLayout(pan_north, BoxLayout.X_AXIS));

        btn_toggleObject.setIcon(new ImageIcon(Util.readBufferedImage("core/assets/unpacked/ic_entity.png").getScaledInstance(25, 25, BufferedImage.SCALE_SMOOTH)));
        btn_toggleObject.setSelectedIcon(new ImageIcon(Util.readBufferedImage("core/assets/unpacked/ic_tile.png").getScaledInstance(25, 25, BufferedImage.SCALE_SMOOTH)));
        btn_toggleObject.setToolTipText("Toggle object mode between entity and tile");
        btn_toggleObject.setFocusable(false);
        pan_north.add(btn_toggleObject);

        btn_toggleSelection.setIcon(new ImageIcon(Util.readBufferedImage("core/assets/unpacked/circle_empty.png").getScaledInstance(25, 25, BufferedImage.SCALE_SMOOTH)));
        btn_toggleSelection.setSelectedIcon(new ImageIcon(Util.readBufferedImage("core/assets/unpacked/circle_filled.png").getScaledInstance(25, 25, BufferedImage.SCALE_SMOOTH)));
        btn_toggleSelection.setToolTipText("Toggle selection mode between paint and select");
        btn_toggleSelection.setFocusable(false);
        pan_north.add(btn_toggleSelection);

        btn_toggleInput.setIcon(new ImageIcon(Util.readBufferedImage("core/assets/unpacked/gear.png").getScaledInstance(25, 25, BufferedImage.SCALE_SMOOTH)));
        btn_toggleInput.setSelectedIcon(new ImageIcon(Util.readBufferedImage("core/assets/unpacked/soccer.png").getScaledInstance(25, 25, BufferedImage.SCALE_SMOOTH)));
        btn_toggleInput.setToolTipText("Toggle mode between game and editor");
        btn_toggleInput.setFocusable(false);
        pan_north.add(btn_toggleInput);

        pan_north.add(cb_pathing);
        pan_north.add(cb_grid);
        getContentPane().add(pan_north, BorderLayout.NORTH);
    }

    private void defineSouth() {
        JPanel pan_south = new JPanel();
        ((FlowLayout) pan_south.getLayout()).setAlignment(FlowLayout.LEFT);
        lbl_subtext.setHorizontalAlignment(SwingConstants.CENTER);
        pan_south.add(lbl_subtext);
        getContentPane().add(pan_south, BorderLayout.SOUTH);
    }

    public EntityEditor getObjectEditor() {
        return obj_editor;
    }

    public Entity getEntityCursor() {
        return entityCursor;
    }

    public void setEntityCursor(Entity e) {
        entityCursor = e;
    }

    public GameObject getDraggedEntity() {
        return draggedEntity;
    }

    public void setDraggedEntity(Entity e) {
        draggedEntity = e;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public void setSelectedTile(Tile selectedTile) {
        this.selectedTile = selectedTile;
    }

    private void defineKeyBindings() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    switch (keyEvent.getKeyCode()) {
                        case KeyEvent.VK_SPACE:
                            btn_toggleSelection.doClick();
                            if (isPaintMode()) {
                                game.getLevel().addEntity(entityCursor);
                                setCursor(getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));
                                game.setDrawBuildingPathing(true);
                            } else {
                                game.getLevel().removeEntity(entityCursor);
                                setCursor(Cursor.getDefaultCursor());
                                game.setDrawBuildingPathing(false);
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void connectToGame(final Game game, Canvas canvas) {
        (new Thread() {
            public void run() {
                while (game.isLoading()) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                inputHandler = new EditorInputHandler(Editor.this);
                inputHandler.transferInput();
                Gdx.input.setInputProcessor(inputHandler);
            }
        }).start();
        this.game = game;
        this.canvas = canvas;
        game.setEditor(this);
        canvas.setSize(1920, 1080);
        getContentPane().add(canvas, BorderLayout.CENTER);
        setVisible(true);
        revalidate();
        requestFocus();
    }

    public JMenuBar defineEditorMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        ActionListener action_listener;

        action_listener = (e -> {
            switch (e.getActionCommand()) {
                case ACTION_NEW_LEVEL:
                    new InputLevelCreation(Editor.this);
                    break;
                case ACTION_SAVE:
                    game.getLevel().save(Editor.this, null);
                    break;
                case ACTION_OPEN:
                    openLevelFromFile();
                    break;
                case ACTION_SAVE_AS:
                    game.getLevel().saveAs(Editor.this);
                    break;
                case ACTION_ENTITY_EDITOR:
                    openEntityEditor();
                    break;
                case ACTION_ENTITY_PAINTER:
                    openEntityPainter();
                    break;
                case ACTION_LOGGER_SHOW:
                    Logger.showWindow();
                    break;
                default:
                    break;
            }
        });

        menuBar = new JMenuBar();
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);

        menuItem = new JMenuItem("New level..", KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand(ACTION_NEW_LEVEL);
        menuItem.addActionListener(action_listener);
        menu.add(menuItem);

        menuItem = new JMenuItem("Save..", KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        menuItem.setActionCommand(ACTION_SAVE);
        menuItem.addActionListener(action_listener);
        menu.add(menuItem);

        menuItem = new JMenuItem("Save as..", KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        menuItem.setActionCommand(ACTION_SAVE_AS);
        menuItem.addActionListener(action_listener);
        menu.add(menuItem);

        menuItem = new JMenuItem("Open", KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.setActionCommand(ACTION_OPEN);
        menuItem.addActionListener(action_listener);
        menu.add(menuItem);

        menuBar.add(menu);

        menu = new JMenu("Window");
        menu.setMnemonic(KeyEvent.VK_E);

        menuItem = new JMenuItem("Open Object Editor", KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        menuItem.setActionCommand(ACTION_ENTITY_EDITOR);
        menuItem.addActionListener(action_listener);
        menu.add(menuItem);

        menuItem = new JMenuItem("Open Object Painter", KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        menuItem.setActionCommand(ACTION_ENTITY_PAINTER);
        menuItem.addActionListener(action_listener);
        menu.add(menuItem);

        menuItem = new JMenuItem("Open Logger", KeyEvent.VK_L);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        menuItem.setActionCommand(ACTION_LOGGER_SHOW);
        menuItem.addActionListener(action_listener);
        menu.add(menuItem);

        menuBar.add(menu);
        return menuBar;
    }

    public Level openLevel(Level level) {
        if (level != null) {
            level.getEntities().remove(getEntityCursor());
            setEntityCursor(null);
        }
        setEntityCursor(new Entity(getGame(), "", 0, 0));
        getGame().getMinimap().updateSize();

        return level;
    }

    /**
     * Shows the file chooser dialog for opening.
     */
    public void openLevelFromFile() {
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Sheep Tag Level";
            }

            @Override
            public boolean accept(File f) {
                String extension = Util.getExtension(f);
                if (extension != null) {
                    if (extension.equals(Level.LEVEL_FILE_TYPE)) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            openLevel(Level.deserializeLevel(this, fc.getSelectedFile()));
        }
    }

    public void openEntityEditor() {
        if (obj_editor != null) {
            obj_editor.setVisible(true);
        }
    }

    public void openEntityPainter() {
        if (obj_editor != null) {
            obj_editor.openEntityPainter();
        }
    }

    public void setSubText(String newText) {
        lbl_subtext.setText(newText);
    }

    public boolean doDrawPathing() {
        return cb_pathing.isSelected();
    }

    public boolean isPaintMode() {
        return (btn_toggleSelection.isSelected());
    }

    public boolean isSelectMode() {
        return (!btn_toggleSelection.isSelected());
    }

    public boolean isObjectMode() {
        return (!btn_toggleObject.isSelected());
    }

    public boolean doEdit() {
        return btn_toggleInput.isSelected() == false;
    }

    public void select(Rectangle r) {
        game.select(r);
        setSubText(game.getSelectedEntities().size() + " units selected");
    }

    public Game getGame() {
        return game;
    }

    public void initializeObjectEditor() {
        if (obj_editor == null) {
            obj_editor = new EntityEditor(this);
        } else {
            Logger.log("Attempted to initialize the Object Editor after it has already been initialized.", "Editor", false);
        }

    }

}