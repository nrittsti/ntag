#! /bin/bash
JAVA_HOME="/usr/lib/jvm/java-13-openjdk"
HIDPI_CONFIG="${HOME}/.config/ntag/hidpi.config"
HIDPI_SCALING=100%
if test -f "${HIDPI_CONFIG}"; then
  . "${HIDPI_CONFIG}"
fi
${JAVA_HOME}/bin/java -Dglass.gtk.uiScale=${HIDPI_SCALING} --module-path /opt/ntag/lib --add-modules=javafx.controls,javafx.fxml,javafx.swing,java.logging,jaudiotagger,java.desktop,java.xml.bind -jar /opt/ntag/ntag.jar "$@"
