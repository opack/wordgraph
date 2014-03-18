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
 * Crée l'IHM à partir d'un descripteur json
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
	 * Parse le fichier indiqué et crée les widgets décrits
	 * @param uiFile
	 */
	public void load(String uiFile) {
		// Parse le fichier et récupère la racine
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

		// Les valeurs globales sont renseignées dans les attributs de la balise metadata
		Element globalValues = root.getChildByName("metadata");
		
		// Crée les widgets
		for (Element widget : rects) {
			// Parse la description du rect et lui ajoute les attributs définis
			populateAttributesFromDescription(widget); 
			
			// Récupère et configure le builder adéquat
			UISvgBuilder builder = getBuilder(widget);
			if (builder == null) {
				throw new IllegalArgumentException("Aucun builder pour le widget " + widget);
			}
			builder.setScreenHeight(screenHeight);
			builder.setWidgetDescription(widget);
			builder.setGlobalValues(globalValues);
			
			// Crée l'objet indiqué
			Actor actor = builder.build(skin);
			
			// Stocke l'objet dans la table
			actorsMap.put(actor.getName(), actor);
		}
	}

	/**
	 * Lit la balise <desc> de l'élément et ajoute les clés/valeurs en tant qu'attributs
	 * de l'élément.
	 * @param widget
	 */
	private void populateAttributesFromDescription(Element widget) {
		// Récupère la balise <desc>
		Element desc = widget.getChildByName("desc");
		if (desc == null) {
			// Aucune balise <desc> : les attributs sont donc normalement déjà renseignés
			return;
		}
		
		// Convertit la description en clés/valeurs
		String description = desc.getText();
		Pattern propertiesPattern = Pattern.compile("(.+)\\s*=\\s*(.+)\\s*");
		Matcher matcher = propertiesPattern.matcher(description);
		while (matcher.find()) {
			System.out.println("SVGUICreator.populateAttributesFromDescription() "+widget.getAttribute("id") + ":"+matcher.group(1)+"="+ matcher.group(2));
			widget.setAttribute(matcher.group(1), matcher.group(2));
		}
	}

	/**
	 * Retourne le builder capable de créer le widget indiqué, d'après sa classe
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
	 * Ajoute les widgets créés au stage indiqué. Seuls les widgets
	 * qui n'ont pas de parents sont ajoutés ; ainsi si certains
	 * widgets ont été manuellement insérés les uns dans les autres,
	 * cette filiation est conservée.
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
