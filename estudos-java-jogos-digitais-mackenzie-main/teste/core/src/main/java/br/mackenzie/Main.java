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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

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
    Texture meteorTexture;
    Array<Sprite> meteorSprites;
    float meteorTimer;
    //Variáveis para caso de derrota
    boolean gameOver;
    Texture gameOverTexture;

    Rectangle naveRectangle;
    Rectangle meteorRectangle;
    Sound meteorSound;
    Music music;
    @Override
    public void create() {
        naveTexture = new Texture("nave.png");
        backgroundTexture = new Texture("background.jpg");
        meteorTexture = new Texture("meteor.png");
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);

        naveSprite = new Sprite(naveTexture);
        naveSprite.setSize(1, 1);

        touchPos = new Vector2();
        
        meteorSprites = new Array<>();

        naveRectangle = new Rectangle();
        meteorRectangle = new Rectangle();
        gameOverTexture = new Texture("gameover.png");
        meteorSound = Gdx.audio.newSound(Gdx.files.internal("meteor.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(.5f);
        music.play();
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
        float speed = 3f;
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
        if (gameOver) return; //condição de derrota
        float worldWidth = viewport.getWorldWidth();
        float naveWidth = naveSprite.getWidth();

        float worldHeight = viewport.getWorldHeight();
        float naveHeight = naveSprite.getHeight();
        naveSprite.setX(MathUtils.clamp(naveSprite.getX(), 0, worldWidth - naveWidth));
        naveSprite.setY(MathUtils.clamp(naveSprite.getY(), 0, worldHeight - naveHeight));

        float delta = Gdx.graphics.getDeltaTime();

        naveRectangle.set(naveSprite.getX(), naveSprite.getY(), naveWidth, naveHeight);

        for (int i = meteorSprites.size - 1; i >= 0; i--){
            Sprite meteorSprite = meteorSprites.get(i);
            float meteorWidth = meteorSprite.getWidth();
            float meteorHeight = meteorSprite.getHeight();

            meteorSprite.translateY(-2.5f * delta);
            meteorRectangle.set(meteorSprite.getX(), meteorSprite.getY(), meteorWidth, meteorHeight);
            if (meteorSprite.getY() < -meteorHeight) meteorSprites.removeIndex(i);
            else if(naveRectangle.overlaps(meteorRectangle)){
                meteorSprites.removeIndex(i);
                meteorSound.play();
                gameOver = true;
            }

        }

           
         

        meteorTimer += delta;
        if(meteorTimer > 1.5f){
            meteorTimer = 0;
            createMeteor();
        }

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        spriteBatch.draw(backgroundTexture, 0,0, worldWidth, worldHeight);
        //nave some em caso de colisão e derrota
        if(!gameOver){
        naveSprite.draw(spriteBatch);
        }
        for(Sprite meteorSprite : meteorSprites){
            meteorSprite.draw(spriteBatch);
        }
        // caso haja derrota, imprime img de game over no centro da tela
        if(gameOver){
            float imgWidth = 4f;
            float imgHeight = 2f;
            spriteBatch.draw(gameOverTexture, (worldWidth - imgWidth) /2, (worldHeight - imgHeight) /2 , imgWidth, imgHeight);
        }
        spriteBatch.end();
    }

    private void createMeteor(){
        float meteorWidth = 1;
        float meteorHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite meteorSprite = new Sprite(meteorTexture);
        meteorSprite.setSize(meteorWidth, meteorHeight);
        meteorSprite.setX(MathUtils.random(0f, worldWidth - meteorWidth));
        meteorSprite.setY(worldHeight);
        meteorSprites.add(meteorSprite);
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

