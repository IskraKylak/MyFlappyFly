package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.loader.ResourseLoader;
import com.mygdx.game.screens.SplashScreen;

public class FlyGame extends Game {

	// загружаем ресурсы
	@Override
	public void create () {
		ResourseLoader.load();
		setScreen(new SplashScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		ResourseLoader.dispose();
	}
}
