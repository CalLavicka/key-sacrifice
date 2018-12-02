package com.clavicka.keys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Entity {
	
	static final float[] SHIP = new float[] {
			-1,-1,-1,-0.8f,-0.2f,-0.3f,-0.2f,0.3f,-1,0.8f,-1,1,
			1,0.1f,1,-0.1f
	};
	
	static final float[] BLOB = new float[] {
			-0.3f,-1, -1,-0.6f,-0.3f,-0.2f,-1,0.2f,-0.3f,0.6f,-1,1,
			//1,1,
			1,0.6f,0.3f,0.2f,1,-0.2f,0.3f,-0.6f,1,-1,
			//-1,-1
	};
	
	static final float[] SQUARE = new float[] {
			-1,-0.2f,-1,0.2f,-0.8f,0.2f,-0.8f,0.4f,-0.6f,0.4f,-0.6f,0.6f,
			-0.4f,0.6f,-0.4f,0.8f,-0.2f,0.8f,-0.2f,1,0.2f,1,0.2f,0.8f,
			0.4f,0.8f,0.4f,0.6f,0.6f,0.6f,0.6f,0.4f,0.8f,0.4f,0.8f,0.2f,
			1,0.2f,1,-0.2f,0.8f,-0.2f,0.8f,-0.4f,0.6f,-0.4f,0.6f,-0.6f,
			0.4f,-0.6f,0.4f,-0.8f,0.2f,-0.8f,0.2f,-1,-0.2f,-1,-0.2f,-0.8f,
			-0.4f,-0.8f,-0.4f,-0.6f,-0.6f,-0.6f,-0.6f,-0.4f,-0.8f,-0.4f,
			-0.8f,-0.2f
	};
	
	private static final float cross_factor = 0.2f;
	
	static final float[] CROSS = new float[] {
			-1,0,-cross_factor,cross_factor,
			0,1,cross_factor,cross_factor,
			1,0,cross_factor,-cross_factor,
			0,-1,-cross_factor,-cross_factor
	};
	
	Vector2 movementDir = new Vector2();
	float speed = 1;
	int health;
	int score;
	float[] poly = SHIP;
	Color color = Color.CYAN;
	float rotation = 0;

	public Enemy(Vector2 position, float radius, int health, int score) {
		super(position, radius);
		this.health = health;
		this.score = score;
	}
	
	public void update() {
		
		position.mulAdd(movementDir, speed);
		
		if (!movementDir.isZero()) {
			float dx = KeyGame.game.player.position.x - position.x;
			float dy = KeyGame.game.player.position.y - position.y;

			float dot = -dx * movementDir.y + dy * movementDir.x;
			if (dot < 0.0f)
				movementDir.rotate(-1);
			else
				movementDir.rotate(1);
		}
		
		rotation = MathUtils.radDeg * MathUtils.atan2(movementDir.y, movementDir.x);
	}
	
	public void render(ShapeRenderer renderer) {
		renderer.setColor(color);
		//renderer.circle(position.x, position.y, radius);
		renderer.translate(position.x, position.y, 0);
		renderer.scale(radius, radius, 1);
		renderer.rotate(0, 0, 1, rotation);
		renderer.polygon(poly);
		
		renderer.identity();
	}
	
}
