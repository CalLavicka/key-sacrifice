package com.clavicka.keys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class WandererEnemy extends Enemy {

	public WandererEnemy(Vector2 position) {
		super(position, 20, 8, 500);
		// TODO Auto-generated constructor stub
		speed = 1.5f;
		poly = SQUARE;
		color = Color.MAROON;
	}
	
	@Override
	public void update() {
		position.mulAdd(movementDir, speed);
		
		Vector2[] valid_dirs = new Vector2[] {
				new Vector2(0,1), new Vector2(1,0), new Vector2(-1,0), new Vector2(0,-1)
		};
		
		if(KeyGame.game.gen.nextInt(100) == 0) {
			int i = KeyGame.game.gen.nextInt(4);
			while(true) {
				if(position.x > 100 && valid_dirs[i].x < 0) break;
				if(position.x < KeyGame.game.gameWidth - 100 && valid_dirs[i].x > 0) break;
				if(position.y > 100 && valid_dirs[i].y < 0) break;
				if(position.y < KeyGame.game.gameHeight - 100 && valid_dirs[i].y > 0) break;
				
				i = (i + 1) % 4;
			}
			movementDir.set(valid_dirs[i]);
		} else if (position.x <= radius) {
			movementDir.set(1,0);
		} else if (position.x >= KeyGame.game.gameWidth - radius) {
			movementDir.set(-1,0);
		} else if (position.y <= radius) {
			movementDir.set(0,1);
		} else if (position.y >= KeyGame.game.gameHeight - radius) {
			movementDir.set(0,-1);
		}
	}

}
