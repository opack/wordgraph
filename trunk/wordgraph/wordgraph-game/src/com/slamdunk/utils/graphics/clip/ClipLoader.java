package com.slamdunk.utils.graphics.clip;

import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.slamdunk.utils.PropertiesEx;
import com.slamdunk.wordgraph.Assets;

/**
 * Classe chargée de lire un fichier properties décrivant un clip
 * et de créer un objet Clip à partir de ces informations.
 * Le fichier peut avoir les propriétés suivantes :
 *
 */
public class ClipLoader {
	// TODO Faire un cache pour retourner les męmes Textures et TextureRegions si la męme sheet est chargée plusieurs fois
    public static Clip createClip(FileHandle spritesFile, int frameCols, int frameRows, float frameDuration) {
        Texture sheet = new Texture(spritesFile);
        return createClip(sheet, frameCols, frameRows, frameDuration);
    }

    public static Clip createClip(TextureAtlas atlas, String regionName, float frameDuration) {
        if (atlas == null) {
            return null;
        }
        Array<AtlasRegion> regions = atlas.findRegions(regionName);
        if (regions == null) {
            return null;
        }
        TextureRegion[] frames = new TextureRegion[regions.size];
        for (int index = 0; index < regions.size; index++) {
            frames[index] = regions.get(index);
        }
        return new Clip(frameDuration, frames);
    }

    public static Clip createClip(Texture spriteSheet, int frameCols, int frameRows, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(
                spriteSheet,
                spriteSheet.getWidth() / frameCols,
                spriteSheet.getHeight() / frameRows);
        TextureRegion[] frames = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        return new Clip(frameDuration, frames);
    }
    
	public static Clip loadClip(AssetManager assets, String clipProperties) {
        // Chargement du fichier de propriétés
        PropertiesEx properties = new PropertiesEx();
        FileHandle fh = Gdx.files.internal("clips/" + clipProperties);
        InputStream inStream = fh.read();
        try {
            properties.load(inStream);
            inStream.close();
        } catch (IOException e) {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ex) {
                }
            }
        }

        // Création du clip
        Clip clip = null;
        if (properties.containsKey("spriteSheet.file")) {
            clip = createClip(
                Gdx.files.internal(properties.getProperty("spriteSheet.file")),
                properties.getIntegerProperty("spriteSheet.nbCols", 1),
                properties.getIntegerProperty("spriteSheet.nbRows", 1),
                properties.getFloatProperty("frameDuration", 1.0f));
        } else {
            clip = createClip(
        		assets.get(properties.getProperty("spriteSheet.atlas"), TextureAtlas.class),
                properties.getProperty("spriteSheet.region"),
                properties.getFloatProperty("frameDuration", 1/24));
        }

        // Initialisation du clip
        clip.alignX = properties.getFloatProperty("align.x", 0.0f);
        clip.alignY = properties.getFloatProperty("align.y", 0.0f);
        clip.flipH = properties.getBooleanProperty("flip.h", false);
        clip.flipV = properties.getBooleanProperty("flip.v", false);
        clip.offsetX = properties.getFloatProperty("offset.x", 0.0f);
        clip.offsetY = properties.getFloatProperty("offset.y", 0.0f);
        clip.scaleX = properties.getFloatProperty("scale.x", 1.0f);
        clip.scaleY = properties.getFloatProperty("scale.y", 1.0f);
        clip.stretchX = properties.getBooleanProperty("stretch.x", false);
        clip.stretchY = properties.getBooleanProperty("stretch.y", false);
        clip.setPlayMode(properties.getIntegerProperty("playMode", Animation.LOOP));

        // Chargement des sons à jouer
        for (int frame = 0; frame < clip.getFrameCount(); frame++) {
            String soundFile = properties.getProperty("soundOnFrame." + frame, "");
            final Sound sound = assets.get(soundFile, Sound.class);
            if (sound != null) {
                clip.setKeyFrameRunnable(frame, new Runnable(){
                    @Override
                    public void run() {
                        Assets.playSound(sound);
                    }
                });
            }
        }

        // Chargement des runnables
        // TODO...
        return clip;
    }
}
