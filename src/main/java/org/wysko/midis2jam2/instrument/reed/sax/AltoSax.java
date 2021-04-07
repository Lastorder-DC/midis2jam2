package org.wysko.midis2jam2.instrument.reed.sax;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import org.wysko.midis2jam2.Midis2jam2;
import org.wysko.midis2jam2.instrument.Clone;
import org.wysko.midis2jam2.instrument.PressedKeysFingeringManager;
import org.wysko.midis2jam2.midi.MidiChannelSpecificEvent;

import java.util.List;

import static org.wysko.midis2jam2.Midis2jam2.rad;

/**
 * The alto saxophone.
 */
public class AltoSax extends Saxophone {
	
	
	public static final PressedKeysFingeringManager FINGERING_MANAGER = PressedKeysFingeringManager.from(AltoSax.class);
	
	/**
	 * The amount to stretch the bell of this instrument by.
	 */
	private final static float STRETCH_FACTOR = 0.65f;
	
	/**
	 * Constructs an alto saxophone.
	 *
	 * @param context context to midis2jam2
	 * @param events  all events that pertain to this instance of an alto saxophone
	 */
	public AltoSax(Midis2jam2 context, List<MidiChannelSpecificEvent> events) throws ReflectiveOperationException {
		super(context, events, AltoSaxClone.class, FINGERING_MANAGER);
		
		groupOfPolyphony.setLocalTranslation(-32, 46.5f, -50);
		
	}
	
	/**
	 * Implements {@link Clone}, as alto saxophone clones.
	 */
	public class AltoSaxClone extends SaxophoneClone {
		
		/**
		 * Instantiates a new Alto sax clone.
		 */
		public AltoSaxClone() {
			super(AltoSax.this, STRETCH_FACTOR);
			
			Material shinyHornSkin = context.reflectiveMaterial("Assets/HornSkin.bmp");
			Material black = new Material(context.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
			black.setColor("Color", ColorRGBA.Black);
			
			this.body = context.getAssetManager().loadModel("Assets/AltoSaxBody.fbx");
			this.bell.attachChild(context.getAssetManager().loadModel("Assets/AltoSaxHorn.obj"));
			
			Node bodyNode = ((Node) body);
			
			bodyNode.getChild(0).setMaterial(shinyHornSkin);
			bodyNode.getChild(1).setMaterial(black);
			bell.setMaterial(shinyHornSkin);
			
			modelNode.attachChild(this.body);
			modelNode.attachChild(bell);
			bell.move(0, -22, 0); // Move bell down to body
			
			animNode.setLocalTranslation(0, 0, 20);
			highestLevel.setLocalRotation(new Quaternion().fromAngles(rad(13), rad(75), 0));
		}
	}
}
