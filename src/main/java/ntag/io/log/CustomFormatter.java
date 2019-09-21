/*
  This file is part of NTag (audio file tag editor).
  <p>
  NTag is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p>
  NTag is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p>
  You should have received a copy of the GNU General Public License
  along with NTag.  If not, see <http://www.gnu.org/licenses/>.
  <p>
  Copyright 2016, Nico Rittstieg
 */
package ntag.io.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter {

    private String format;

    public CustomFormatter() {
        this.format = "%1$tH:%1$tM:%1$tS.%1$tL %2$s [%4$s]%n%5$s%n%6$s%n";
    }

    public CustomFormatter(String format) {
        this.format = format;
    }

    @Override
    public synchronized String format(LogRecord record) {
        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
                source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }
        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        return String.format(format, LocalDateTime.now(), source, record.getLoggerName(), record.getLevel(), message, throwable);
    }

}
