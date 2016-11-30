package com.mygdx.game.ui;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.loader.ResourseLoader;
// кнопка для старта игры
public class PlayButton {

    private float x, y, width, height;

    private TextureRegion buttonUp;
    private TextureRegion buttonDown;

    private Rectangle bounds;

    private boolean isPressed = false;

    public PlayButton(float x, float y, float width, float height, TextureRegion buttonUp, TextureRegion buttonDown) {
        this.x = x;
        this.y = y;
        this.buttonDown = buttonDown;
        this.buttonUp = buttonUp;
        this.width = width;

        this.height = height;

        bounds = new Rectangle(x, y, width, height);
    }
    // для отрисовки
    public void draw(SpriteBatch batch){
        if(isPressed){
            batch.draw(buttonDown, x, y, width, height);
        } else {
            batch.draw(buttonUp, x, y, width, height);
        }
    }
    // возавращает тру при нажатии кнопки
    public boolean isTouchDown(int screenX, int screenY){
        if(bounds.contains(screenX, screenY)){
            isPressed = true;
            return true;
        }
        return false;
    }
    // при  выходе из нажатого состояния
    // когда кнопка отжимаеться мы очистим флаг нажатия кнопки
    public boolean isTouchUp(int screenX, int screenY){
        if(bounds.contains(screenX, screenY)&& isPressed){
            isPressed = false;
            ResourseLoader.flap.play();
            return true;
        }
        isPressed = false;
        return false;
    }


}
