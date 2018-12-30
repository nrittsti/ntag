module ntag {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.swing;
	requires java.logging;
	requires jaudiotagger;
	requires java.desktop;
	requires java.xml.bind;

	opens ntag to javafx.fxml;
	exports ntag;
}