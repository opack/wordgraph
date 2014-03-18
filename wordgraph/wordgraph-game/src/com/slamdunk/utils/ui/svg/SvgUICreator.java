package com.slamdunk.utils.ui.svg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.slamdunk.wordgraph.Options;

/**
 * Cr�e l'IHM � partir d'un descripteur json
 */
public class SvgUICreator {
	private static final Map<String, UISvgBuilder> BUILDERS;
	private Map<String, Actor> actorsMap;
	private Skin skin;
	
	static {
		// Charge les builders de widget
		BUILDERS = new HashMap<String, UISvgBuilder>();
		BUILDERS.put("com.badlogic.gdx.scenes.scene2d.ui.Label", new LabelSvgBuilder(Options.langCode));
		BUILDERS.put("com.badlogic.gdx.scenes.scene2d.ui.TextButton", new TextButtonSvgBuilder(Options.langCode));
		BUILDERS.put("com.badlogic.gdx.scenes.scene2d.ui.ScrollPane", new ScrollPaneSvgBuilder());
		BUILDERS.put("com.slamdunk.wordgraph.puzzle.graph.Graph", new GraphSvgBuilder());
		BUILDERS.put("com.badlogic.gdx.scenes.scene2d.ui.Image", new ImageSvgBuilder());
		BUILDERS.put("com.badlogic.gdx.scenes.scene2d.ui.Table", new TableSvgBuilder());
		BUILDERS.put("com.badlogic.gdx.scenes.scene2d.ui.Group", new GroupSvgBuilder());
	}
	
	public SvgUICreator(Skin skin) {
		this.skin = skin;
		actorsMap = new HashMap<String, Actor>();
	}

	/**
	 * Parse le fichier indiqu� et cr�e les widgets d�crits
	 * @param uiFile
	 */
	public void load(String uiFile) {
		// Parse le fichier et r�cup�re la racine
		XmlReader reader = new XmlReader();
		Element root = null;
		try {
			root = reader.parse(Gdx.files.internal(uiFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float screenHeight = root.getFloatAttribute("height");
		Element g = root.getChildByName("g");
		Array<Element> rects = g.getChildrenByName("rect");

		// Les valeurs globales sont renseign�es dans les attributs de la balise metadata
		Element globalValues = root.getChildByName("metadata");
		
		// Cr�e les widgets
		for (Element widget : rects) {
			// Parse la description du rect et lui ajoute les attributs d�finis
			populateAttributesFromDescription(widget); 
			
			// R�cup�re et configure le builder ad�quat
			UISvgBuilder builder = getBuilder(widget);
			if (builder == null) {
				throw new IllegalArgumentException("Aucun builder pour le widget " + widget);
			}
			builder.setScreenHeight(screenHeight);
			builder.setWidgetDescription(widget);
			builder.setGlobalValues(globalValues);
			
			// Cr�e l'objet indiqu�
			Actor actor = builder.build(skin);
			
			// Stocke l'objet dans la table
			actorsMap.put(actor.getName(), actor);
		}
	}

	/**
	 * Lit la balise <desc> de l'�l�ment et ajoute les cl�s/valeurs en tant qu'attributs
	 * de l'�l�ment.
	 * @param widget
	 */
	private void populateAttributesFromDescription(Element widget) {
		// R�cup�re la balise <desc>
		Element desc = widget.getChildByName("desc");
		if (desc == null) {
			// Aucune balise <desc> : les attributs sont donc normalement d�j� renseign�s
			return;
		}
		
		// Convertit la description en cl�s/valeurs
		String description = desc.getText();
		Pattern propertiesPattern = Pattern.compile("(.+)\\s*=\\s*(.+)\\s*");
		Matcher matcher = propertiesPattern.matcher(description);
		while (matcher.find()) {
			System.out.println("SVGUICreator.populateAttributesFromDescription() "+widget.getAttribute("id") + ":"+matcher.group(1)+"="+ matcher.group(2));
			widget.setAttribute(matcher.group(1), matcher.group(2));
		}
	}

	/**
	 * Retourne le builder capable de cr�er le widget indiqu�, d'apr�s sa classe
	 * @param widget
	 * @return
	 */
	private UISvgBuilder getBuilder(Element widget) {
		String clazz = widget.getAttribute("ui.class");
		UISvgBuilder builder = BUILDERS.get(clazz);
		if (builder == null) {
			throw new IllegalStateException("Class " + clazz + " has no associated UIWidgetBuilder.");
		}
		return builder;
	}

	/**
	 * Ajoute les widgets cr��s au stage indiqu�. Seuls les widgets
	 * qui n'ont pas de parents sont ajout�s ; ainsi si certains
	 * widgets ont �t� manuellement ins�r�s les uns dans les autres,
	 * cette filiation est conserv�e.
	 * @param stage
	 */
	public void populate(Stage stage) {
		for (Actor actor : actorsMap.values()) {
			if (!actor.hasParent()) {
				stage.addActor(actor);
			}
		}
	}

	public Actor getActor(String name) {
		return actorsMap.get(name);
	}
}
