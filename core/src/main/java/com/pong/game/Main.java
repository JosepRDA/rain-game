package com.pong.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture dropImage;
    private Texture bucketImage;
    private OrthographicCamera camera;
    private Rectangle bucket;
    private Array<Rectangle> raindrops; // an array that stores my raindrops
    private long lastDropTime;


    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800-64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }


    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        raindrops = new Array<Rectangle>();
        spawnRaindrop();

        dropImage = new Texture(Gdx.files.internal("drop.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0.2f, 1f);
        batch.setProjectionMatrix(camera.combined);
        batch.begin(); // BEGINS HERE

        batch.draw(bucketImage, bucket.x,bucket.y);

        // this renders the raindrops as the come and go
        for(Rectangle raindrop: raindrops) {
            batch.draw(dropImage, raindrop.x, raindrop.y);
        }

        batch.end();

        // this is what makes the bucket move when I use the mouse
        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(); // will turn this into a field later on
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;

        }

        // this is responsible for keyboard input in the bucket
        // accepts both arrow keys and A-D
        if(Gdx.input.isKeyPressed(Input.Keys.A) ||
            Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 300 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.D) ||
            Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 300 * Gdx.graphics.getDeltaTime();

        // this checks for collision with the screen borders and makes sure
        // that the bucket does not escape view
        if(bucket.x < 0) bucket.x = 0;
        if(bucket.x > 800 - 64) bucket.x = 800 - 64;

        // this checks if a certain period of time has passed and if the program can spawn a new raindrop
        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        // this makes the raindrops move and despawn if they are outside the screen
        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 64 < 0) iter.remove();

            // has to be inside this for loop no matter what
            if(raindrop.overlaps(bucket)) {
                iter.remove();
            }


        }

    }



    @Override
    public void dispose() {
        // Once you dispose of a resource, you should not access it in any way.
        batch.dispose();
        dropImage.dispose();
        bucketImage.dispose();
    }
}
