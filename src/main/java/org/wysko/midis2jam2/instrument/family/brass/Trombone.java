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

package org.wysko.midis2jam2.instrument.family.brass;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.jetbrains.annotations.NotNull;
import org.wysko.midis2jam2.Midis2jam2;
import org.wysko.midis2jam2.instrument.MonophonicInstrument;
import org.wysko.midis2jam2.instrument.clone.Clone;
import org.wysko.midis2jam2.midi.MidiChannelSpecificEvent;
import org.wysko.midis2jam2.midi.NotePeriod;
import org.wysko.midis2jam2.world.Axis;

import java.util.List;

import static org.wysko.midis2jam2.Midis2jam2.rad;

/**
 * The trombone animates by moving a slide on the instrument. The slide position can be determined by calculating {@code
 * note % 7} since there are only 7 slide positions. To be implemented: the slide will slowly move to the next position
 * if there is time for it to move.
 */
public class Trombone extends MonophonicInstrument {
	
	/**
	 * Instantiates a new Trombone.
	 *
	 * @param context   the context to the main class
	 * @param eventList the event list
	 */
	public Trombone(@NotNull Midis2jam2 context,
	                @NotNull List<MidiChannelSpecificEvent> eventList) throws ReflectiveOperationException {
		super(context, eventList, TromboneClone.class, null);
	}
	
	@Override
	protected void moveForMultiChannel(float delta) {
		offsetNode.setLocalTranslation(0, 10f * indexForMoving(delta), 0);
	}
	
	/**
	 * A single trombone.
	 */
	public class TromboneClone extends Clone {
		
		/**
		 * The slide.
		 */
		private final Spatial slide;
		
		/**
		 * Instantiates a new Trombone clone.
		 */
		public TromboneClone() {
			super(Trombone.this, 0.1f, Axis.X);
			
			/* Load and attach trombone */
			Spatial body = context.loadModel("Trombone.fbx", "HornSkin.bmp", Midis2jam2.MatType.REFLECTIVE, 0.9f);
			modelNode.attachChild(body);
			
			/* Set horn skin grey material */
			((Node) body).getChild(1).setMaterial(context.reflectiveMaterial("Assets/HornSkinGrey.bmp"));
			
			/* Load and attach slide */
			slide = context.loadModel("TromboneSlide.obj", "HornSkin.bmp", Midis2jam2.MatType.REFLECTIVE, 0.9f);
			modelNode.attachChild(slide);
			modelNode.setLocalRotation(new Quaternion().fromAngles(rad(-10), 0, 0));
			highestLevel.setLocalTranslation(0, 65, -200);
		}
		
		/**
		 * Moves the slide of the trombone to a given position, from 1st to 7th position.
		 *
		 * @param position the trombone position
		 */
		private void moveToPosition(double position) {
			slide.setLocalTranslation(slidePosition(position));
		}
		
		/**
		 * Returns the 3D vector for a given slide position.
		 *
		 * @param position the position
		 * @return the translation vector
		 */
		private Vector3f slidePosition(double position) {
			return new Vector3f(0, 0, (float) (((3.333333) * position) - 1));
		}
		
		/**
		 * Returns the current slide position.
		 *
		 * @return the current slide position
		 */
		private double getCurrentSlidePosition() {
			var pos = slide.getLocalTranslation();
			return 0.3 * (pos.z + 1);
		}
		
		@Override
		public void tick(double time, float delta) {
			super.tick(time, delta);
			if (isPlaying() && currentNotePeriod != null) {
				moveToPosition(getSlidePositionFromNote(currentNotePeriod));
			}
			if (!notePeriods.isEmpty() && !isPlaying()) {
				var notePeriod = notePeriods.get(0);
				var startTime = notePeriod.startTime;
				if (startTime - time <= 1) {
					var targetPos = getSlidePositionFromNote(notePeriod);
					var currentPos = getCurrentSlidePosition();
					moveToPosition(getCurrentSlidePosition() + ((targetPos - currentPos) / (startTime - time)) * delta);
				}
			}
		}
		
		
		/**
		 * Gets slide position from note.
		 *
		 * @param period the period
		 * @return the slide position from note
		 */
		private int getSlidePositionFromNote(NotePeriod period) {
			return (period.midiNote % 7) + 1;
		}
		
		@Override
		protected void moveForPolyphony() {
			offsetNode.setLocalRotation(new Quaternion().fromAngles(0, rad(30f + indexForMoving() * -3), 0));
			offsetNode.setLocalTranslation(0, indexForMoving(), 0);
		}
	}
}
