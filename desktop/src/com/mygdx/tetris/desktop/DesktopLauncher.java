package com.mygdx.tetris.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.tetris.SimpleTetrisGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Simple Tetris");
		//config.setWindowedMode(416, 480);
		new Lwjgl3Application(new SimpleTetrisGame(), config);
	}
}
