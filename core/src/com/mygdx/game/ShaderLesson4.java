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

public class ShaderLesson4 extends ApplicationAdapter {
	final String VERT = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE
			+ ";\n" + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
			+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +

			"uniform mat4 u_projTrans;\n" + " \n" + "varying vec4 vColor;\n"
			+ "varying vec2 vTexCoord;\n" +

			"void main() {\n" + "	vColor = " + ShaderProgram.COLOR_ATTRIBUTE
			+ ";\n" + "	vTexCoord = " + ShaderProgram.TEXCOORD_ATTRIBUTE
			+ "0;\n" + "	gl_Position =  u_projTrans * "
			+ ShaderProgram.POSITION_ATTRIBUTE + ";\n" + "}";

	final String FRAG =
	// GL ES specific stuff
	"#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n"
			+ //
			"varying LOWP vec4 vColor;\n"
			+ "varying vec2 vTexCoord;\n"
			+ "uniform sampler2D u_texture;\n"
			+ "uniform sampler2D u_texture1;\n"
			+ "uniform sampler2D u_mask;\n"
			+ "void main(void) {\n"
			+ "	//sample the colour from the first texture\n"
			+ "	vec4 texColor0 = texture2D(u_texture, vTexCoord);\n"
			+ "\n"
			+ "	//sample the colour from the second texture\n"
			+ "	vec4 texColor1 = texture2D(u_texture1, vTexCoord);\n"
			+ "\n"
			+ "	//get the mask; we will only use the alpha channel\n"
			+ "	float mask = texture2D(u_mask, vTexCoord).a;\n"
			+ "\n"
			+ "	//interpolate the colours based on the mask\n"
			+ "	gl_FragColor = vColor * mix(texColor0, texColor1, mask);\n"
			+ "}";

	Texture tex0, tex1, mask;
	SpriteBatch batch;
	OrthographicCamera cam;
	ShaderProgram shader;

	@Override
	public void create() {
		tex0 = new Texture(Gdx.files.internal("data/grass.png"));
		tex1 = new Texture(Gdx.files.internal("data/dirt.png"));
		mask = new Texture(Gdx.files.internal("data/mask.png"));

		// important since we aren't using some uniforms and attributes that
		// SpriteBatch expects
		ShaderProgram.pedantic = false;

		// print it out for clarity
		System.out.println("Vertex Shader:\n-------------\n\n" + VERT);
		System.out.println("\n");
		System.out.println("Fragment Shader:\n-------------\n\n" + FRAG);

		shader = new ShaderProgram(VERT, FRAG);
		if (!shader.isCompiled()) {
			System.err.println(shader.getLog());
			System.exit(0);
		}
		if (shader.getLog().length() != 0)
			System.out.println(shader.getLog());

		shader.begin();
		shader.setUniformi("u_texture1", 1);
		shader.setUniformi("u_mask", 2);
		shader.end();

		// bind mask to glActiveTexture(GL_TEXTURE2)
		mask.bind(2);

		// bind dirt to glActiveTexture(GL_TEXTURE1)
		tex1.bind(1);

		// now we need to reset glActiveTexture to zero!!!! since sprite batch
		// does not do this for us
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

		// tex0 will be bound when we call SpriteBatch.draw

		batch = new SpriteBatch(1000, shader);
		batch.setShader(shader);

		cam = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		cam.setToOrtho(false);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		batch.draw(tex0, 0, 0);

		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		batch.setProjectionMatrix(cam.combined);
	}
}
