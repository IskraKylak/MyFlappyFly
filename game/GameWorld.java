package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.loader.ResourseLoader;
import com.mygdx.game.objects.Fly;
import com.mygdx.game.objects.MovHandler;
import com.mygdx.game.objects.Moving;


// класс обновления мира
public class GameWorld {
    // отправляем  муху в игровой мир
    private Fly fly;
    private MovHandler movHandler;
    // прямоугольник земли
    private Rectangle ground;




    // центр экрана
    private int midPointY;
    private int midPointX;
    // храним счет игрока
    private int score = 0;
    //
    private float runTime = 0;

    //
    private GameRender renderer;
    // переменная для состояний игры ( меню конец игры запуск)
    private GameState currentState;

    // переменная которая может принимать те значения которые мы указали для нее
    public enum GameState {
        MENU, READY, RUNNING, GAMEOVER, HIGHSCORE
    }

    public GameWorld(int midPointY, int midPointX) {
        // инициализирует ее в конструкторе
        currentState = GameState.MENU;
        this.midPointY = midPointY;
        this.midPointX = midPointX;
        // 33 - точка Х где муха находится на протяжении всей игры
        // указываем кординаты и размер мухи
        fly = new Fly(33, midPointY - 5, 27, 22);
        // инициализируем прокрутку в конструкторе передаем положение травы
        // она должана быть на 66 пик. ниже центра
        movHandler = new MovHandler(this, midPointY + 66);
        // рисуем прямоугольники земли
        ground = new Rectangle(0, midPointY + 66, 137, 11);

    }
    // проверяет текущее состояние игры перед тем как запустить логику обновления
    public void update(float delta){
        runTime += delta;

        switch (currentState){
            case READY:
            case MENU:
                updateReady(delta);
                break;
            case RUNNING:
                updateRunning(delta);
                break;
            default:
                break;
        }
    }

    private void updateReady(float delta){
        fly.updateReady(runTime);
        movHandler.updateReady(delta);
    }

    // вызываем метод обновления мухи внути этого метода
    public void updateRunning(float delta)
    {
        // эфект нормализации
        if(delta > 0.15f){
            delta = 0.15f;
        }


        fly.update(delta);
        movHandler.update(delta);

        // при столкновении останавливаем мир
        //смерть мухи
        // прилипание мухи и музика
        if(movHandler.collides(fly) && fly.isAlive() ) {
            movHandler.stop();
            fly.die();
            fly.cling();

            ResourseLoader.fall.play();
            renderer.prepareTransition(255, 255, 255, 0.3f);
            currentState = GameState.GAMEOVER;
        }
        // столкновение с землей
        if (Intersector.overlaps(fly.getCircle(), ground))
        {
            if(fly.isAlive()){
                ResourseLoader.dead.play();
                fly.die();
                // отрисовка белого экрана
                renderer.prepareTransition(255, 255, 255, 0.3f);
            }
            movHandler.stop();
            fly.cling();
            currentState = GameState.GAMEOVER;
            // условия сохранения лучшего результата и оповещения о нем
            if(score > ResourseLoader.getHighScore()){
                ResourseLoader.setHighScore(score);
                currentState = GameState.HIGHSCORE;
            }
        }

    }

    public MovHandler getMovHandler() {
        return movHandler;
    }

    // гетер для мухи
    public Fly getFly() {
        return fly;
    }

    public void setRenderer(GameRender renderer) {
        this.renderer = renderer;
    }
    // доступ к счету игрока
    public int getScore() {
        return score;
    }
    // увеличивает счет игрока
    public void addScore(int increment){
        score += increment;
    }
    // устанавливает currentState в READY
    public void ready(){
        currentState = GameState.READY;
        renderer.prepareTransition(0, 0, 0, 1f);
    }
    // изменяет корент статйт на RUNNING
    public void start(){
        currentState = GameState.RUNNING;
    }
    // обнуляет все переменные которые подвергались изменению в игре ( по умолчанию)
    public void restart(){
        score = 0;
        fly.onRestart(midPointY - 5);
        movHandler.onRestart();
        ready();
    }
    // еще пару методов для разных состояний
    public boolean isReady(){
        return currentState == GameState.READY;
    }

    public boolean isGameOver(){
        return currentState == GameState.GAMEOVER;
    }

    public boolean isHighScore(){
        return currentState == GameState.HIGHSCORE;
    }

    public boolean isMenu(){
        return currentState == GameState.MENU;
    }

    public boolean isRunning(){
        return currentState == GameState.RUNNING;
    }

    public int getMidPointY() {
        return midPointY;
    }

    public int getMidPointX() {
        return midPointX;
    }
}
