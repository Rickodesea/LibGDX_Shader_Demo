package com.algodal.alricksur.units;

import static com.algodal.alricksur.Game.image_default;
import static com.algodal.alricksur.Game.region_default_box;

import com.algodal.alricksur.Game;
import com.algodal.alricksur.Unit;

public class UnitTest extends Unit {
	
	public UnitTest() {
		w(h(40).h);
	}
	
	@Override
	public void draw(Game g) {
		g.begin();
		g.draw(image_default, region_default_box, this);
		g.end();
	}

	@Override
	public void update(Game g) {
		// TODO Auto-generated method stub
	}
}


















