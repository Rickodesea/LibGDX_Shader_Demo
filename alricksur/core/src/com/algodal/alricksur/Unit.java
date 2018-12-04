package com.algodal.alricksur;

public abstract class Unit implements Renderable {
	//INPUT FIELDS
	public float x, y, w, h; //center x, center y, width, height
	
	//DIMENSION
	public float hh() { return h / 2; }
	public float hw() { return w / 2; }
	public float left() { return x - hw(); }
	public float right() { return x + hw(); }
	public float bottom() { return y - hh(); }
	public float top() { return y + hh(); }
	public Unit x(float x) { this.x = x; return this; }
	public Unit y(float y) { this.y = y; return this; }
	public Unit w(float w) { this.w = w; return this; }
	public Unit h(float h) { this.h = h; return this; }
}









