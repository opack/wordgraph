package com.slamdunk.wordgraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.slamdunk.utils.PropertiesManager;

public class Assets {

	private static Map<String, Disposable> disposables;
	
	public static Skin uiSkin;
	public static Skin defaultPuzzleSkin;
	
	private static TextureAtlas uiAtlas;
	
//	public static TextureRegionDrawable grey_down;
//	public static TextureRegion ui_button;
//	public static TextureRegion ui_msgBox;
	public static TextureRegion puzzleDone;
	
	//public static Animation playerWalkingRightAnimation;

	public static float soundVolume;
	public static Sound stepsSound;
	
	// Musique de fond, instanciée à la demande
	public static float musicVolume;
	public static Music music;
	public static String mainMusic;
	public static String currentMusic = "";

	public static final float VIRTUAL_WIDTH = 30.0f;
	public static final float VIRTUAL_HEIGHT = 20.0f;
	
	public static float pixelDensity;

	public static boolean asBoolean(Properties properties, String name, boolean fallback) {
		String v = properties.getProperty(name);
		if (v == null) return fallback;
		return Boolean.parseBoolean(v);
	}

	public static float asFloat(Properties properties, String name, float fallback) {
		String v = properties.getProperty(name);
		if (v == null) return fallback;
		return Float.parseFloat(v);
	}

	public static int asInt(Properties properties, String name, int fallback) {
		String v = properties.getProperty(name);
		if (v == null) return fallback;
		return Integer.parseInt(v);
	}

	public static String asString(Properties properties, String name, String fallback) {
		String v = properties.getProperty(name);
		if (v == null) return fallback;
		return v;
	}

	private static float calculatePixelDensity () {
		FileHandle textureDir = Gdx.files.internal("textures");
		FileHandle[] availableDensities = textureDir.list();
		FloatArray densities = new FloatArray();
		for (int i = 0; i < availableDensities.length; i++) {
			try {
				float density = Float.parseFloat(availableDensities[i].name());
				densities.add(density);
			} catch (NumberFormatException ex) {
				// Ignore anything non-numeric, such as ".svn" folders.
			}
		}
		densities.shrink(); // Remove empty slots to get rid of zeroes.
		densities.sort(); // Now the lowest density comes first.
		//DBG DDEreturn CameraHelper.bestDensity(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, densities.items);
		return 24;
	}
	
	private static void createAnimations () {
		//playerWalkingRightAnimation = new Animation(PLAYER_FRAME_DURATION, Assets.playerWalkingRight1, Assets.playerWalkingRight2);
		//robotWalkingLeftAnimation = new Animation(ROBOT_FRAME_DURATION, robotLeft1, robotLeft2, robotLeft3, robotLeft4, robotLeft3,	robotLeft2);
	}

	public static void dispose() {
		for (Disposable disposable : disposables.values()) {
			disposable.dispose();
		}
		disposables.clear();
		music = null;
		currentMusic = "";
	}
	
	public static void disposeSkin(String name) {
		Disposable disposable = disposables.remove("skin-" + name);
		if (disposable != null) {
			disposable.dispose();
		}
	}

//	private static Sound[] loadSounds (String dir) {
//		// Sur desktop, files.internal ne sait pas récupérer un répertoire dans les
//		// assets, puisque tout le contenu se retrouve dans le classpath. Du coup
//		// c'est dur d'en parcourir un. Pour contourner ça, on fait un cas particulier
//		// dans le cas desktop pour aller regarder dans bin.
//		FileHandle dirHandle;
//		if (Gdx.app.getType() == ApplicationType.Android) {
//		   dirHandle = Gdx.files.internal("sounds/" + dir);
//		} else {
//		  // ApplicationType.Desktop ..
//		  dirHandle = Gdx.files.internal("./assets/sounds/" + dir);
//		}
//		
//		FileHandle[] fhs = dirHandle.list();
//		System.out.println("Assets.loadSounds() "+fhs.length);
//		List<Sound> sounds = new ArrayList<Sound>();
//		for (int i = 0; i < fhs.length; i++) {
//			String name = fhs[i].name();
//			// DDE On ne filtre pas sur les ogg
//			//if (name.endsWith(".ogg")) {
//				sounds.add(loadSound(dir + "/" + name));
//			//}
//		}
//		Sound[] result = new Sound[0];
//		return sounds.toArray(result);
//	}

	public static void load () {
		disposables = new HashMap<String, Disposable>();
		pixelDensity = calculatePixelDensity();
		loadTextures();
		createAnimations();
		loadFonts();
		loadSounds();
		loadSkins();
	}
	
	private static void loadSkins() {
		uiSkin = loadSkin("default-puzzle");
		defaultPuzzleSkin = uiSkin;//loadSkin("default-puzzle");

//		uiSkin.get("mainTitle", LabelStyle.class).font = mainTitleFont;
//		uiSkin.get("title", LabelStyle.class).font = titleFont;
//		uiSkin.get("text", LabelStyle.class).font = textFont;
//		uiSkin.get("riddle", LabelStyle.class).font = riddleFont;
//		uiSkin.get("suggestion", LabelStyle.class).font = suggestionFont;
//		for (TextButtonStyle style : uiSkin.getAll(TextButtonStyle.class).values()) {
//			style.font = textFont;
//		}
	}

	/**
	 * Charge la skin spécifiée. On s'attend à ce que la skin soit spécifiée
	 * dans le fichier skins/[name]/[name].json
	 * @param name
	 * @return
	 */
	public static Skin loadSkin(String name) {
		String id = "skin-" + name;
		if (disposables.containsKey(id)) {
			return (Skin)disposables.get(id);
		}
		Skin skin = new Skin(Gdx.files.internal("skins/" + name + "/" + name + ".json"));
		disposables.put(id, skin);
		return skin;
	}
	
	/**
	 * Charge la skin spécifiée en utilisant l'atlas spécifié.
	 * On s'attend à ce que la skin soit décrite dans le fichier skins/[name]/[name].json
	 * @param name
	 * @return
	 */
	public static Skin loadSkin(String name, TextureAtlas atlas) {
		String id = "skin-" + name;
		if (disposables.containsKey(id)) {
			return (Skin)disposables.get(id);
		}
		Skin skin = new Skin(Gdx.files.internal("skins/" + name + "/" + name + ".json"), atlas);
		disposables.put(id, skin);
		return skin;
	}
	
	private static TextureAtlas loadAtlas(String name) {
		String id = "atlas-" + name;
		if (disposables.containsKey(id)) {
			return (TextureAtlas)disposables.get(id);
		}
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("textures/" + name + ".pack"));
		disposables.put(id, atlas);
		return atlas;
	}

	private static void loadAtlases() {
		//uiAtlas = loadAtlas("ui");
	}
	
	private static BitmapFont loadFont(String subDir, String name, float fontScale) {
		String id = "font-" + name;
		if (disposables.containsKey(id)) {
			return (BitmapFont)disposables.get(id);
		}
		BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/" + subDir + "/" + name), false);
		font.setScale(fontScale);
		disposables.put(id, font);
		return font;
	}
	
	private static void loadFonts () {
		//String fontSubDir = "";// DDE DBG + (int)pixelDensity + "/";
		
		//characterFont.setScale(1.0f / pixelDensity);
		//characterFont = loadFont(fontSubDir, TEXT_FONT, 0.4f);
	}
	
	private static Sound loadSound (String filename) {
		String id = "sound-" + filename;
		if (disposables.containsKey(id)) {
			return (Sound)disposables.get(id);
		}
		Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + filename));
		disposables.put(id, sound);
		return sound;
	}
	
	private static void loadSounds () {
		// Effets sonores
		soundVolume = PropertiesManager.getFloat("config", "sounds.sfx.volume", 1.0f);
		//stepsSound = loadSound("steps.ogg");
		
		// Musiques
		musicVolume = PropertiesManager.getFloat("config", "sounds.music.volume", 0.5f);
		mainMusic = "";
	}

	public static TextureRegion loadTexture(String file) {
		String id = "texture-" + file;
		if (disposables.containsKey(id)) {
			return (TextureRegion)disposables.get(id);
		}
		Texture texture = new Texture(Gdx.files.internal("textures/" + file));
		TextureRegion region = new TextureRegion(texture);
		disposables.put(id, texture);
		return region;
	}

	private static TextureRegion loadTexture(TextureAtlas atlas, String regionName) {
		AtlasRegion region = atlas.findRegion(regionName);
		if (region == null) {
			throw new IllegalArgumentException("La région " + regionName + " est introuvable dans l'atlas.");
		}
		return region;
	}
	
	private static TextureRegion loadTexture(TextureAtlas atlas, String regionName, int index) {
		AtlasRegion region = atlas.findRegion(regionName, index);
		if (region == null) {
			throw new IllegalArgumentException("La région " + regionName + " ou son index " + index + " est introuvable dans l'atlas.");
		}
		return region;
	}

	private static void loadTextures () {
		loadAtlases();
		
		puzzleDone = loadTexture("puzzle_done.png");
		
		// Atlas ui
//		ui_button = loadTexture(uiAtlas, "button");
//		ui_msgBox = loadTexture(uiAtlas, "msgBox");
//		ui_button = loadTexture("msgBox.button.9.png");
//		ui_msgBox = loadTexture("msgBox.background.9.png");
		
		
	}
	
	public static boolean playMusic(String file) {
		// Si on cherche à jouer la même musique, rien à faire à part
		// s'assurer qu'elle est bien en train de tourner
		if (currentMusic.equals(file)) {
			if (!music.isPlaying()) {
				music.play();
			}
			return true;
		}
		
		
		// Arrêt de la musique en cours
		stopMusic();
		
		// S'il n'y a aucune musique à jouer, on ne joue rien
		if (file == null || file.isEmpty()) {
			return false;
		}
		
		// Lancement de la musque
		String id = "music-" + file;
		if (disposables.containsKey(id)) {
			music = (Music)disposables.get(id);
		} else {
			music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music/" + file));
		}
		music.setLooping(true);
		music.setVolume(musicVolume);
		music.play();
		currentMusic = file;
		
		// On s'assure que la musique sera libérée
		disposables.put(id, music);
		return true;
	}
	
	public static void playSound(Sound sound) {
		if (sound == null) {
			return;
		}
		sound.play(soundVolume);
	}

	public static void stopMusic() {
		if (music != null && music.isPlaying()) {
			music.stop();
		}
	}

	private static float toHeight (TextureRegion region) {
		return region.getRegionHeight() / pixelDensity;
	}
	
	private static float toWidth (TextureRegion region) {
		return region.getRegionWidth() / pixelDensity;
	}

	public static Sound getSound(String file) {
		return (Sound)disposables.get("sound-" + file);
	}
	
	public static TextureAtlas getAtlas(String name) {
		return (TextureAtlas)disposables.get("atlas-" + name);
	}
	
	public static Skin getSkin(String file) {
		return (Skin)disposables.get("skin-" + file);
	}
}
