package com.spoffordstudios.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Main extends ApplicationAdapter {
	public static final String ATLAS_NAME = "atlas0";
	private SpriteBatch batch;
	private TextureAtlas atlas = new TextureAtlas("assets/atlas0.atlas");
	private ArrayList<Entity> entities = new ArrayList<Entity>();

	@Override
	public void create() {
		batch = new SpriteBatch();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for (Entity e : entities) {
			e.update();
			e.draw();
		}
		batch.end();
	}
}
