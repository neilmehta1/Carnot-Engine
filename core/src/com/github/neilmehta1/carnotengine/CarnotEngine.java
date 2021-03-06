package com.github.neilmehta1.carnotengine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.neilmehta1.carnotengine.screens.PlayScreen;

public class CarnotEngine extends Game {
	public SpriteBatch batch;
	public static final boolean playerMovesBlack = true;
	public static final boolean playerMovesWhite = true;
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
