package com.mygdx.game.objects;


import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;
// розширяет класс мовинг
public class Web extends Moving {
    // для создания препятствий на разной высоте
    private Random r;
    // прямоугольники для  паутины и паука
    private Rectangle spider, webUp, webDown;
    // константа для указания просвета между прямоугольниками
    private static final int GAP = 45;
    // булеан переменная для очков
    private boolean isScored = false;

    private float groundY;

    public Web(float x, float y, int width, int height,float movSpeed, float groundY) {
        super(x, y, width, height, movSpeed);

        r = new Random();
        // инициализируем прямоугольники
        spider = new Rectangle();
        webUp = new Rectangle();
        webDown = new Rectangle();

        this.groundY = groundY;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // рисуем прямоугольники
        webUp.set(position.x, position.y, width, height);
        webDown.set(position.x, position.y + height + GAP, width, groundY - (position.y + height
                + GAP));
        spider.set(position.x - (24 - width) /2,position.y + height - 11, 24, 11);
    }

    @Override
    public void reset(float newX) {
        super.reset(newX);
        // меняем высоту на случайное значение из указаного диапозона
        // добавляем 15 пик. что б обьекты не улетали за экран
        height = r.nextInt(90) + 15;
        // коггда сбрасываеться положение паутины
        isScored = false;
    }
    //
    public boolean collides(Fly fly){
        // позиция прямоугольника по Х меньше позиция + ширина мухи зеачит сталкновение
        if(position.x < fly.getX() + fly.getWidth()){
            // вернет труе если любой из прямоугольников пересечеться с кругом нашей мухи
            return (Intersector.overlaps(fly.getCircle(), webUp)) ||
                    Intersector.overlaps(fly.getCircle(), webDown) ||
                    Intersector.overlaps(fly.getCircle(), spider);
        }
        return false;
    }

    public boolean isScored() {
        return isScored;
    }

    public void setScored(boolean b){
        isScored = b;
    }

    public void onRestart(float x, float movSpeed) {
        velocity.x = movSpeed;
        reset(x);
    }
}
