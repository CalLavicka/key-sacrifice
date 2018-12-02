package com.clavicka.keys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Bullet extends Entity {

	Vector2 velocity;
	int time;
	
	public Bullet(Vector2 position, float radius, Vector2 velocity, int time) {
		super(position, radius);
		
		this.time = time;
		this.velocity = velocity;
	}
	
	public void update() {
		time--;
		this.position.add(velocity);
	}
	
	public void render(ShapeRenderer renderer) {
		renderer.setColor(Color.RED);
		renderer.circle(position.x, position.y, radius);
	}

}
