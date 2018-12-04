package com.algodal.alricksur;

public abstract class Window implements Renderable {
	private final static String nameValue = "abstract window";
	
	public final String[] assets;
	public String name;

	public Window(String...assets) {
		this.assets = assets==null ? new String[]{} : assets;
		name = nameValue;
	}
	
	
}














