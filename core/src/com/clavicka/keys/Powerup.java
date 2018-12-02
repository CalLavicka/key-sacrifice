package com.clavicka.keys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Powerup extends Entity {
	
	
	static final float[] LIFE_HEART = {
			-0.7f,0.8f,0,0.5f,0.7f,0.8f,
			0.9f,0.5f,0,-0.9f,-0.9f,0.5f
	};
	
	static final float[] BOMB = {
			-0.9f, 0.1f, 0, 0.4f,
			0.9f, 0.1f, 0.9f, -0.1f,
			0, -0.4f, -0.9f, -0.1f
	};
	
	float[] poly;

	public Powerup(Vector2 position, float radius, float[] poly) {
		super(position, radius);
		this.poly = poly;
	}
	
	
	public void render(ShapeRenderer renderer) {
		renderer.setColor(Color.GREEN);
		//renderer.circle(position.x, position.y, radius);
		renderer.translate(position.x, position.y, 0);
		renderer.scale(radius, radius, 1);
		//renderer.rotate(0, 0, 1, rotation);
		renderer.polygon(poly);
		
		renderer.identity();
	}

}
