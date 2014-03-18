package com.slamdunk.wordgraph;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ajouté juste pour éviter l'erreur http://code.google.com/p/libgdx/issues/detail?id=1039
        //FreeTypeFontGenerator dummy;
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        cfg.useWakelock = true;
        initialize(new WordGraphGame(), cfg);
    }
}