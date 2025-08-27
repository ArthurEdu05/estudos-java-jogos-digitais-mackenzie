package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main implements ApplicationListener {

    Texture naveTexture;
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Sprite naveSprite;
    Texture backgroundTexture;
    Vector2 touchPos;

    @Override
    public void create() {
        naveTexture = new Texture("nave.png");
        backgroundTexture = new Texture("background.jpg");
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);

        naveSprite = new Sprite(naveTexture);
        naveSprite.setSize(1, 1);

        touchPos = new Vector2();
        
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if (width <= 0 || height <= 0) {
            return;
        }

        // Resize your application here. The parameters represent the new window size.
    }

    @Override
    public void render() {
        // Draw your application here.
        input();
        logic();
        draw();
    }

    private void input() {
        float speed = 5f;
        float delta = Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            naveSprite.translateX(speed * delta);       
         } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            naveSprite.translateX(-speed * delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            naveSprite.translateY(speed * delta);       
         } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            naveSprite.translateY(-speed * delta);
    }

    if(Gdx.input.isTouched()){
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            naveSprite.setCenterX(touchPos.x);
            naveSprite.setCenterY(touchPos.y);
    }
}
    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float naveWidth = naveSprite.getWidth();

        float worldHeight = viewport.getWorldHeight();
        float naveHeight = naveSprite.getHeight();
        naveSprite.setX(MathUtils.clamp(naveSprite.getX(), 0, worldWidth - naveWidth));
        naveSprite.setY(MathUtils.clamp(naveSprite.getY(), 0, worldHeight - naveHeight));
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        spriteBatch.draw(backgroundTexture, 0,0, worldWidth, worldHeight);

        naveSprite.draw(spriteBatch);
        spriteBatch.end();

        
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}

