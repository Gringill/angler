package controller;

import com.badlogic.gdx.math.Rectangle;
import model.Entity;
import model.GameObject;
import model.Level;
import model.Tile;

//import gui.panel.EntityEditor;
//import gui.panel.InputLevelCreation;

public class Editor {
    private static final String ACTION_SAVE = "SAVE";
    private static final String ACTION_NEW_LEVEL = "NEW_LEVEL";
    private static final String ACTION_OPEN = "OPEN";
    private static final String ACTION_SAVE_AS = "SAVE_AS";
    private static final String ACTION_ENTITY_EDITOR = "ENTITY_EDITOR";
    private static final String ACTION_ENTITY_PAINTER = "ENTITY_PAINTER";
    private static final String ACTION_LOGGER_SHOW = "LOGGER_SHOW";
    private static final String FRAME_NAME = "Sheep Tag Editor";

    public Editor() {
        // TODO Set Frame name
        defineKeyBindings();

        // Build Interface
        defineNorth();
        defineSouth();

//        defineEditorMenuBar();
    }

    private void defineNorth() {

    }

    private void defineSouth() {
//        JPanel pan_south = new JPanel();
//        ((FlowLayout) pan_south.getLayout()).setAlignment(FlowLayout.LEFT);
//        lbl_subtext.setHorizontalAlignment(SwingConstants.CENTER);
//        pan_south.add(lbl_subtext);
//        getContentPane().add(pan_south, BorderLayout.SOUTH);
    }







    private void defineKeyBindings() {
//        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
//            @Override
//            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
//                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
//                    switch (keyEvent.getKeyCode()) {
//                        case KeyEvent.VK_SPACE:
//                            btn_toggleSelection.doClick();
//                            if (isPaintMode()) {
//                                game.getLevel().addEntity(entityCursor);
//                                setCursor(getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));
//                                game.setDrawBuildingPathing(true);
//                            } else {
//                                game.getLevel().removeEntity(entityCursor);
//                                setCursor(Cursor.getDefaultCursor());
//                                game.setDrawBuildingPathing(false);
//                            }
//                            break;
//                    }
//                }
//                return false;
//            }
//        });
    }

//    public void connectToGame(final Game game, Canvas canvas) {
//        (new Thread() {
//            public void run() {
//                while (game.isLoading()) {
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                inputHandler = new EditorInputHandler(Editor.this);
//                inputHandler.transferInput();
//                Gdx.input.setInputProcessor(inputHandler);
//            }
//        }).start();
//        this.game = game;
//        this.canvas = canvas;
//        game.setEditor(this);
//        canvas.setSize(1920, 1080);
//        getContentPane().add(canvas, BorderLayout.CENTER);
//        JPanel panel = new JPanel();
//        panel.add(game.getMinimap());
//        getContentPane().add(panel, BorderLayout.WEST);
//        setVisible(true);
//        revalidate();
//        requestFocus();
//    }

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



    /**
     * Shows the file chooser dialog for opening.
     */
    public void openLevelFromFile() {
//        final JFileChooser fc = new JFileChooser();
//        fc.setFileFilter(new FileFilter() {
//            @Override
//            public String getDescription() {
//                return "Sheep Tag Level";
//            }
//
//            @Override
//            public boolean accept(File f) {
//                String extension = Util.getExtension(f);
//                if (extension != null) {
//                    if (extension.equals(Level.LEVEL_FILE_TYPE)) {
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//                return false;
//            }
//        });
//        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        int returnVal = fc.showOpenDialog(this);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            openLevel(Level.deserializeLevel(this, fc.getSelectedFile()));
//        }
    }

//    public void openEntityEditor() {
//        if (obj_editor != null) {
//            obj_editor.setVisible(true);
//        }
//    }
//
//    public void openEntityPainter() {
//        if (obj_editor != null) {
//            obj_editor.openEntityPainter();
//        }
//    }

}