/*
 * This file is part of NTag (audio file tag editor).
 *
 * NTag is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * NTag is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with NTag. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Copyright 2022, Nico Rittstieg
 *
 */

package ntag.fx.util;

import ntag.fx.scene.NTagWindowController;

public class ToggleSplitPane {

  private double[] lastPosition = {0.5d};

  private final NTagWindowController windowController;

  public ToggleSplitPane(NTagWindowController windowController) {
    this.windowController = windowController;
  }

  public void hideOrShowEditor() {
    if (windowController.getDividerPositions()[0] > 0.9d) {
      this.windowController.setDividerPositions(lastPosition);
    } else {
      this.lastPosition = windowController.getDividerPositions();
      this.windowController.setDividerPositions(new double[] {1d});
    }
  }

}
