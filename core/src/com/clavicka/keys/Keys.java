package com.clavicka.keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

public class Keys {
	public List<Key> activeKeys = new ArrayList<>();
	
	public List<Key> deadKeys = new ArrayList<>();
	
	public List<Key> keysLeft = new ArrayList<>();
	
	public Map<Action, Key> inputs = new HashMap<>();
	public Map<String, Key> keyMap = new HashMap<>();
	
	public static final Color PENDING = Color.GRAY;
	public static final Color ACTIVE = Color.WHITE;
	public static final Color DEAD = Color.DARK_GRAY;
	public static final Color PRESSED = Color.ROYAL;
	public static final Color KILLED = Color.FIREBRICK;
	
	public static final String[] KEY_NAMES = new String[]{
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
			"M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
			"Up", "Down", "Left", "Right", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "0"
	};
	
	public Keys() {
		keysLeft.addAll(Arrays.stream(KEY_NAMES)
				.map(s -> new Key(Input.Keys.valueOf(s))).collect(Collectors.toList()));
		for(Key k : keysLeft) {
			keyMap.put(k.keyname, k);
		}
		
		for(Key k : new ArrayList<Key>(keysLeft)) {
			if(k.keyname.equals("W")) {
				activateKey(k, Action.MOVEUP);
			} else if(k.keyname.equals("A")) {
				activateKey(k, Action.MOVELEFT);
			} else if(k.keyname.equals("S")) {
				activateKey(k, Action.MOVEDOWN);
			} else if(k.keyname.equals("D")) {
				activateKey(k, Action.MOVERIGHT);
			} else if(k.keyname.equals("Up")) {
				activateKey(k, Action.SHOOTUP);
			} else if(k.keyname.equals("Down")) {
				activateKey(k, Action.SHOOTDOWN);
			} else if(k.keyname.equals("Left")) {
				activateKey(k, Action.SHOOTLEFT);
			} else if(k.keyname.equals("Right")) {
				activateKey(k, Action.SHOOTRIGHT);
			}
		}
		
		Collections.shuffle(keysLeft);
		Collections.shuffle(activeKeys);
	}
	
	public Set<Action> getActions() {
		Set<Action> ret = new HashSet<>();
		for(Action a : Action.values()) {
			Key k = inputs.get(a);
			if(k != null) {
				if (Gdx.input.isKeyPressed(k.keycode)) {
					if(Gdx.input.isKeyJustPressed(k.keycode)) {
						k.life -= 15;
					} else {
						k.life--;
					}
					ret.add(a);
					k.color = PRESSED;
				} else {
					k.color = ACTIVE;
				}
			} 
		}
		return ret;
	}
	
	Key justDead = null;
	
	public boolean getActivation(Action a) {
		for(Key k : keysLeft) {
			if(Gdx.input.isKeyJustPressed(k.keycode)) {
				activateKey(k, a);
				if(justDead != null) {
					justDead.color = DEAD;
					justDead = null;
				}
				return true;
			}
		}
		return false;
	}
	
	public void activateKey(Key k, Action a) {
		if(keysLeft.remove(k)) {
			k.action = a;
			inputs.put(a, k);
			activeKeys.add(k);
			k.color = ACTIVE;
		}
	}
	
	public Action killPossibleKey() {
		for(Key k : activeKeys) {
			if (k.life <= 0) {
				Action ret = k.action;
				killKey(k);
				return ret;
			}
		}
		return null;
	}
	
	public void killKey(Key k) {
		if(activeKeys.remove(k)) {
			inputs.remove(k.action);
			k.action = null;
			deadKeys.add(k);
			k.color = KILLED;
			justDead = k;
		}
	}
	
	public static final int START_LIFE = 1400;
	
	public static class Key {
		int keycode;
		String keyname;
		
		int life;
		
		Action action = null;
		
		Color color = PENDING;
		
		public Key(int code) {
			this.keycode = code;
			this.keyname = Input.Keys.toString(code);
			
			life = START_LIFE;
		}
	}
	
	public static enum Action {
		SHOOTUP, SHOOTLEFT, SHOOTRIGHT, SHOOTDOWN,
		MOVEUP, MOVELEFT, MOVERIGHT, MOVEDOWN
	}
	
	public Color getColor(String key) {
		Key k = keyMap.get(key);
		if(k.color == PRESSED) {
			Color c = new Color(KILLED);
			return c.lerp(PRESSED, (float)k.life / (float)START_LIFE);
		}
		return k.color;
	}
	
	public void renderKey(float x, float y, String key, ShapeRenderer renderer) {
		renderer.setColor(getColor(key));
		renderer.rect(x, y-19, 19, 19);
		if(key.equals("Up")) {
			renderer.triangle(x + 3, y - 14, x + 9, y - 4, x + 15, y - 14);
		} else if(key.equals("Left")) {
			renderer.triangle(x + 4, y - 9, x + 14, y - 15, x + 14, y - 3);
		} else if(key.equals("Right")) {
			renderer.triangle(x + 14, y - 9, x + 4, y - 15, x + 4, y - 3);
		} else if(key.equals("Down")) {
			renderer.triangle(x + 3, y - 4, x + 9, y - 14, x + 15, y - 4);
		}
	}
	
	public void renderKeyText(float x, float y, String key, BitmapFont font, SpriteBatch batch) {
		font.setColor(getColor(key));
		font.draw(batch, key, x, y-3, 18, Align.center, false);
	}
	
	static final String[] row1 = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
	static final String[] row2 = new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
	static final String[] row3 = new String[]{"A", "S", "D", "F", "G", "H", "J", "K", "L"};
	static final String[] row4 = new String[]{"Z", "X", "C", "V", "B", "N", "M"};
	
	static final int spacing = 23;
	
	public void renderKeyboard(float x, float y, ShapeRenderer renderer) {
		for(int i=0; i<row1.length; i++) {
			renderKey(x + i * spacing, y, row1[i], renderer);
		}
		
		y -= spacing;
		for(int i=0;i<row2.length;i++) {
			renderKey(x + i * spacing, y, row2[i], renderer);
		}
		
		y -= spacing;
		x += 3;
		for(int i=0;i<row3.length;i++) {
			renderKey(x + i * spacing, y, row3[i], renderer);
		}
		
		y -= spacing;
		x += 6;
		for(int i=0;i<row4.length;i++) {
			renderKey(x + i * spacing, y, row4[i], renderer);
		}
		
		x += spacing * row4.length + 30;
		renderKey(x, y, "Left", renderer);
		renderKey(x + spacing, y, "Down", renderer);
		renderKey(x + spacing * 2, y, "Right", renderer);
		renderKey(x + spacing, y + spacing, "Up", renderer);
	}
	
	public void renderKeyboardText(float x, float y, BitmapFont font, SpriteBatch batch) {
		for(int i=0; i<row1.length; i++) {
			renderKeyText(x + i * spacing, y, row1[i], font, batch);
		}
		
		y -= spacing;
		for(int i=0;i<row2.length;i++) {
			renderKeyText(x + i * spacing, y, row2[i], font, batch);
		}
		
		y -= spacing;
		x += 3;
		for(int i=0;i<row3.length;i++) {
			renderKeyText(x + i * spacing, y, row3[i], font, batch);
		}
		
		y -= spacing;
		x += 6;
		for(int i=0;i<row4.length;i++) {
			renderKeyText(x + i * spacing, y, row4[i], font, batch);
		}
	}
}
