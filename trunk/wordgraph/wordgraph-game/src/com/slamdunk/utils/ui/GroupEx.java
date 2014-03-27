package com.slamdunk.utils.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Ce group permet quelques petites choses de plus
 * que le Group de LibGDX, comme par exemple
 * retourner une taille cohérente avec les acteurs
 * contenus dedans.
 */
public class GroupEx extends Group  {

    private boolean isSizeValid;

    @Override
    protected void childrenChanged() {
        super.childrenChanged();
        // Mise ? jour de la taille du groupe lors du prochain getWidth() ou getHeight()
        isSizeValid = false;
    }

    @Override
    public float getHeight() {
        if (!isSizeValid) {
            updateBounds();
        }
        return super.getHeight();
    }

    @Override
    public float getWidth() {
        if (!isSizeValid) {
            updateBounds();
        }
        return super.getWidth();
    }

    @Override
    public float getX() {
        if (!isSizeValid) {
            updateBounds();
        }
        return super.getX();
    }

    @Override
    public float getY() {
        if (!isSizeValid) {
            updateBounds();
        }
        return super.getY();
    }

    protected void updateBounds() {
        final int count = getChildren().size;
        Actor[] children = getChildren().items;
        if (count == 0) {
            return;
        }
        Actor child = children[0];
        float minX = child.getX();
        float maxX = minX + child.getWidth();
        float minY = child.getY();
        float maxY = minY + child.getHeight();

        float curMinX = 0;
        float curMaxX = 0;
        float curMinY = 0;
        float curMaxY = 0;

        for (int cur = 1; cur < count; cur++) {
            child = children[cur];
            curMinX = child.getX();
            curMaxX = curMinX + child.getWidth();
            curMinY = child.getY();
            curMaxY = curMinY + child.getHeight();

            if (curMinX < minX) {
                minX = curMinX;
            } else if (curMaxX > maxX) {
                maxX = curMaxX;
            }

            if (curMinY < minY) {
                minY = curMinY;
            } else if (curMaxY > maxY) {
                maxY = curMaxY;
            }
        }

        setBounds(minX, minY, maxX - minX, maxY - minY);
        isSizeValid = true;
    }
}