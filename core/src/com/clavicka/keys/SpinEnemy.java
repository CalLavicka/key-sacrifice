package com.clavicka.keys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class SpinEnemy extends Enemy {

	public SpinEnemy(Vector2 position) {
		super(position, 15, 3, 1000);
		speed = 4;
		poly = CROSS;
		
		movementDir = new Vector2(position).sub(KeyGame.game.gameWidth / 2.0f, KeyGame.game.gameHeight / 2.0f)
				.scl(-1).nor();
		color = Color.CORAL;
	}
	
	@Override
	public void update() {
		position.mulAdd(movementDir, speed);
		if(position.x < radius && movementDir.x < 0) {
			movementDir.x = -movementDir.x;
		}
		if(position.x >= KeyGame.game.gameWidth - radius && movementDir.x > 0) {
			movementDir.x = -movementDir.x;
		}
		if(position.y < radius && movementDir.y < 0) {
			movementDir.y = -movementDir.y;
		}
		if(position.y >= KeyGame.game.gameHeight - radius && movementDir.y > 0) {
			movementDir.y = -movementDir.y;
		}
		
		rotation += 10;
	}

}
