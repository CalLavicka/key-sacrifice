package com.clavicka.keys;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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

	@Override
	public void create() {
		keys = new Keys();
		player = new Player(new Vector2(100, 100));
		renderer = new ShapeRenderer();
		playerBullets = new ArrayList<>();
		enemyBullets = new ArrayList<>();
		enemies = new ArrayList<>();

		game = this;

		gen = new Random();
		
		font = new BitmapFont();
		font.setFixedWidthGlyphs("0123456789");
		batch = new SpriteBatch();
		
		score = 0;
		
		gameWidth = Gdx.graphics.getWidth();
		gameHeight = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
		calcSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		

		// keys.killKey(keys.inputs.get(Keys.Action.MOVEUP));
	}
	
	public void calcSize(int width, int height) {
		float aspect = (float)height / width;
		camera.setToOrtho(false, 600, 600 * aspect);
		gameHeight = (int) (600*aspect) - UI_HEIGHT;
		gameWidth = 600;
		camera.position.set(gameWidth/2, (gameHeight - UI_HEIGHT)/2,0);
		camera.update();
		
		renderer.setProjectionMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);
	}

	public void spawnEnemy() {
		int top = score / 800;
		int num = gen.nextInt(Math.min(top, 9) + 1);
		int posx = gen.nextInt(gameWidth);
		int posy, movy;
		if(gen.nextBoolean()) {
			posy = -40;
			movy = 1;
		} else {
			posy = gameHeight + 40;
			movy = -1;
		}
		if (num < 5) {
			Enemy e = new Enemy(new Vector2(posx, posy), 10, 3, 100);
			enemies.add(e);
			e.movementDir.y = movy;

			spawnTime = 100;
		} else if (num < 8) {
			Enemy e = new WandererEnemy(new Vector2(posx, posy));
			enemies.add(e);
			e.movementDir.y = movy;

			spawnTime = 300;
		} else {
			Enemy e = new SpinEnemy(new Vector2(posx, posy));
			enemies.add(e);
			
			spawnTime = 200;
		}
	}

	public void checkSacrifice() {
		currentSacrifice = keys.killPossibleKey();
	}

	public void getInput() {
		Set<Keys.Action> actions = keys.getActions();
		player.movementDir.setZero();
		player.fireDir.setZero();
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
		return e.position.x - e.radius < 0 || e.position.y - e.radius < 0 ||
				e.position.x + e.radius > gameWidth ||
				e.position.y + e.radius > gameHeight;
	}

	public void update() {
		if (currentSacrifice != null) {
			if (!keys.getActivation(currentSacrifice))
				return;
			else
				currentSacrifice = null;
		}

		List<Bullet> deadBullets = new ArrayList<>();
		List<Enemy> deadEnemies = new ArrayList<>();

		getInput();
		player.update();
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
				}
				if (e.health <= 0) {
					deadEnemies.add(e);
					score += e.score;
				}
			}
			
			if(player.collidesWith(e) && player.invulnTime <= 0) {
				player.health--;
				player.invulnTime = 100;
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
		}

		playerBullets.removeAll(deadBullets);
		enemies.removeAll(deadEnemies);
	}

	@Override
	public void render() {

		update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.begin(ShapeType.Line);
		player.render(renderer);

		for (Enemy e : enemies) {
			e.render(renderer);
		}
		for (Bullet b : playerBullets) {
			b.render(renderer);
		}
		renderer.end();
		
		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.BLACK);
		renderer.rect(0, -UI_HEIGHT, gameWidth, UI_HEIGHT);
		renderer.setColor(Color.ROYAL);
		renderer.rect(0, -2,gameWidth,2);
		renderer.end();
		
		
		// Draw UI stuff
		renderer.begin(ShapeType.Line);
		keys.renderKeyboard(320,100-UI_HEIGHT, renderer);
		renderer.end();
		
		renderer.begin(ShapeType.Filled);

		// Render life bar
		
		int life_x = 90;
		int life_y = 30-UI_HEIGHT;
		
		Color life_color = new Color(Color.RED);
		renderer.setColor(life_color.lerp(Color.GREEN, (float)player.health / Player.MAX_HEALTH));
		renderer.rect(life_x, life_y, player.health * 20, 20);
		
		renderer.setColor(Color.ROYAL);
		renderer.rect(life_x, life_y, Player.MAX_HEALTH*20, 2);
		renderer.rect(life_x, life_y, 2, 20);
		renderer.rect(life_x, life_y + 18, Player.MAX_HEALTH*20, 2);
		renderer.rect(life_x + Player.MAX_HEALTH * 20 - 2, life_y, 2, 20);
		
		renderer.end();
		

		batch.begin();
		keys.renderKeyboardText(320,99-UI_HEIGHT, font, batch);
		

		font.setColor(Color.WHITE);
		
		if(currentSacrifice != null) {
			font.draw(batch, "Remap Key for \"" + currentSacrifice.toString() + '"', 150, 200-UI_HEIGHT);
		}
		
		font.draw(batch, "Life:", life_x - 40, 45-UI_HEIGHT);

		font.draw(batch, "Score: " + score, life_x - 40, 75-UI_HEIGHT);
		
		
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
	}
}
