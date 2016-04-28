package com.github.neilmehta96.carnotengine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.neilmehta96.carnotengine.screens.PlayScreen;

public class CarnotEngine extends Game {
	public SpriteBatch batch;
	public static final boolean playerMovesBlack = false;
	public static final int depthOfSearch = 6;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

}