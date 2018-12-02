package com.clavicka.keys;

import com.badlogic.gdx.math.Vector2;

public class Entity {
	public Vector2 position;
	public float radius;
	public Entity(Vector2 position, float radius) {
		this.position = position;
		this.radius = radius;
	}
	
	public boolean collidesWith(Entity other) {
		return Vector2.dst2(position.x, position.y, other.position.x, other.position.y) <=
				(other.radius + radius) * (other.radius + radius);
	}
}
