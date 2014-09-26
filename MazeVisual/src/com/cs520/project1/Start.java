package com.cs520.project1;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cs520.project1.Main;

public class Start {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Maze Visualizer";
		cfg.useGL20 = false;
		cfg.width = 960;
		cfg.height = 768;
		
		new LwjglApplication(new Main(), cfg);
	}
}
