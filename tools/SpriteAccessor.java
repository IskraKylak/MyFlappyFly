package com.mygdx.game.tools;

import com.badlogic.gdx.graphics.g2d.Sprite;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by Vlad on 17.02.2016.
 */
// меняет прозрачность

    // ИНТЕРПОЛИРУЕТ - один обект получает свойства другого
    /* инерпалирует флоат переменную в вал класса валуе
     используем это для планого перехода между экранама
     между сплешь скрин и гейм скрин
     вспышка и плавный переход прозрачности*/
public class SpriteAccessor implements TweenAccessor<Sprite> {

    public static final int ALPHA = 1;
    /* получаем значения которое нужно изменить в обьекте типа спрайт
    и сохнраяем их в масив с именем ретурн алуес
    так как значение одно мы можем сохранить его под первим индексом масива
     */
    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {
        switch (tweenType){
            case ALPHA:
                // вспишка
                returnValues[0] = target.getColor().a;
                return 1;
            default:
                return 0;
        }
    }
    // измененное твин энджин значение, передаем в метод сетВал.
    @Override
    public void setValues(Sprite target, int tweenType, float[] newValues) {
        switch (tweenType){
            case ALPHA:
                target.setColor(1, 1, 1, newValues[0]);
                break;
        }
    }
}
