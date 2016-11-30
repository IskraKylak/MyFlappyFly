package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.FlyGame;
import com.mygdx.game.loader.ResourseLoader;
import com.mygdx.game.tools.SpriteAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;


/**
 * Created by Vlad on 17.02.2016.
 */
public class SplashScreen implements Screen{

    private TweenManager manager;
    private SpriteBatch batch;
    private Sprite sprite;
    private FlyGame game;
    //для вызова этого экрана при старте игры
    public SplashScreen(FlyGame game){
        this.game = game;
    }

    @Override
    public void show() {
        /*
        создаем спрайт и передаем ему лого
        из загрузчика ресурсов и устанавливаем
        для него белый цвет
         */
        sprite = new Sprite(ResourseLoader.logo);
        sprite.setColor(1, 1, 1, 0);
        /*
        обьявлям флоат переменные которые будут участвовать в
        преобразованиях
         */
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float desireWidth = width * 0.7f;
        float scale = desireWidth / sprite.getWidth();
        // устанавливаем размер и позицию лога
        sprite.setSize(sprite.getWidth() * scale, sprite.getHeight()*scale);
        sprite.setPosition((width / 2) - (sprite.getWidth() / 2), (height / 2)
                - (sprite.getHeight() / 2));

        //SpriteBatch для отрисовки
        setupTween();
        batch = new SpriteBatch();

    }
    // метод для преобразований
    private void setupTween (){
        /*
        регестрируем новый спрайт акцессор который
        будет изменять наш спрайт
        TweenManager - производит интерполяцию используя спрайт акцессор,
        ему мы будем передавать флоат дельта в методе рендер
         */
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        manager = new TweenManager();
        // его методы вызываються после окончания преобразований
        TweenCallback callback = new TweenCallback(){
            // будем вызывать гейм скрин
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                game.setScreen(new GameScreen());
            }
        };
        // код преобразования
        /*
        берем спрайт и увеличиваем прозрачность от 0 до 1 а
        затем снова уменьшаем до 0
        в конце указиваем колбек который вызывается после преобразования
        запускаем методом start передаме менеджер который выполнит преобразования
         */
        Tween.to(sprite, SpriteAccessor.ALPHA, 0.8f).target(1)
                .ease(TweenEquations.easeInOutQuad).repeatYoyo(1, 0.4f)
                .setCallback(callback).setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }
    // отрисовка
    @Override
    public void render(float delta) {
        // вызов менеджера
        manager.update(delta);
        // обновляем экран белым цветом
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //отрисовываем спрай между begin и end
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
