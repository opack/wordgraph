package com.slamdunk.utils.graphics;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

/**
 * Assemble plusieurs textures pour en créer une nouvelle. Le travail avec des
 * clips s'effectue avec plusieurs clips contenant le m?me nombre de frames.
 */
public class DynamicTextureBuilder {
    private int maxWidth;
    private int maxHeight;
    private List<TextureRegion> textures;

    public DynamicTextureBuilder() {
        textures = new ArrayList<TextureRegion>();
    }

    public void addTexture(TextureRegion texture) {
        // Mise ? jour de la taille de la texture finale
        int width = texture.getRegionWidth();
        if (width > maxWidth) {
            maxWidth = width;
        }
        int height = texture.getRegionHeight();
        if (height > maxHeight) {
            maxHeight = height;
        }
        // Ajout de la texture
        textures.add(texture);
    }

    /**
     * Contruit la texture finale
     * @return
     */
    public Texture build() {
        FrameBuffer buffer = new FrameBuffer(Format.RGB565, maxWidth, maxHeight, false);
        buffer.begin();
        for (TextureRegion texture : textures) {
        }
        buffer.end();
        Texture result = buffer.getColorBufferTexture();
        buffer.dispose();
        return result;
    }
}