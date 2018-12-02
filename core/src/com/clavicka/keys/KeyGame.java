package com.clavicka.keys;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class KeyGame extends ApplicationAdapter {

	static final int UI_HEIGHT = 120;

	Sound[] shootSounds;
	Sound hitSound;
	Sound spinSpawnSound;
	Sound bigSpawnSound;
	Sound hurtSound;
	Sound deadSound;
	Sound powerSound;
	Sound boomSound;
	Sound pickupSound;
	float volume = 0.3f;

	Keys keys;
	Player player;

	ShapeRenderer renderer;
	BitmapFont font;
	SpriteBatch batch;

	int score;

	List<Bullet> playerBullets;
	List<Enemy> enemies;
	List<Bullet> enemyBullets;

	public static KeyGame game;

	int spawnTime = 1;

	Random gen;

	Keys.Action currentSacrifice = null;

	OrthographicCamera camera;

	int gameWidth;
	int gameHeight;

	Powerup lifePower = null;
	Powerup bombPower = null;

	int life_target = 3000;
	int bomb_target = 1000;
	
	int numBombs = 0;
	
	float bombRadius = 0;
	
	boolean menu = true;
	boolean dead = false;

	@Override
	public void create() {

		game = this;

		gen = new Random();

		font = new BitmapFont();
		font.setFixedWidthGlyphs("0123456789");
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();

		gameWidth = Gdx.graphics.getWidth();
		gameHeight = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		calcSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		shootSounds = new Sound[5];
		for (int i = 0; i < 5; i++) {
			shootSounds[i] = Gdx.audio.newSound(Gdx.files.internal("shoot" + (i + 1) + ".wav"));
		}
		hitSound = Gdx.audio.newSound(Gdx.files.internal("hit1.wav"));
		spinSpawnSound = Gdx.audio.newSound(Gdx.files.internal("spawn-spin.wav"));
		bigSpawnSound = Gdx.audio.newSound(Gdx.files.internal("spawn-big.wav"));
		hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.wav"));
		deadSound = Gdx.audio.newSound(Gdx.files.internal("dead.wav"));
		powerSound = Gdx.audio.newSound(Gdx.files.internal("powerup.wav"));
		boomSound = Gdx.audio.newSound(Gdx.files.internal("boom1.wav"));
		pickupSound = Gdx.audio.newSound(Gdx.files.internal("coin1.wav"));

		// keys.killKey(keys.inputs.get(Keys.Action.MOVEUP));
		initializeGame();
	}
	
	public void initializeGame() {
		keys = new Keys();
		player = new Player(new Vector2(100, 100));
		playerBullets = new ArrayList<>();
		enemyBullets = new ArrayList<>();
		enemies = new ArrayList<>();

		score = 0;
		
		currentSacrifice = null;
		bombRadius = 0;
		bombOrigin = null;
		numBombs = 0;
		life_target = 3000;
		bomb_target = 1000;
		spawnTime = 1;
		lifePower = null;
		bombPower = null;
		dead = false;
	}

	public void calcSize(int width, int height) {
		float aspect = (float) height / width;
		camera.setToOrtho(false, 600, 600 * aspect);
		gameHeight = (int) (600 * aspect) - UI_HEIGHT;
		gameWidth = 600;
		camera.position.set(gameWidth / 2, (gameHeight - UI_HEIGHT) / 2, 0);
		camera.update();

		renderer.setProjectionMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);
	}

	public void spawnEnemy() {
		int top = score / 800;
		int num = gen.nextInt(Math.min(top, 11) + 1);
		int posx = gen.nextInt(gameWidth);
		int posy, movy;
		if (gen.nextBoolean()) {
			posy = -40;
			movy = 1;
		} else {
			posy = gameHeight + 40;
			movy = -1;
		}
		if (num < 7) {
			Enemy e = new Enemy(new Vector2(posx, posy), 10, 3, 100);
			enemies.add(e);
			e.movementDir.y = movy;

			spawnTime = 100;
		} else if (num < 10) {
			Enemy e = new WandererEnemy(new Vector2(posx, posy));
			enemies.add(e);
			e.movementDir.y = movy;

			spawnTime = 300;
			bigSpawnSound.play(volume);
		} else {
			Enemy e = new SpinEnemy(new Vector2(posx, posy));
			enemies.add(e);

			spawnTime = 200;
			spinSpawnSound.play(volume);
		}
	}

	public void checkSacrifice() {
		currentSacrifice = keys.killPossibleKey();
		if(keys.keysLeft.isEmpty()) {
			currentSacrifice = null;
		}
	}

	public void getInput() {
		Set<Keys.Action> actions = keys.getActions();
		player.movementDir.setZero();
		player.fireDir.setZero();
		if(dead) return;
		for (Keys.Action a : actions) {
			switch (a) {
			case MOVEUP:
				if (!actions.contains(Keys.Action.MOVEDOWN)) {
					player.movementDir.y = 1;
				}
				break;
			case MOVEDOWN:
				if (!actions.contains(Keys.Action.MOVEUP)) {
					player.movementDir.y = -1;
				}
				break;
			case MOVELEFT:
				if (!actions.contains(Keys.Action.MOVERIGHT)) {
					player.movementDir.x = -1;
				}
				break;
			case MOVERIGHT:
				if (!actions.contains(Keys.Action.MOVELEFT)) {
					player.movementDir.x = 1;
				}
				break;
			case SHOOTUP:
				if (!actions.contains(Keys.Action.SHOOTDOWN)) {
					player.fireDir.y = 1;
				}
				break;
			case SHOOTDOWN:
				if (!actions.contains(Keys.Action.SHOOTUP)) {
					player.fireDir.y = -1;
				}
				break;
			case SHOOTLEFT:
				if (!actions.contains(Keys.Action.SHOOTRIGHT)) {
					player.fireDir.x = -1;
				}
				break;
			case SHOOTRIGHT:
				if (!actions.contains(Keys.Action.SHOOTLEFT)) {
					player.fireDir.x = 1;
				}
				break;
			}
		}

		player.fireDir.nor();
		player.movementDir.nor();
	}

	public boolean oob(Entity e) {
		return e.position.x - e.radius < 0 || e.position.y - e.radius < 0 || e.position.x + e.radius > gameWidth
				|| e.position.y + e.radius > gameHeight;
	}
	
	float bomb_speed = 10;
	Vector2 bombOrigin = null;

	public void update() {
		if(menu) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				menu = false;
			}
			return;
		}
		
		if(dead && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			initializeGame();
			return;
		}
		if (currentSacrifice != null) {
			if (!keys.getActivation(currentSacrifice))
				return;
			else
				currentSacrifice = null;
		}

		List<Bullet> deadBullets = new ArrayList<>();
		List<Enemy> deadEnemies = new ArrayList<>();

		getInput();
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && numBombs > 0 && bombOrigin == null) {
			bombRadius = 0;
			bombOrigin = new Vector2(player.position);
			boomSound.play(volume);
			numBombs--;
		} else if(bombOrigin != null) {
			bombRadius += bomb_speed;
			if(bombRadius > 2000) {
				bombRadius = 0;
				bombOrigin = null;
			}
		}
		if(!dead)
		player.update();
		
		if(lifePower != null && player.collidesWith(lifePower)) {
			lifePower = null;
			player.health = Math.min(player.health + 1, Player.MAX_HEALTH);
			powerSound.play(volume);
		}
		if(bombPower != null && player.collidesWith(bombPower)) {
			bombPower = null;
			numBombs = Math.min(numBombs + 1, 9);
			pickupSound.play(volume);
		}
		
		for (Bullet b : playerBullets) {
			b.update();
			if (b.time <= 0) {
				deadBullets.add(b);
			}
		}
		for (Enemy e : enemies) {
			e.update();
			for (Bullet b : playerBullets) {
				if (e.collidesWith(b)) {
					deadBullets.add(b);
					e.health--;
					if(e.health > 0);
					hitSound.play(volume);
				}
				if (e.health <= 0) {
					deadEnemies.add(e);
					score += e.score;
					deadSound.play(volume);
				}
			}
			if(bombOrigin != null) {
				float dst = e.position.dst(bombOrigin) - bombRadius;
				if(Math.abs(dst) <= e.radius) {
					deadEnemies.add(e);
					deadSound.play(volume);
				}
			}

			if (player.collidesWith(e) && player.invulnTime <= 0) {
				player.health--;
				player.invulnTime = 100;
				hurtSound.play(volume);
			}
		}

		spawnTime--;
		if (spawnTime <= 0) {
			spawnEnemy();
		}

		checkSacrifice();

		if (!player.fireDir.isZero() && player.fireCooldown == 0) {
			Bullet b = new Bullet(new Vector2(player.position), 2.0f,
					new Vector2(player.fireDir).scl(4).mulAdd(player.movementDir, player.speed),
					100);
			playerBullets.add(b);
			player.fireCooldown = 20;
			shootSounds[gen.nextInt(shootSounds.length)].play(volume);
		}

		if (score > life_target) {
			life_target += 5000;
			if (lifePower == null) {
				lifePower = new Powerup(new Vector2(gen.nextInt(gameWidth), gen.nextInt(gameHeight)),
						5, Powerup.LIFE_HEART);
			}
		}
		if (score > bomb_target) {
			bomb_target *= 2;
			bomb_target += 30000;
			if (bombPower == null) {
				bombPower = new Powerup(new Vector2(gen.nextInt(gameWidth), gen.nextInt(gameHeight)),
						5, Powerup.BOMB);
			}
		}

		playerBullets.removeAll(deadBullets);
		enemies.removeAll(deadEnemies);
		
		if(player.health <= 0) {
			dead = true;
		}
	}

	@Override
	public void render() {

		update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.begin(ShapeType.Line);

		if(!dead) player.render(renderer);
		
		if(lifePower != null) lifePower.render(renderer);
		if(bombPower != null) bombPower.render(renderer);

		for (Enemy e : enemies) {
			e.render(renderer);
		}
		for (Bullet b : playerBullets) {
			b.render(renderer);
		}
		
		if(bombOrigin != null) {
			renderer.setColor(Color.RED);
			renderer.circle(bombOrigin.x, bombOrigin.y, bombRadius);
		}
		
		renderer.end();

		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.BLACK);
		renderer.rect(0, -UI_HEIGHT, gameWidth, UI_HEIGHT);
		renderer.setColor(Color.ROYAL);
		renderer.rect(0, -2, gameWidth, 2);
		renderer.end();

		int life_x = 90;
		int life_y = 20 - UI_HEIGHT;

		// Draw UI stuff
		renderer.begin(ShapeType.Line);
		keys.renderKeyboard(320, 100 - UI_HEIGHT, renderer);
		renderer.setColor(Color.GREEN);
		renderer.translate(life_x + 45, 90 - UI_HEIGHT, 0);
		renderer.scale(10, 10, 1);
		renderer.polygon(Powerup.BOMB);
		renderer.identity();
		
		Color spaceColor = (numBombs > 0 && !keys.keysLeft.isEmpty()) ? Keys.ACTIVE : Keys.DEAD;
		
		renderer.setColor(spaceColor);
		renderer.rect(life_x - 40, 80 - UI_HEIGHT, 70, 19);
		
		renderer.end();

		renderer.begin(ShapeType.Filled);

		// Render life bar

		Color life_color = new Color(Color.RED);
		renderer.setColor(life_color.lerp(Color.GREEN, (float) player.health / Player.MAX_HEALTH));
		renderer.rect(life_x, life_y, player.health * 20, 20);

		renderer.setColor(Color.ROYAL);
		renderer.rect(life_x, life_y, Player.MAX_HEALTH * 20, 2);
		renderer.rect(life_x, life_y, 2, 20);
		renderer.rect(life_x, life_y + 18, Player.MAX_HEALTH * 20, 2);
		renderer.rect(life_x + Player.MAX_HEALTH * 20 - 2, life_y, 2, 20);

		renderer.end();

		batch.begin();
		keys.renderKeyboardText(320, 99 - UI_HEIGHT, font, batch);
		
		font.setColor(spaceColor);
		font.draw(batch, "Space", life_x - 25, 96 - UI_HEIGHT);

		font.setColor(Color.WHITE);
		font.draw(batch, Integer.toString(numBombs), life_x + 62, 95 - UI_HEIGHT);

		if (currentSacrifice != null) {
			font.draw(batch, "Remap Key for \"" + currentSacrifice.toString() + '"', 150, 200 - UI_HEIGHT);
		}
		
		if(menu) {
			font.draw(batch, "Press SPACE to begin", 200, 150);
		}
		
		if(dead) {
			font.draw(batch, "You have died", 200, 150);
			font.draw(batch, "Press SPACE to restart", 200, 120);
		}

		font.draw(batch, "Life:", life_x - 40, 35 - UI_HEIGHT);

		font.draw(batch, "Score: " + score, life_x - 40, 65 - UI_HEIGHT);

		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		calcSize(width, height);
	}

	@Override
	public void dispose() {
		renderer.dispose();
		font.dispose();
		batch.dispose();

		for (Sound s : shootSounds) {
			s.dispose();
		}
		hitSound.dispose();
		spinSpawnSound.dispose();
		bigSpawnSound.dispose();
		hurtSound.dispose();
		deadSound.dispose();
		boomSound.dispose();
		powerSound.dispose();
		pickupSound.dispose();
	}
}
