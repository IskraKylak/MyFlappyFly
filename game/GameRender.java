package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.loader.ResourseLoader;
import com.mygdx.game.objects.Fly;
import com.mygdx.game.objects.Grass;
import com.mygdx.game.objects.MovHandler;
import com.mygdx.game.objects.Web;
import com.mygdx.game.tools.Value;
import com.mygdx.game.tools.ValueAccessor;
import com.mygdx.game.ui.InputHandler;
import com.mygdx.game.ui.PlayButton;

import java.util.List;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by Vlad on 03.03.2016.
 */
// класс для отрисовки мира
public class GameRender {

    private int midPointX;
    private int midPointY;


    //обьявляем камеру
    private OrthographicCamera camera;
    // для добавления графики
    //ShapeRenderer -  рисует прямоугольник 1 цветом
    private ShapeRenderer shapeRenderer;
    // SpriteBatch - для отрисовки текстуры
    private SpriteBatch batch;

    private Fly myFly;
    private MovHandler movHandler;
    private Grass frontGrass, backGrass;
    private Web web1, web2, web3;

    private Sprite background, grass, flyMid, spider,webUp, webDown, ready,
    flyLogo, gameOver, highScore, scoreboard, starOn,starOff, retry;

    private Animation flyAnimation;
    private Music music;
    // переменные для вспышки
    private TweenManager manager;
    private Value alpha = new Value();
    private Color transitionColor;

    private List<PlayButton> menuButtons;

    GameWorld myWorld;

    // должен иметь доступ к GameWorld
    public GameRender(GameWorld world, int gameHeight, int midPointY, int midPointX) {
        myWorld = world;


        this.midPointX = midPointX;
        this.midPointY = midPointY;
        this.menuButtons = ((InputHandler) Gdx.input.getInputProcessor()).getMenuButtons();
        // создаем экземпляр камеры
        camera = new OrthographicCamera();
        // задаем ортогональную проэкцию, и задаем размеры игрового мира
        camera.setToOrtho(true,136, gameHeight);
        // SpriteBatch - отрисовывает картинки используя переданые указатели
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        initGameObjects();
        initAssets();
        // инициализируем ее в конструкторе
        transitionColor = new Color();
        prepareTransition(255, 255, 255, 0.5f);
    }
    // метод отрисовки
    // runTime - значения продожительности игры
    public void render(float delta, float runTime) {

        // заполняем задний фон одним цветом
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // стартуем shapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // рисуем фоновый цвет
        shapeRenderer.setColor(0 / 255.0f, 74 / 255.0f, 128 / 255.0f, 1);
        //shapeRenderer.setColor(186 / 255.0f, 224 / 255.0f, 213 / 255.0f, 1);
        shapeRenderer.rect(0, 0, 136, midPointY + 66);
        // отрисуем прямоугольник травы
        shapeRenderer.setColor(171 / 255.0f, 160 / 255.0f, 0 / 255.0f, 1);
        //shapeRenderer.setColor(167 / 255.0f, 211 / 255.0f, 152 / 255.0f, 1);
        shapeRenderer.rect(0, midPointY + 66, 136, 11);
        // прямоугольник воды
        shapeRenderer.setColor(132/ 255.0f, 125 / 255.0f, 200 / 255.0f, 1);
        //shapeRenderer.setColor(75 / 255.0f, 136 / 255.0f, 178 / 255.0f, 1);
        shapeRenderer.rect(0, midPointY + 77, 136, 53);
        // закончили рисовать
        shapeRenderer.end();


        // стратуем SpriteBatch
        batch.begin();
        // отменим прозрачность
        batch.disableBlending();
        // отрисуем фоновую картинку
        batch.draw(background, 0, midPointY + 23, 136, 43);
        // мухе нужна прозрачность включаем ее
        batch.enableBlending();

        drawGrass();
        drawWebs();
        drawSpiders();
        // вызываем методи отрисовки в зависимосте от игровых состояний
        if (myWorld.isRunning()) {
            drawFly(runTime);
            drawScore();
        } else if (myWorld.isReady()) {
            drawFly(runTime);
            drawReady();
        } else if (myWorld.isMenu()) {
            drawFlyCentered(runTime);
            drawMenuUI();
        } else if (myWorld.isGameOver()) {
            drawScoreboard();
            drawFly(runTime);
            drawGameOver();
            drawRetry();
        } else if (myWorld.isHighScore()) {
            drawScoreboard();
            drawFly(runTime);
            drawHighScore();
            drawRetry();
        }

        batch.end();
        drawTransition(delta);

        if (myFly.isAlive()) {
            music.play();
            music.isLooping();
        } else{
            music.stop();
        }
    }

    private void initAssets(){
        background = ResourseLoader.background;
        grass = ResourseLoader.grass;
        flyAnimation = ResourseLoader.flyAnimation;
        flyMid = ResourseLoader.fly2;
        spider = ResourseLoader.spider;
        webUp = ResourseLoader.webUp;
        webDown = ResourseLoader.webDown;
        ready = ResourseLoader.ready;
        flyLogo = ResourseLoader.flyAndSpiders;
        gameOver = ResourseLoader.gameOver;
        highScore = ResourseLoader.highScore;
        scoreboard = ResourseLoader.scoreboard;
        retry = ResourseLoader.retry;
        starOn = ResourseLoader.starOn;
        starOff = ResourseLoader.starOff;
        music = ResourseLoader.fly;
    }

    private void initGameObjects(){
        myFly = myWorld.getFly();
        movHandler = myWorld.getMovHandler();
        frontGrass = movHandler.getFrontGrass();
        backGrass = movHandler.getBackGrass();
        web1 = movHandler.getWeb1();
        web2 = movHandler.getWeb2();
        web3 = movHandler.getWeb3();
    }
    // отрисовка мухи
    private void drawFly(float runTime){
        // отрисовку мухи будем выполнять с учетом поворота
        if(myFly.notFlap()) {
            batch.draw(flyMid, myFly.getX(), myFly.getY(),
                    myFly.getWidth() / 2.0f, myFly.getHeight() / 2.0f,
                    myFly.getWidth(), myFly.getHeight(), 1, 1, myFly.getRotation());
        }else{
            batch.draw(flyAnimation.getKeyFrame(runTime), myFly.getX(),
                    myFly.getY(), myFly.getWidth() / 2.0f,
                    myFly.getHeight() / 2.0f, myFly.getWidth(), myFly.getHeight(),
                    1, 1, myFly.getRotation());
        }
    }
    // отрисовка травы
    private void drawGrass(){
        batch.draw(grass, frontGrass.getX(), frontGrass.getY(),
                frontGrass.getWidth(), frontGrass.getHeight());
        batch.draw(grass, backGrass.getX(), backGrass.getY(),
                backGrass.getWidth(), backGrass.getHeight());
    }
    // отисовка верхней и нижней паутины
    private void drawWebs(){
        batch.draw(webUp, web1.getX(), web1.getY(), web1.getWidth(),
                web1.getHeight());
        batch.draw(webDown, web1.getX(), web1.getY() + web1.getHeight() + 45,
                web1.getWidth(), midPointY + 66 - (web1.getHeight() + 45));

        batch.draw(webUp, web2.getX(), web2.getY(), web2.getWidth(),
                web2.getHeight());
        batch.draw(webDown, web2.getX(),web2.getY() + web2.getHeight() + 45,
                web2.getWidth(), midPointY + 66 - (web2.getHeight() + 45));

        batch.draw(webUp, web3.getX(), web3.getY(), web3.getWidth(),
                web3.getHeight());
        batch.draw(webDown, web3.getX(), web3.getY() + web3.getHeight() + 45,
                web3.getWidth(), midPointY + 66 - (web3.getHeight() + 45));
    }
    // отрисовывает пауков на вехней паутине
    private void drawSpiders(){

        batch.draw(spider, web1.getX() - 1,
                web1.getY() + web1.getHeight() - 14, 24, 14);

        batch.draw(spider, web2.getX() - 1,
                web2.getY() + web2.getHeight() - 14, 24, 14);

        batch.draw(spider, web3.getX() - 1,
                web3.getY() + web3.getHeight() - 14, 24, 14);
    }
    // подготовка отрисовки перехода
    public void prepareTransition(int r, int g, int b, float duration){
        // определяем цвет
        transitionColor.set(r / 255.0f, g / 255.0f, b / 255.0f, 1);
        // занчение 1 обьекту альфа
        alpha.setVal(1);
        //
        Tween.registerAccessor(Value.class, new ValueAccessor());
        // будет анумировать наш переход
        manager = new TweenManager();
        // запускаем метод создания новой интреполяции
        Tween.to(alpha, - 1, duration).target(0)
                .ease(TweenEquations.easeOutQuad).start(manager);
    }
    // отрисовывает вспышку
    private void drawTransition(float delta){
        // проверяем что альфа больше 0
        if(alpha.getVal() > 0){
            // обновляем твин менеджер
            manager.update(delta);
            // включаем смешование опен гл
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(transitionColor.r, transitionColor.g,
                    transitionColor.b, alpha.getVal());
            shapeRenderer.rect(0, 0, 136, 300);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }
    // метод отрисовки меню
    private void drawMenuUI (){
        batch.draw(flyLogo, midPointX - 48, midPointY - 50, 96, 14);

        for(PlayButton button : menuButtons) {
            button.draw(batch);
        }
    }

    private void drawScoreboard() {
        //отрисовка турнирной таблицы результатов
        batch.draw(scoreboard, 22, midPointY - 30, 97, 37);
        // отрисовка звезды
        batch.draw(starOff, 25, midPointY -15, 10, 10);
        batch.draw(starOff, 37, midPointY -15, 10, 10);
        batch.draw(starOff, 49, midPointY -15, 10, 10);
        batch.draw(starOff, 61, midPointY -15, 10, 10);
        batch.draw(starOff, 73, midPointY -15, 10, 10);


        if(myWorld.getScore() > 2) {
            batch.draw(starOn, 73, midPointY - 15, 10, 10);
        }

        if(myWorld.getScore() > 17) {
            batch.draw(starOn, 61, midPointY - 15, 10, 10);
        }

        if(myWorld.getScore() > 50) {
            batch.draw(starOn, 49, midPointY - 15, 10, 10);
        }

        if(myWorld.getScore() > 80) {
            batch.draw(starOn, 37, midPointY - 15, 10, 10);
        }

        if(myWorld.getScore() > 120) {
            batch.draw(starOn, 25, midPointY - 15, 10, 10);
        }
        // с помощью шрифтов выводится текущий и лучший результат
        int length = ("" + myWorld.getScore()).length();

        ResourseLoader.whiteFont.draw(batch,"" + myWorld.getScore(),
                104 - (2 * length), midPointY -20);

        int length2 = ("" + ResourseLoader.getHighScore()).length();
        ResourseLoader.whiteFont.draw(batch,"" + ResourseLoader.getHighScore(),
                104 - (2.5f * length2), midPointY -3);
    }
    // методи отрисовки надписи на экране которые выводятся с помощью изображений
    private void drawRetry(){
        batch.draw(retry, 36, midPointY + 10, 66, 14);
    }

    private void drawReady(){
        batch.draw(ready, 36, midPointY - 50, 68, 14);
    }

    private void drawGameOver(){
        batch.draw(gameOver, 24, midPointY - 50, 92, 14);
    }

    private  void drawScore(){
        int lenght = ("" + myWorld.getScore()).length();
        ResourseLoader.shadow.draw(batch,"" + myWorld.getScore(),
                68 - (3 * lenght), midPointY - 82);
        ResourseLoader.font.draw(batch, "" + myWorld.getScore(),
                68-(3 * lenght), midPointY - 83);
    }

    private void drawHighScore(){
        batch.draw(highScore, 22, midPointY - 50, 96, 14);
    }
    // метод отрисовки мухи на экране меню
    private void drawFlyCentered(float runTime){
        batch.draw(flyAnimation.getKeyFrame(runTime), 59, myFly.getY() - 15,
                myFly.getWidth() / 2.0f, myFly.getHeight() / 2.0f,
                myFly.getWidth(), myFly.getHeight(), 1, 1, myFly.getRotation());
    }

}

