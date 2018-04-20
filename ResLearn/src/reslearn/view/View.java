package reslearn.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class View extends Application{
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start (Stage stage) throws Exception {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("Einstellungen.fxml"));
		stage.setScene(new Scene(root));
		stage.setTitle("ResLearn");
		stage.show();
	}
}
