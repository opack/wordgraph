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
 * Classe fournissant des fonctionnalit�s suppl�mentaires � une animation,
 * comme (en autres) le param�trage via un fichier de propri�t�s et la gestion du son.
 */
public class Clip extends Animation {
    /**
     * Indique quelle proportion de l'espace restant doit ?tre
     * laiss�e ? gauche du dessin de la frame.
     * Mettre 0.0 indique qu'il faut aligner la frame ? gauche de
     * la zone de dessin du clip. Mettre 1.0 indique qu'il faut
     * aligner ? droite.
     */
    public float alignX;
    /**
     * Indique quelle proportion de l'espace restant doit ?tre
     * laiss�e en bas du dessin de la frame.
     * Mettre 0.0 indique qu'il faut aligner la frame en bas de
     * la zone de dessin du clip. Mettre 1.0 indique qu'il faut
     * aligner en haut.
     */
    public float alignY;
    /**
     * Zone de dessin dans laquelle seront dessin�es les frames
     */
    public Rectangle drawArea;
    /**
     * Indique si la frame doit ?tre invers�e horizontalement
     */
    public boolean flipH;
    /**
     * Indique si la frame doit ?tre invers�e verticalement
     */
    public boolean flipV;
    /**
     * H�r�sie ! Ce champ n'existe que parce qu'il n'est pas possible d'acc�der au champ Animation.keyFrames !
     */
    private TextureRegion[] keyFrames;
    /**
     * Tableau avec les Runnables ? ex�cuter ? la fin de chaque keyFrame
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
     * Facteur d'�chelle ? appliquer en largeur
     */
    public float scaleX;
    /**
     * Facteur d'�chelle ? appliquer en hauteur
     */
    public float scaleY;
    /**
     * Indique s'il faut �tirer les trames pour qu'elles
     * occupent toute la largeur de l'espace de dessin.
     * Si true, la valeur de scaleX est ignor�e.
     */
    public boolean stretchX;
    /**
     * Indique s'il faut �tirer les trames pour qu'elles
     * occupent toute la hauteur de l'espace de dessin.
     * Si true, la valeur de scaleY est ignor�e.
     */
    public boolean stretchY;
    /**
     * Horloge permettant de savoir o? on en est dans l'animation
     */
    public float stateTime;
    /**
     * Index de la derni?re image affich�e (et donc dont le runnable a �t� ex�cut�)
     */
    private int lastKeyFrameIndex;
    /**
     * Sprite utilis� pour afficher et g�rer la g�om�trie du clip
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
     * Initialise le clip avec des valeurs par d�faut
     * @param frameCount
     */
    private void init(TextureRegion[] keyFrames) {
        this.keyFrames = keyFrames;
        keyFrameRunnables = new Runnable[keyFrames.length];

        // Pas de mise ? l'�chelle
        scaleX = 1.0f;
        scaleY = 1.0f;

        // Pas de stretch
        stretchX = false;
        stretchY = false;

        // Par d�faut, pas de flip
        flipH = false;
        flipV = false;

        // Alignement en bas ? gauche
        alignX = 0.0f;
        alignY = 0.0f;

        // Pas de d�calage
        offsetX = 0.0f;
        offsetY = 0.0f;

        // Animation au d�but
        sprite = new Sprite();
        setStateTime(0);

        // Taille de la zone d'affichage inconnue, initialis�e ?
        // la taille d'une frame
        drawArea = new Rectangle(0, 0, sprite.getRegionWidth(), sprite.getRegionHeight());
    }

    public boolean isFinished() {
        return isAnimationFinished(stateTime);
    }

    /**
     * Dessine la frame courante et r�alise l'actions associ�e
     * ? cette frame (son...)
     */
    public void play(float delta, SpriteBatch batch) {
        // Choisit l'image en fonction du temps �coul�
        update(delta);

        // Dessine l'image
        playCurrentFrame(batch);
    }

    /**
     * Dessine la frame courante et r�alise l'actions associ�e
     * ? cette frame (son...). Utilis�e apr?s update(delta) pour
     * d�couper en 2 �tapes le dessin du clip, au lieu de tout
     * faire en une fois avec play(float, SpriteBatch).
     */
    public void playCurrentFrame(SpriteBatch batch) {
        // D�termine la taille du dessin
        float frameWidth = sprite.getRegionWidth() * scaleX;
        float frameHeight = sprite.getRegionHeight() * scaleY;

        // Prise en compte du stretch
        if (stretchX) {
            frameWidth = drawArea.width;
        }
        if (stretchY) {
            frameHeight = drawArea.height;
        }

        // D�termine la position de la frame
        float posX = drawArea.x + (drawArea.width - frameWidth) * alignX + offsetX;
        float posY = drawArea.y + (drawArea.height - frameHeight) * alignY + offsetY;

        // Fixe les dimensions gr�ce aux calculs pr�c�dents
        sprite.setBounds(posX, posY, frameWidth, frameHeight);

        // D�termine les flips
        sprite.flip(flipH, flipV);

        // Dessine la frame
        sprite.draw(batch);

        // R�alise l'action associ�e si on vient de changer de frame
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
     * Replace l'animation au d�but
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
     * Choisit l'image en fonction du temps indiqu�. Peut ?tre appel�
     * avec playCurrentFrame(SpriteBatch) pour d�couper en 2 �tapes le dessin
     * du clip. Utile notamment pour r�utiliser le m?me clip plusieurs fois.
     * @param stateTime
     */
    public void setStateTime(float stateTime) {
        // Mise ? jour de l'horloge
        this.stateTime = stateTime;
        // R�cup?re la trame courante
        sprite.setRegion(getKeyFrame(stateTime, true));
    }

    /**
     * Choisit l'image en fonction du temps �coul�. Peut ?tre appel�
     * avec playCurrentFrame(SpriteBatch) pour d�couper en 2 �tapes le dessin
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