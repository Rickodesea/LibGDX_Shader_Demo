package com.algodal.alricksur.windows;

import com.algodal.alricksur.Game;
import com.algodal.alricksur.Window;

public class WindowDefault extends Window {
	public WindowDefault() {
		name = "none";
	}
	
	@Override
	public void update(Game g) {
		g.setWindow(g.windowPlay);
	}

	@Override
	public void draw(Game g) {
		//
	}

}
