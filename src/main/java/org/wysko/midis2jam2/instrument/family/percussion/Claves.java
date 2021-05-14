/*
 * Copyright (C) 2021 Jacob Wysko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package org.wysko.midis2jam2.instrument.family.percussion;

import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.wysko.midis2jam2.Midis2jam2;
import org.wysko.midis2jam2.instrument.family.percussion.drumset.NonDrumSetPercussion;
import org.wysko.midis2jam2.instrument.family.percussive.Stick;
import org.wysko.midis2jam2.midi.MidiNoteOnEvent;
import org.wysko.midis2jam2.world.Axis;

import java.util.List;

import static com.jme3.scene.Spatial.CullHint.Dynamic;
import static org.wysko.midis2jam2.Midis2jam2.rad;

/**
 * The claves.
 */
public class Claves extends NonDrumSetPercussion {
	
	/**
	 * Contains the left clave.
	 */
	private final Node rightClaveNode = new Node();
	
	/**
	 * Contains the right clave.
	 */
	private final Node leftClaveNode = new Node();
	
	/**
	 * Instantiates claves.
	 *
	 * @param context the context
	 * @param hits    the hits
	 */
	public Claves(Midis2jam2 context,
	              List<MidiNoteOnEvent> hits) {
		super(context, hits);
		this.hits = hits;
		
		Spatial rightClave = context.loadModel("Clave.obj", "Clave.bmp");
		rightClave.setLocalTranslation(2.5f, 0, 0);
		rightClave.setLocalRotation(new Quaternion().fromAngles(0, rad(20), 0));
		rightClaveNode.attachChild(rightClave);
		rightClaveNode.setLocalTranslation(-1, 0, 0);
		
		Spatial leftClave = context.loadModel("Clave.obj", "Clave.bmp");
		leftClave.setLocalTranslation(-2.5f, 0, 0);
		leftClave.setLocalRotation(new Quaternion().fromAngles(0, -rad(20), 0));
		leftClaveNode.attachChild(leftClave);
		
		instrumentNode.setLocalTranslation(-12, 42.3f, -48.4f);
		instrumentNode.setLocalRotation(new Quaternion().fromAngles(rad(90), rad(90), 0));
		instrumentNode.attachChild(rightClaveNode);
		instrumentNode.attachChild(leftClaveNode);
	}
	
	@Override
	public void tick(double time, float delta) {
		super.tick(time, delta);
		
		Stick.StickStatus status = Stick.handleStick(context, rightClaveNode, time, delta, hits, Stick.STRIKE_SPEED, Stick.MAX_ANGLE, Axis.X);
		rightClaveNode.setCullHint(Dynamic);
		leftClaveNode.setLocalRotation(new Quaternion().fromAngles(-status.getRotationAngle(), 0, 0));
	}
}
