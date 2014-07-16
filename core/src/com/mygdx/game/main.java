package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.game.entities.Entity;

public class main extends ApplicationAdapter {
	private OrthographicCamera camera;
	private static final int GRID_SIZE = 32;
	private static final int WIDTH = 1920 / 2;
	private static final int HEIGHT = 1080 / 2;
	private SpriteBatch batch;
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private Texture tex0, tex1, mask;
	private ShaderProgram shader;
	private float tick = 0;
	
	@Override
	public void create() {
		tex0 = new Texture("water.png");
		tex1 = new Texture("sand.png");
		mask = new Texture("mask.png");
		
		ShaderProgram.pedantic = false;
		
		shader = new ShaderProgram(Gdx.files.internal("test.vsh"),
				Gdx.files.internal("test.fsh"));
		
//		shader = new ShaderProgram(VERT, FRAG);
		
		if (!shader.isCompiled()) {
			System.err.println("Log: " + shader.getLog());
			System.exit(0);
		}
		
		if (shader.getLog().length() != 0)
			System.out.println(shader.getLog());
		
		shader.begin();
		shader.setUniformi("u_texture1", 1);
		shader.setUniformi("u_mask", 2);
		shader.setUniformf("time", tick);
		shader.end();
		
		mask.bind(2);
		tex1.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		batch = new SpriteBatch(100, shader);
		batch.setShader(shader);
		
		camera = new OrthographicCamera(WIDTH, HEIGHT);
		camera.setToOrtho(false);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		shader.setUniformf("time", tick+=0.01f);
		batch.draw(tex0, 0, 0, 256, 256);
		batch.end();
	}

	public void step() {
		for (Entity e : entities) {

		}
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		batch.setProjectionMatrix(camera.combined);
	}
}
