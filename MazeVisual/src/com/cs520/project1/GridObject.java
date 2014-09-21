package com.cs520.project1;
import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * Template for any generic object that can be placed into the environment.
 */
public abstract class GridObject {
	private Sprite sprite;
	private Texture texture;
	private Grid.ObjectType type;
	
	public GridObject(Grid.ObjectType type, String imagePath) {
		texture = new Texture(Gdx.files.internal(imagePath));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		sprite = new Sprite(texture);
		this.type = type;
	}
	
	public void render(SpriteBatch batch, Point drawPos, Point drawSize) {
		sprite.setSize(drawSize.x, drawSize.y);
		sprite.setOrigin(0, 0);
		sprite.setPosition(drawPos.x, drawPos.y);
		sprite.draw(batch);
	}
	
	public Grid.ObjectType getType() {
		return type;
	}
}
