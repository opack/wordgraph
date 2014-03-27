package com.slamdunk.utils.graphics.clip;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.slamdunk.utils.graphics.point.Point;

/**
 * Classe fournissant des fonctionnalités supplémentaires à une animation,
 * comme (en autres) le paramétrage via un fichier de propriétés et la gestion du son.
 */
public class Clip extends Animation {
    /**
     * Indique quelle proportion de l'espace restant doit ?tre
     * laissée ? gauche du dessin de la frame.
     * Mettre 0.0 indique qu'il faut aligner la frame ? gauche de
     * la zone de dessin du clip. Mettre 1.0 indique qu'il faut
     * aligner ? droite.
     */
    public float alignX;
    /**
     * Indique quelle proportion de l'espace restant doit ?tre
     * laissée en bas du dessin de la frame.
     * Mettre 0.0 indique qu'il faut aligner la frame en bas de
     * la zone de dessin du clip. Mettre 1.0 indique qu'il faut
     * aligner en haut.
     */
    public float alignY;
    /**
     * Zone de dessin dans laquelle seront dessinées les frames
     */
    public Rectangle drawArea;
    /**
     * Indique si la frame doit ?tre inversée horizontalement
     */
    public boolean flipH;
    /**
     * Indique si la frame doit ?tre inversée verticalement
     */
    public boolean flipV;
    /**
     * Hérésie ! Ce champ n'existe que parce qu'il n'est pas possible d'accéder au champ Animation.keyFrames !
     */
    private TextureRegion[] keyFrames;
    /**
     * Tableau avec les Runnables ? exécuter ? la fin de chaque keyFrame
     */
    private Runnable[] keyFrameRunnables;
    /**
     * Indique un offset par rapport ? la gauche, en pixels.
     */
    public float offsetX;
    /**
     * Indique un offset en pixels par rapport au bas, en pixels.
     */
    public float offsetY;
    /**
     * Facteur d'échelle ? appliquer en largeur
     */
    public float scaleX;
    /**
     * Facteur d'échelle ? appliquer en hauteur
     */
    public float scaleY;
    /**
     * Indique s'il faut étirer les trames pour qu'elles
     * occupent toute la largeur de l'espace de dessin.
     * Si true, la valeur de scaleX est ignorée.
     */
    public boolean stretchX;
    /**
     * Indique s'il faut étirer les trames pour qu'elles
     * occupent toute la hauteur de l'espace de dessin.
     * Si true, la valeur de scaleY est ignorée.
     */
    public boolean stretchY;
    /**
     * Horloge permettant de savoir o? on en est dans l'animation
     */
    public float stateTime;
    /**
     * Index de la derni?re image affichée (et donc dont le runnable a été exécuté)
     */
    private int lastKeyFrameIndex;
    /**
     * Sprite utilisé pour afficher et gérer la géométrie du clip
     */
    private Sprite sprite;
    /**
     * Points d'ancrage utilisables pour aligner des clips
     */
    private Map<String, Point[]> anchors;

    public Clip(Clip clip) {
        super(clip.frameDuration, clip.keyFrames);
        this.alignX = clip.alignX;
        this.alignY = clip.alignY;
        if (clip.anchors != null) {
            anchors = new HashMap<String, Point[]>(anchors);
        }
        this.drawArea = new Rectangle(clip.drawArea);
        this.flipH = clip.flipH;
        this.flipV = clip.flipV;
        this.keyFrameRunnables = Arrays.copyOf(clip.keyFrameRunnables, clip.keyFrameRunnables.length);
        this.offsetX = clip.offsetX;
        this.offsetY = clip.offsetY;
        this.scaleX = clip.scaleX;
        this.scaleY = clip.scaleY;
        this.sprite = new Sprite(clip.sprite);
        this.stretchX = clip.stretchX;
        this.stretchY = clip.stretchY;
        this.stateTime = clip.stateTime;
        this.lastKeyFrameIndex = clip.lastKeyFrameIndex;
    }

    public Clip(float frameDuration, TextureRegion[] keyFrames) {
        super(frameDuration, keyFrames);
        init(keyFrames);
    }

    public int getFrameCount() {
        return keyFrameRunnables.length;
    }

    public float getCurrentFrameHeight() {
        return sprite.getRegionHeight();
    }

    public float getCurrentFrameWidth() {
        return sprite.getRegionWidth();
    }

    /**
     * Initialise le clip avec des valeurs par défaut
     * @param frameCount
     */
    private void init(TextureRegion[] keyFrames) {
        this.keyFrames = keyFrames;
        keyFrameRunnables = new Runnable[keyFrames.length];

        // Pas de mise ? l'échelle
        scaleX = 1.0f;
        scaleY = 1.0f;

        // Pas de stretch
        stretchX = false;
        stretchY = false;

        // Par défaut, pas de flip
        flipH = false;
        flipV = false;

        // Alignement en bas ? gauche
        alignX = 0.0f;
        alignY = 0.0f;

        // Pas de décalage
        offsetX = 0.0f;
        offsetY = 0.0f;

        // Animation au début
        sprite = new Sprite();
        setStateTime(0);

        // Taille de la zone d'affichage inconnue, initialisée ?
        // la taille d'une frame
        drawArea = new Rectangle(0, 0, sprite.getRegionWidth(), sprite.getRegionHeight());
    }

    public boolean isFinished() {
        return isAnimationFinished(stateTime);
    }

    /**
     * Dessine la frame courante et réalise l'actions associée
     * ? cette frame (son...)
     */
    public void play(float delta, SpriteBatch batch) {
        // Choisit l'image en fonction du temps écoulé
        update(delta);

        // Dessine l'image
        playCurrentFrame(batch);
    }

    /**
     * Dessine la frame courante et réalise l'actions associée
     * ? cette frame (son...). Utilisée apr?s update(delta) pour
     * découper en 2 étapes le dessin du clip, au lieu de tout
     * faire en une fois avec play(float, SpriteBatch).
     */
    public void playCurrentFrame(SpriteBatch batch) {
        // Détermine la taille du dessin
        float frameWidth = sprite.getRegionWidth() * scaleX;
        float frameHeight = sprite.getRegionHeight() * scaleY;

        // Prise en compte du stretch
        if (stretchX) {
            frameWidth = drawArea.width;
        }
        if (stretchY) {
            frameHeight = drawArea.height;
        }

        // Détermine la position de la frame
        float posX = drawArea.x + (drawArea.width - frameWidth) * alignX + offsetX;
        float posY = drawArea.y + (drawArea.height - frameHeight) * alignY + offsetY;

        // Fixe les dimensions grâce aux calculs précédents
        sprite.setBounds(posX, posY, frameWidth, frameHeight);

        // Détermine les flips
        sprite.flip(flipH, flipV);

        // Dessine la frame
        sprite.draw(batch);

        // Réalise l'action associée si on vient de changer de frame
        final int index = getKeyFrameIndex(stateTime);
        if (lastKeyFrameIndex != index) {
            lastKeyFrameIndex = index;
            final Runnable runnable = keyFrameRunnables[index];
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    /**
     * Replace l'animation au début
     */
    public void rewind() {
        stateTime = 0;
    }

    /**
     * Raccourci vers setKeyFrameRunnable() pour ajouter le runnable
     * sur la premi?re frame
     * @param runnable
     */
    public void setFirstKeyFrameRunnable(Runnable runnable) {
        setKeyFrameRunnable(0, runnable);
    }

    public void setKeyFrameRunnable(int frame, Runnable runnable) {
        if (frame > -1 && frame < keyFrameRunnables.length) {
            keyFrameRunnables[frame] = runnable;
        }
    }

    /**
     * Raccourci vers setKeyFrameRunnable() pour ajouter le runnable
     * sur la derni?re frame
     * @param runnable
     */
    public void setLastKeyFrameRunnable(Runnable runnable) {
        setKeyFrameRunnable(keyFrameRunnables.length - 1, runnable);
    }

    /**
     * Choisit l'image en fonction du temps indiqué. Peut ?tre appelé
     * avec playCurrentFrame(SpriteBatch) pour découper en 2 étapes le dessin
     * du clip. Utile notamment pour réutiliser le m?me clip plusieurs fois.
     * @param stateTime
     */
    public void setStateTime(float stateTime) {
        // Mise ? jour de l'horloge
        this.stateTime = stateTime;
        // Récup?re la trame courante
        sprite.setRegion(getKeyFrame(stateTime, true));
    }

    /**
     * Choisit l'image en fonction du temps écoulé. Peut ?tre appelé
     * avec playCurrentFrame(SpriteBatch) pour découper en 2 étapes le dessin
     * du clip, au lieu de tout faire en une fois avec play(float, SpriteBatch).
     * @param delta
     */
    public void update(float delta) {
        setStateTime(stateTime + delta);
    }

    public void rotate(float degrees) {
        sprite.rotate(degrees);
    }
}