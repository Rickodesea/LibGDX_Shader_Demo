package com.algodal.alricksur.windows;

import com.algodal.alricksur.Game;
import com.algodal.alricksur.Window;
import com.algodal.alricksur.units.UnitTest;

public class WindowPlay extends Window {
	public UnitTest test;
	
	public WindowPlay() {
		name = "play";
		test = new UnitTest();
	}
	
	@Override
	public void draw(Game g) {
		test.draw(g);
	}
	
	@Override
	public void update(Game g) {
		test.update(g);
	}
}











