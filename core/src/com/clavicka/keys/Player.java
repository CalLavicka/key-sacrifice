package com.clavicka.keys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {
	
	public static final int MAX_HEALTH = 10;
	
	public Vector2 movementDir = new Vector2();
	public Vector2 fireDir = new Vector2();
	public float speed = 2;
	public int fireCooldown = 0;
	
	public int health = MAX_HEALTH;
	public int invulnTime;
	
	public Player(Vector2 position) {
		super(position, 10);
		// TODO Auto-generated constructor stub
	}
	
	public void update() {
		position.mulAdd(movementDir, speed);
		
		if(position.x < radius) position.x = radius;
		if(position.x >= KeyGame.game.gameWidth - radius) {
			position.x = KeyGame.game.gameWidth - radius - 1;
		}
		if(position.y < radius) position.y = radius;
		if(position.y >= KeyGame.game.gameHeight - radius) {
			position.y = KeyGame.game.gameHeight - radius - 1;
		}
		
		fireCooldown = Math.max(0, fireCooldown-1);
		
		if (invulnTime > 0) invulnTime--;
	}
	
	public void render(ShapeRenderer renderer) {
		boolean rendering = false;
		if(invulnTime > 48) {
			if(invulnTime % 16 < 8) {
				rendering = true;
			}
		}else {
			if(invulnTime % 10 < 5) {
				rendering = true;
			}
		}
		
		if(rendering) {
			renderer.setColor(Color.GREEN);
			renderer.circle(position.x, position.y, radius);
		}
	}

}
