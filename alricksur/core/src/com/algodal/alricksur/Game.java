package com.algodal.alricksur;

import com.algodal.alricksur.windows.WindowDefault;
import com.algodal.alricksur.windows.WindowPlay;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Game extends BaseGame {
	//GAME DIMENSION
	public final static float width = 100;
	public final static float height = (width * 16) / 9;
	public final static String gameName = "Alrick's Ur";
	
	//------------------- ASSETS -------------------//
	
	//IMAGES
	public final static String image_main = "atlas/alricksur.atlas";
		public final static String region_main_redfill = "redbox_filled";
		public final static String region_main_bluefill = "bluebox_filled";
		public final static String region_main_redoutline = "redbox_outline";
		public final static String region_main_blueoutline = "bluebox_outline";
	public final static String image_default = "atlas/default.atlas";
		public final static String region_default_box = "default";
		public final static String region_default_shader = "shader_default";
	
	//SOUNDS
	//
	
	//MUSICS
	//
		
	//SKINS
	//
	
	//EFFECTS
	//
	
	
	//-------------------- WINDOW STATES -------------------------//
		
	public final static int state_win_display = 0;
	public final static int state_win_pending = 1;
	public final static int state_win_transin = 2;
	public final static int state_win_transout = 3;
	
	//-------------------- SHADER PROGRAMS -------------------------//
	
	public final static String shader_default_vert = "shader/passthrough.vert.glsl";
	public final static String shader_default_frag = "shader/passthrough.frag.glsl";
	public final static String shader_transit_vert = "shader/transit.vert.glsl";
	public final static String shader_transit_frag = "shader/transit.frag.glsl";
		
	//--------------------------------------------------------------//
	
	//HELPER METHODS
	public final static void log(String a, String b) { Gdx.app.log(a, b); }
	public final static boolean contains(String[] l, String i) { 
		for(String o : l) if(o.equals(i)) return true; return false; }
	public final static boolean isImage(String filename) { return filename.contains("atlas/");}
	public final static boolean isSound(String filename) { return filename.contains("sound/");}
	public final static boolean isMusic(String filename) { return filename.contains("music/");}
	public final static boolean isSkin(String filename) { return filename.contains("skin/");}
	public final static boolean isEffect(String filename) { return filename.contains("effect/");}
	public final static FileHandle roFile(String filename) { return Gdx.files.internal(filename); }
	private final static void log(ShaderProgram sp) {
		if (!sp.isCompiled()) {
			log("shader program failed", sp.getLog());
			throw new GdxRuntimeException("shader program compile error");
		}
		 
		if (sp.getLog().length()!=0){
			log("shader program warning", sp.getLog());
		}
	}
	public final void log(String a, String b, int frames) {
		if(time == 0) {
			log(a, b);
		} else {
			final int totalFrames = (int)(time * 60);
			final int val = totalFrames % frames;
			if(val == 0) {
				log(a, b);
			}
		}
	}
	
	//LIBGDX CORE HANDLES
	public SpriteBatch sb;
	public FitViewport vp_fit;
	public ScreenViewport vp_screen;
	public OrthographicCamera cm_fit, cm_screen;
	public Stage st;
	public AssetManager am;
	public Color cc;
	
	//USEFUL DATA
	public float delta;
	public boolean done;
	public Unit unitWindow, unitScreen;
	private float time;
	
	//SYSTEM FIELDS
	private Window window /*reference active window*/, pendingWindow, oldWindow;
	private int windowStateID;
	
	//SHADER PROGRAM
	private ShaderProgram sp_transit;
		private float cutoff;
		private final static float smooth_size = 0.05f;
		private boolean drawCover;
	
	//WINDOWS
	public WindowDefault windowDefault; //the first window
	public WindowPlay windowPlay;
	
	@Override
	public void create () {
		//INITIALIZE LIBGDX CORE
		sb = new SpriteBatch();
		cm_fit = new OrthographicCamera();
		cm_screen = new OrthographicCamera();
		vp_fit = new FitViewport(width, height, cm_fit);
		vp_screen = new ScreenViewport(cm_screen);
		am = new AssetManager();
		cc = new Color(Color.BLACK);
		
		//LOAD DEFAULT ASSETS
		am.load(image_default, TextureAtlas.class);
		am.finishLoading();
		
		//INITIALIZE USEFUL DATA
		unitWindow = new UnitAdapter().w(width).h(height);
		unitScreen = new UnitAdapter();
		
		//SET UP SHADER PROGRAM
		ShaderProgram.pedantic = false;
		sp_transit = new ShaderProgram(
				roFile(shader_transit_vert),
				roFile(shader_transit_frag));
		log(sp_transit);
		
		//INITIALIZE ALL WINDOWS
		windowDefault = new WindowDefault();
		windowPlay = new WindowPlay();
		
		//SET THE NEW ACTIVE WINDOW
		//setWindow(windowPlay);
		window = windowDefault;
	}
	
	@Override
	public void resize(int width, int height) {
		vp_fit.update(width, height);
		vp_screen.update(width, height);
		unitScreen.w(width).h(height);
	}
	
	@Override
	public void dispose () {
		sb.dispose();
		am.dispose();
		sp_transit.dispose();
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(cc.r, cc.g, cc.b, cc.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		delta = Gdx.graphics.getDeltaTime();
		done = am.update();
		time += delta;
		if(time >= (Float.MAX_VALUE / 60)) time = 0;
		
		renderWindow();
		updateShader();
		renderTransition();
	}
	
	public void begin(ShaderProgram sp, Viewport vp) {
		vp.apply();
		sb.setShader(sp);
		sb.setProjectionMatrix(vp.getCamera().combined);
		sb.enableBlending();
		sb.begin();
	}
	
	public void begin(Viewport vp) {
		begin(null, vp);
	}
	
	public void begin(ShaderProgram sp) {
		begin(sp, vp_fit);
	}
	
	public void begin() {
		begin(null, vp_fit);
	}
	
	public void end() {
		sb.end();
		sb.disableBlending();
	}
	
	/** process is completed over time in different states **/
	public void setWindow(Window window) {
		this.window = this.window == null ? windowDefault : this.window;
		pendingWindow = window;
		windowStateID = state_win_pending;
	}
	
	private void renderWindow() {
		window.draw(this);
		
		if(windowStateID == state_win_display) {
			window.update(this);
			log("display | active", window.name, 60);
			log("display | pending", pendingWindow.name, 60);
		}
		
		if(windowStateID == state_win_pending) {
			drawCover = true;
			cutoff = 1.0f;
			loadOnly(window, pendingWindow);
			windowStateID = state_win_transin;
			log("pending | active", window.name, 60);
			log("pending | pending", pendingWindow.name, 60);
		}
		
		if(windowStateID == state_win_transin) {
			cutoff -= delta;
			
			if(cutoff <= 0) {
				if(done) {
					oldWindow = window;
					window = pendingWindow;
					pendingWindow = windowDefault;
					cutoff = 0;
					unLoadOnly(window, oldWindow);
					windowStateID = state_win_transout;
					log("done in transit | old", oldWindow.name);
					log("done in transit | active", window.name);
					log("done in transit | pending", pendingWindow.name);
				} else {
					//TODO display loading message
				}
			} else {
				log("in transit | active", window.name, 60);
				log("in transit | pending", pendingWindow.name, 60);
			}
		}
		
		if(windowStateID == state_win_transout) {
			cutoff += delta;
			
			if(cutoff >= 1) {
				cutoff = 1;
				drawCover = false;
				windowStateID = state_win_display;
				log("done out transit | active", window.name);
				log("done out transit | pending", pendingWindow.name);
			} else {
				log("out transit | active", window.name, 60);
				log("out transit | pending", pendingWindow.name, 60);
			}
		}
	}
	
	private void renderTransition() {
		if(drawCover) {
			begin(sp_transit, vp_screen);
			draw(image_default, region_default_shader, unitScreen);
			end();
		}
	}
	
	private void updateShader() {
		//TRANSITION SHADER
		sp_transit.begin();
		sp_transit.setUniformf("cutoff", cutoff);
		sp_transit.setUniformf("smooth_size", smooth_size);
		sp_transit.end();
	}
	
	public TextureRegion get(String filename, String region) {
		return am.get(filename, TextureAtlas.class).findRegion(region);
	}
	
	public void draw(String filename, String region, Unit unit) {
		sb.draw(get(filename, region), unit.left(), unit.bottom(), unit.w, unit.h);
	}
	
	private void loadOnly(Window active, Window pending) {
		final Array<String> toBeLoaded = new Array<String>();
		load_filterNew(toBeLoaded, active, pending);
		for(String i : toBeLoaded) { load_amLoad(i); log("loaded", i); }
		toBeLoaded.clear();
	}
	
	private void unLoadOnly(Window active, Window pending) {
		final Array<String> toBeUnLoaded = new Array<String>();
		load_filterNew(toBeUnLoaded, active, pending);
		for(String i : toBeUnLoaded) { load_amUnLoad(i); log("unloaded", i); }
		toBeUnLoaded.clear();
	}
	
	private static void load_filterNew(Array<String> arr, Window ref, Window obj) {
		for(String o : obj.assets) {
			if(!contains(ref.assets, o)) arr.add(o);
		}
	}
	
	private void load_amLoad(String i) {
		am.load(
				i, 
				isImage(i) ? TextureAtlas.class :
				isSound(i) ? Sound.class :
				isMusic(i) ? Music.class :
				isSkin(i) ? Skin.class :
				ParticleEffect.class
				);
	}
	
	private void load_amUnLoad(String i) {
		am.unload(i);
	}
}













