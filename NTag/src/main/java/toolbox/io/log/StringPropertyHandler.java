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
package toolbox.io.log;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;

public class StringPropertyHandler extends Handler {

	private SimpleStringProperty text = new SimpleStringProperty(this, "text", "");

	public final ReadOnlyStringProperty textProperty() {
		return this.text;
	}

	public final String getText() {
		return this.textProperty().get();
	}

	public StringPropertyHandler() {
		setFormatter(new CustomFormatter());
	}

	public StringPropertyHandler(Formatter formatter) {
		setFormatter(formatter);
	}

	@Override
	public void publish(LogRecord record) {
		if (text.length().getValue() == 0) {
			text.set(this.getFormatter().format(record));
		} else {
			text.set(text.get() + this.getFormatter().format(record));
		}
	}

	public void clear() {
		text.set("");
	}

	@Override
	public void flush() {
		// nothing do to
	}

	@Override
	public void close() throws SecurityException {
		text.set("");
		text.unbind();
	}
}
