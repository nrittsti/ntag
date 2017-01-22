/**
 * This file is part of NTag (audio file tag editor).
 *
 * NTag is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NTag is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2016, Nico Rittstieg
 */
package ntag.fx.scene.control.rating;

import java.util.List;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;

import javafx.scene.input.MouseEvent;

public class RatingBehavior extends BehaviorBase<RatingControl> {

	public RatingBehavior(RatingControl control, List<KeyBinding> keyBindings) {
		super(control, keyBindings);
		control.setOnMouseClicked((MouseEvent event) -> {
			mouseClicked(event);
		});
		control.setOnMouseMoved((MouseEvent event) -> {
			mouseMoved(event);
		});
		control.setOnMouseExited((MouseEvent event) -> {
			mouseExitedIntern(event);
		});
	}

	private void mouseClicked(MouseEvent event) {
		event.consume();
		if (getControl().isDisabled() || !getControl().isVisible()) {
			return;
		}
		RatingControl c = getControl();
		int selection = calcSelection(event);
		if (c.getValue() != selection) {
			c.setValue(selection);
		}
		c.setRating(c.getValue() / RatingControl.STEP);
	}

	private void mouseMoved(MouseEvent event) {
		if (getControl().isDisabled() || !getControl().isVisible()) {
			return;
		}
		RatingControl c = getControl();
		int selection = calcSelection(event);
		if (selection != c.getHoverValue()) {
			c.setHoverValue(selection);
		}
	}

	private void mouseExitedIntern(MouseEvent e) {
		if (getControl().isDisabled() || !getControl().isVisible()) {
			return;
		}
		RatingControl c = getControl();
		if (c.getHoverValue() != c.getValue()) {
			c.setHoverValue(c.getValue());
		}
	}

	private int calcSelection(MouseEvent event) {
		int w = 16 * 5;
		int x = Math.min((int) event.getX(), w);
		int value = (int) ((x / (float) w) * RatingControl.MAXIMUM);
		if (value < 15) {
			value = 0;
		} else {
			value = value + (RatingControl.STEP - (value % RatingControl.STEP));
			value = value >= RatingControl.MAXIMUM ? RatingControl.MAXIMUM : value;
		}
		return value;
	}
}
