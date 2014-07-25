package com.spoffordstudios.editor;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.minlog.Log;
import com.spoffordstudios.game.Entity;
import com.spoffordstudios.game.Game;

public class Editor extends JFrame {
	public static Editor EDITOR;
	private DefaultListModel<Entity> mod_entity = new DefaultListModel<Entity>();
	private JPanel content;
	private JMenuBar menubar;
	private Level level;
	private int width, height;
	private EntityEditor obj_editor;
	private EntityPainter obj_painter;
	private QuickEntityEditor quick_obj_editor;
	private JPanel panCenter = new JPanel();
	private JToggleButton btn_toggleSelection = new JToggleButton("");
	private JToggleButton btn_toggleInput = new JToggleButton("");
	private JLabel lbl_subtext = new JLabel(" ");
	private Vector2 dragBeginPoint;
	private Canvas canvas;
	private boolean containsMouse;
	private ArrayList<Entity> selectedEntities = new ArrayList<>();
	private Entity draggedEntity;
	private Entity entityCursor;
	private boolean changingRotation;

	public Editor(int width, int height) {
		super(Game.APP_NAME);
		defineKeyBindings();
		this.width = width;
		this.height = height;
		EDITOR = this;
		obj_editor = new EntityEditor();
		obj_painter = new EntityPainter();
		quick_obj_editor = new QuickEntityEditor(null);
		content = (JPanel) getContentPane();
		Log.setLogger(new Log.Logger());
		Log.DEBUG();
		getContentPane().setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		getContentPane().add(panel, BorderLayout.SOUTH);

		lbl_subtext.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl_subtext);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		BufferedImage img = null;

		try {
			img = ImageIO.read(new File("bin/unpacked/circle_empty.png"));
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		btn_toggleSelection.setIcon(new ImageIcon(img.getScaledInstance(25, 25, BufferedImage.SCALE_SMOOTH)));
		try {
			img = ImageIO.read(new File("bin/unpacked/circle_filled.png"));
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		btn_toggleSelection.setSelectedIcon(new ImageIcon(img.getScaledInstance(25, 25, BufferedImage.SCALE_SMOOTH)));
		btn_toggleSelection.setToolTipText("Toggle selection mode between paint and select");
		btn_toggleSelection.setFocusable(false);
		panel_1.add(btn_toggleSelection);

		try {
			img = ImageIO.read(new File("bin/unpacked/gear.png"));
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		btn_toggleInput.setIcon(new ImageIcon(img.getScaledInstance(25, 25, BufferedImage.SCALE_SMOOTH)));
		try {
			img = ImageIO.read(new File("bin/unpacked/soccer.png"));
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		btn_toggleInput.setSelectedIcon(new ImageIcon(img.getScaledInstance(25, 25, BufferedImage.SCALE_SMOOTH)));
		btn_toggleInput.setToolTipText("Toggle mode between game and editor");
		btn_toggleInput.setFocusable(false);
		panel_1.add(btn_toggleInput);

		getContentPane().add(panCenter, BorderLayout.CENTER);
		panCenter.setLayout(new BorderLayout());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menubar = Util.defineEditorMenuBar();
		setJMenuBar(menubar);
		setVisible(true);
	}

	private void defineKeyBindings() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_SPACE:
						btn_toggleSelection.doClick();
						if (getSelectionMode()) {
							Game.getLevel().addEntity(entityCursor);
							setCursor(getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));
						} else {
							Game.getLevel().removeEntity(entityCursor);
							setCursor(Cursor.getDefaultCursor());
						}
						break;
					case KeyEvent.VK_ENTER:
						if (selectedEntities.size() == 1) {
							if (quick_obj_editor.isOpen() == false) {
								quick_obj_editor.execute(selectedEntities.get(0));
							}
						}
						break;
					}
				}
				return false;
			}
		});
	}

	public void setGameCanvas(Canvas canvas) {
		this.canvas = canvas;
		canvas.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				containsMouse = true;
				Vector2 worldMouse = Util.getMouseWorldCoords(e.getPoint());
				if (entityCursor.isBuilding()) {
					entityCursor.setPosition(Util.getSnappedWorldPoint(worldMouse, 25, (int) entityCursor.getWidth(), (int) entityCursor.getHeight()));
				} else {

					entityCursor.setPosition(worldMouse);
				}
			}

			@Override
			public void mouseDragged(MouseEvent mouseEvent) {
				containsMouse = true;
				if (selectedEntities.size() >= 1 && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					// Force selected entities to face dragged mouse position
					for (Entity e : selectedEntities) {
						e.setFacing(Util.facingTowardsPoint(e.getPosition(), Util.getMouseWorldCoords(mouseEvent.getPoint())));
					}
				} else {
					Vector2 worldMouse = Util.getMouseWorldCoords(mouseEvent.getPoint());
					if (draggedEntity != null) {
						if (draggedEntity.isBuilding()) {
							draggedEntity.setPosition(Util.getSnappedWorldPoint(worldMouse, 25, (int) entityCursor.getWidth(), (int) entityCursor.getHeight()));
						} else {
							draggedEntity.setPosition(worldMouse);
						}
					}
				}
			}
		});
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (!changingRotation) {
					if (draggedEntity == null) {
						Vector2 end = Util.getMouseWorldCoords(e.getPoint());
						Rectangle r = Util.getPleasantRectangle(dragBeginPoint, end);
						select(r);
						dragBeginPoint = null;
					} else {
						draggedEntity = null;
					}
				} else {
					changingRotation = false;
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					float shortestDist = 500;
					float dist;
					Entity ent = null;

					for (Entity e2 : Game.getLevel().getEntities()) {
						dist = Util.distPoints(e2.getPosition(), Util.getMouseWorldCoords(e.getPoint()));
						if (dist < e2.getSize() && dist < shortestDist) {
							shortestDist = dist;
							ent = e2;
						}
					}
					if (ent != null) {
						ent.setSelected(true);
						draggedEntity = ent;
					} else {
						dragBeginPoint = Util.getMouseWorldCoords(e.getPoint());
					}
				} else {
					changingRotation = true;
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				containsMouse = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				containsMouse = true;
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (draggedEntity == null) {
					if (getSelectionMode()) { // Paint entity
						Game.getLevel().addEntity(entityCursor.clone());
					} else { // Single left click entity select
						float shortestDist = 9999999999f;
						float dist;
						Entity ent = null;

						for (Entity e2 : Game.getLevel().getEntities()) {
							dist = Util.distPoints(e2.getPosition(), Util.getMouseWorldCoords(e.getPoint()));
							if (dist < e2.getSize() && dist < shortestDist) {
								shortestDist = dist;
								ent = e2;
							}
						}
						if (ent != null) {
							ent.setSelected(true);
							selectedEntities.clear();
							selectedEntities.add(ent);
							setSubText("Selected unit of type [" + ent.getName() + "]");
						}
					}
				}

			}
		});
		canvas.setSize(width, height);
		panCenter.add(canvas, BorderLayout.CENTER);
		EDITOR.pack();
		EDITOR.revalidate();
	}

	public void select(Rectangle r) {
		selectedEntities.clear();
		for (Entity e : Game.getLevel().getEntities()) {
			if (r.contains(e.getPosition())) {
				e.setSelected(true);
				selectedEntities.add(e);
			} else {
				e.setSelected(false);
			}
		}
		setSubText(selectedEntities.size() + " units selected");
	}

	public static void openLevel(Level level) {
		if (EDITOR.level != null) {
			EDITOR.level.getEntities().remove(EDITOR.entityCursor);
			EDITOR.entityCursor = null;
		}
		EDITOR.level = level;
		Game.GAME.loadLevel(level);
		EDITOR.setEntityCursor(new Entity("", 0, 0));
	}

	public Level getLevel() {
		return level;
	}

	public static boolean enabled() {
		return (EDITOR != null);
	}

	public DefaultListModel<Entity> getSharedEntityModel() {
		return mod_entity;
	}

	public EntityEditor getObjectEditor() {
		return obj_editor;
	}

	public EntityPainter getObjectPainter() {
		return obj_painter;
	}

	public void setEntityCursor(Entity e) {
		if (entityCursor == null) {
			entityCursor = e;
		} else {
			try {
				throw new Exception("Cannot change entity cursor after it has been set.");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public ArrayList<Entity> getSelectedEntities() {
		return selectedEntities;
	}

	public void setSelectedEntity(ArrayList<Entity> e) {
		selectedEntities = e;
	}

	public Entity getEntityCursor() {
		return entityCursor;
	}

	public boolean getSelectionMode() {
		return (btn_toggleSelection.isSelected());
	}

	public void setSubText(String newText) {
		lbl_subtext.setText(newText);
	}

	public Vector2 getDragBeginPoint() {
		return dragBeginPoint;
	}

	public Entity getDraggedEntity() {
		return draggedEntity;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public boolean containsMouse() {
		return containsMouse;
	}
}