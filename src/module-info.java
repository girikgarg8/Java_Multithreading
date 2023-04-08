module FirstFinalProject {
	requires javafx.controls;
	requires javafx.graphics;
	requires tess4j;
	requires opencv;
	requires java.desktop;
	
	opens application to javafx.graphics, javafx.fxml;
}
