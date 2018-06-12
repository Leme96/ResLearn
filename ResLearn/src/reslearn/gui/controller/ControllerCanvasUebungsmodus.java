package reslearn.gui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Pair;
import reslearn.gui.Diagramm;
import reslearn.gui.DisplayCanvas;
import reslearn.gui.ResFeld;
import reslearn.gui.ViewUebungsmodus;
import reslearn.gui.ImportExport.AufgabeLadenImport;
import reslearn.model.algorithmus.Algorithmus;
import reslearn.model.paket.Arbeitspaket;
import reslearn.model.paket.ResEinheit;
import reslearn.model.paket.Teilpaket;
import reslearn.model.resCanvas.ResCanvas;
import reslearn.model.utils.Vektor2i;
import reslearn.model.validierung.Validierung;

public class ControllerCanvasUebungsmodus {

	private double zeigerX, zeigerY;
	private double translateX, translateY;
	private double newTranslateX, newTranslateY;
	private Diagramm diagramm;
	private ResCanvas resCanvas;
	private Teilpaket teilpaketClicked;
	private ResFeld rect;
	private ColorPicker colorPicker;
	private Button validierenButton;
	private TextArea fehlerMeldung;
	private Button buttonMaxPersonenMinus;
	private Button buttonMaxPersonenPlus;
	private TextField textFieldMaxPersonen;
	private Label maxPersonen;
	private ArrayList<Arbeitspaket> arbeitspaketeArrayList;
	private Line[] alleLinien = new Line[26];
	private Label korrekturvorschlaege;
	private Rectangle rectangle;
	ArrayList<Rectangle> rahmenArray = new ArrayList<>();

	public ControllerCanvasUebungsmodus(ResCanvas resCanvas, Diagramm diagramm) {
		this.resCanvas = resCanvas;
		this.diagramm = diagramm;
		erstelleRahmen();
		erstelleTabelle();
		erstelleTabelleArbeitspakete();
		erstelleValidierenButton();
		erstelleButtons();
		erstelleGrenzLinie();
		kapaGrenzeEingeben();
		erstelleKorrekturvorschlaege();
		leereFehlermeldungErstellen();
	}

	public void erstelleKorrekturvorschlaege() {
		korrekturvorschlaege = new Label("Korrekturvorschl�ge");
		korrekturvorschlaege.setLayoutX(
				DisplayCanvas.canvasStartpunktX + DisplayCanvas.canvasBreite + DisplayCanvas.gesamtAbstandX);
		korrekturvorschlaege.setLayoutY(DisplayCanvas.tabelleLayoutY);
		korrekturvorschlaege.setPrefWidth(DisplayCanvas.breiteFehlermeldung);
		korrekturvorschlaege.setPrefHeight(DisplayCanvas.resFeldBreite * 1.5);
		korrekturvorschlaege.setAlignment(Pos.CENTER);
		korrekturvorschlaege.setFont(new Font("Arial", DisplayCanvas.schriftGroesse * 1.5));
		korrekturvorschlaege.setStyle("-fx-font-weight: bold");
	}

	public void kapaGrenzeEingeben() {

		maxPersonen = new Label("Kapazit�tsgrenze:");
		maxPersonen.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));
		maxPersonen.setLayoutX(DisplayCanvas.buttonLoesungsmodusLayoutX);
		maxPersonen.setLayoutY(DisplayCanvas.buttonLoesungsmodusLayoutY + DisplayCanvas.resFeldBreite * 2);
		maxPersonen.setPrefWidth(DisplayCanvas.resFeldBreite * 5);

		buttonMaxPersonenMinus = new Button("-");
		buttonMaxPersonenMinus.setLayoutX(maxPersonen.getLayoutX() + maxPersonen.getPrefWidth());
		buttonMaxPersonenMinus.setPrefWidth(DisplayCanvas.resFeldBreite * 1.5);
		buttonMaxPersonenMinus.setLayoutY(DisplayCanvas.buttonLoesungsmodusLayoutY + DisplayCanvas.resFeldBreite * 2);
		buttonMaxPersonenMinus.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));
		buttonMaxPersonenMinus.setOnAction(handleButtonMaxPersonenMinusAction);

		textFieldMaxPersonen = new TextField(Integer.toString(AufgabeLadenImport.maxPersonenParallel));
		textFieldMaxPersonen.setLayoutX(buttonMaxPersonenMinus.getLayoutX() + buttonMaxPersonenMinus.getPrefWidth());
		textFieldMaxPersonen.setLayoutY(DisplayCanvas.buttonLoesungsmodusLayoutY + DisplayCanvas.resFeldBreite * 2);
		textFieldMaxPersonen.setPrefWidth(DisplayCanvas.resFeldBreite * 1.5);
		textFieldMaxPersonen.setAlignment(Pos.CENTER);
		textFieldMaxPersonen.setEditable(false);
		textFieldMaxPersonen.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));

		buttonMaxPersonenPlus = new Button("+");
		buttonMaxPersonenPlus.setLayoutX(textFieldMaxPersonen.getLayoutX() + textFieldMaxPersonen.getPrefWidth());
		buttonMaxPersonenPlus.setLayoutY(DisplayCanvas.buttonLoesungsmodusLayoutY + DisplayCanvas.resFeldBreite * 2);
		buttonMaxPersonenPlus.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));
		buttonMaxPersonenPlus.setPrefWidth(DisplayCanvas.resFeldBreite * 1.5);
		buttonMaxPersonenPlus.setOnAction(handleButtonMaxPersonenPlusAction);

	}

	private EventHandler<ActionEvent> handleButtonMaxPersonenMinusAction = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			arbeitspaketeArrayList = resCanvas.getArbeitspaketListe();
			int maxMitarbeiterAnzahl = 0;
			buttonMaxPersonenPlus.setDisable(false);
			for (int i = 0; i < arbeitspaketeArrayList.size(); i++) {
				if (arbeitspaketeArrayList.get(i).getMitarbeiteranzahl() > maxMitarbeiterAnzahl) {
					maxMitarbeiterAnzahl = arbeitspaketeArrayList.get(i).getMitarbeiteranzahl();
				}
			}
			if (AufgabeLadenImport.maxPersonenParallel > maxMitarbeiterAnzahl) {
				AufgabeLadenImport.maxPersonenParallel--;
				textFieldMaxPersonen.setText(Integer.toString(AufgabeLadenImport.maxPersonenParallel));
			} else {
				buttonMaxPersonenMinus.setDisable(true);
			}
			for (int i = 0; i < alleLinien.length; i++) {
				if (i == AufgabeLadenImport.maxPersonenParallel) {
					alleLinien[i].setOpacity(100);
				} else {
					alleLinien[i].setOpacity(0);
				}
			}

		}
	};

	private EventHandler<ActionEvent> handleButtonMaxPersonenPlusAction = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			buttonMaxPersonenMinus.setDisable(false);
			if (AufgabeLadenImport.maxPersonenParallel < 25) {
				AufgabeLadenImport.maxPersonenParallel++;
				textFieldMaxPersonen.setText(Integer.toString(AufgabeLadenImport.maxPersonenParallel));
			} else {
				buttonMaxPersonenPlus.setDisable(true);
			}
			for (int i = 0; i < alleLinien.length; i++) {
				if (i == AufgabeLadenImport.maxPersonenParallel) {
					alleLinien[i].setOpacity(100);
				} else {
					alleLinien[i].setOpacity(0);
				}
			}

		}
	};

	public void leereFehlermeldungErstellen() {
		fehlerMeldung = new TextArea("");
		fehlerMeldung.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));
		fehlerMeldung.setEditable(false);
		fehlerMeldung.setLayoutX(DisplayCanvas.canvasBreite);
		fehlerMeldung.setLayoutY(0 - DisplayCanvas.abstandY - DisplayCanvas.tabelleArbeitspaketLaenge
				+ korrekturvorschlaege.getPrefHeight());
		fehlerMeldung.setPrefWidth(DisplayCanvas.breiteFehlermeldung);
		fehlerMeldung.setPrefHeight(DisplayCanvas.canvasLaenge);

		fehlerMeldung.setPrefHeight(DisplayCanvas.hoeheFehlermeldung);
		ViewUebungsmodus.getInstance().getPane().getChildren().add(fehlerMeldung);

	}

	public void makeDraggable(ResFeld feld) {
		feld.setOnMousePressed(OnMousePressedEventHandler);
		feld.setOnMouseDragged(OnMouseDraggedEventHandler);
		feld.setOnContextMenuRequested(OnMouseSecondaryEventHandler);
		ViewUebungsmodus.getInstance().getAp().setOnAction(OnMenuItemApEventHandler);
		ViewUebungsmodus.getInstance().getReset().setOnAction(OnMenuItemResetEventHandler);
	}

	// Event Handler Maus klicken
	private EventHandler<MouseEvent> OnMousePressedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {

			zeigerX = e.getSceneX();
			zeigerY = e.getSceneY();

			rect = (ResFeld) e.getSource();
			teilpaketClicked = rect.getResEinheit().getTeilpaket();

			translateX = rect.getTranslateX();
			translateY = rect.getTranslateY();

			befuelleTabelle();
			markiereArbeitspaketInTabelle(teilpaketClicked.getArbeitspaket());

		}
	};

	// Event Handler Maus ziehen
	private EventHandler<MouseEvent> OnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {

			// 1. nur ein ResFeld (A) verschieben
			// 2. die anderen ResFelder m�ssen sich an dem ersten Resfeld orientieren
			// 3. offset von a auf andere �betragen
			// 4. Offset muss gemerkt werden ( falls etwas r�ckg�ngig gemacht werden muss)

			// �berpr�fung ( logische verschieben abgschlossen, aber noch nicht in der Gui
			// dargestellt)

			double neuePositionZeigerX = e.getSceneX();
			double neuePositionZeigerY = e.getSceneY();

			int differenzX = (int) ((neuePositionZeigerX - zeigerX) / DisplayCanvas.resFeldBreite); // neue Position
			// Mauszeiger - alte
			int differenzY = (int) ((neuePositionZeigerY - zeigerY) / DisplayCanvas.resFeldLaenge); // Position
			// Mauszeiger

			// Verschiebung auf der X-Achse bewirkt logisches Verschieben im
			// Koordinatensystem

			while (differenzX != 0 || differenzY != 0) {

				boolean verschiebbar = false;

				if (differenzX > 0) {
					differenzX--;
					verschiebbar = rect.getResEinheit().getTeilpaket().bewegeX(resCanvas, 1);
					Algorithmus.ausgeben(resCanvas.getKoordinatenSystem());
					verschiebenX(verschiebbar, 1);
				}
				if (differenzX < 0) {
					differenzX++;
					verschiebbar = rect.getResEinheit().getTeilpaket().bewegeX(resCanvas, -1);
					Algorithmus.ausgeben(resCanvas.getKoordinatenSystem());
					verschiebenX(verschiebbar, -1);
				}

				// Verschiebung auf der Y-Achse bewirkt logisches Verschieben im
				// Koordinatensystem

				verschiebbar = false;

				if (differenzY > 0) {
					differenzY--;
					verschiebbar = rect.getResEinheit().getTeilpaket().bewegeY(resCanvas, -1);
					Algorithmus.ausgeben(resCanvas.getKoordinatenSystem());
					verschiebenY(verschiebbar, 1);
				}
				if (differenzY < 0) {
					differenzY++;
					verschiebbar = rect.getResEinheit().getTeilpaket().bewegeY(resCanvas, 1);
					Algorithmus.ausgeben(resCanvas.getKoordinatenSystem());
					verschiebenY(verschiebbar, -1);
				}
			}

		}
	};

	private void verschiebenX(boolean verschiebbar, int vorzeichen) {
		if (verschiebbar) {
			newTranslateX = translateX + DisplayCanvas.resFeldBreite * vorzeichen;
			zeigerX += DisplayCanvas.resFeldBreite * vorzeichen;
			bewegeX(vorzeichen);
			translateX = rect.getTranslateX();
		}
	}

	private void verschiebenY(boolean verschiebbar, int vorzeichen) {
		if (verschiebbar) {
			newTranslateY = translateY + DisplayCanvas.resFeldLaenge * vorzeichen;
			zeigerY += DisplayCanvas.resFeldLaenge * vorzeichen;
			bewegeY(vorzeichen);
			translateY = rect.getTranslateY();
		}
	}

	private void bewegeX(int vorzeichen) {
		HashMap<ResFeld, Vektor2i> resFeldMapTmp = new HashMap<ResFeld, Vektor2i>();
		for (int y = 0; y < diagramm.getResFeldArray().length; y++) {
			for (int x = 0; x < diagramm.getResFeldArray()[y].length; x++) {
				ResFeld resFeld = diagramm.getResFeldArray()[y][x];
				if (resFeld != null) {
					if (teilpaketClicked == resFeld.getResEinheit().getTeilpaket()) {
						resFeld.setTranslateX(newTranslateX);
						resFeldMapTmp.put(resFeld, new Vektor2i(y, x));
						diagramm.getResFeldArray()[y][x] = null;
					}
				}
			}
		}
		Iterator<Entry<ResFeld, Vektor2i>> it = resFeldMapTmp.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry<ResFeld, Vektor2i> entry = it.next();
			Vektor2i vektor = entry.getValue();
			diagramm.getResFeldArray()[vektor.getyKoordinate()][vektor.getxKoordinate() + vorzeichen] = entry.getKey();
		}
	}

	private void bewegeY(int vorzeichen) {
		HashMap<ResFeld, Vektor2i> resFeldMapTmp = new HashMap<ResFeld, Vektor2i>();
		for (int y = 0; y < diagramm.getResFeldArray().length; y++) {
			for (int x = 0; x < diagramm.getResFeldArray()[y].length; x++) {
				ResFeld resFeld = diagramm.getResFeldArray()[y][x];
				if (resFeld != null) {

					if (teilpaketClicked == resFeld.getResEinheit().getTeilpaket()) {
						resFeld.setTranslateY(newTranslateY);
						resFeldMapTmp.put(resFeld, new Vektor2i(y, x));
						diagramm.getResFeldArray()[y][x] = null;
					}
				}
			}
		}
		Iterator<Entry<ResFeld, Vektor2i>> it = resFeldMapTmp.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry<ResFeld, Vektor2i> entry = it.next();
			Vektor2i vektor = entry.getValue();
			diagramm.getResFeldArray()[vektor.getyKoordinate() + vorzeichen][vektor.getxKoordinate()] = entry.getKey();
		}
	}

	// Zeige KontextMen� mit Rechtsklick
	private EventHandler<ContextMenuEvent> OnMouseSecondaryEventHandler = new EventHandler<ContextMenuEvent>() {
		@Override
		public void handle(ContextMenuEvent e) {
			ViewUebungsmodus.getInstance().getMenu().show(rect, e.getSceneX(), e.getSceneY());

			for (int y = 0; y < rect.getResEinheit().getTeilpaket().getMitarbeiteranzahl(); y++) {
				for (int x = 0; x < rect.getResEinheit().getTeilpaket().getVorgangsdauer(); x++) {
					if (diagramm.getResFeldArray()[y][x] != null) {
						ViewUebungsmodus.getInstance().getReset().setDisable(true);
						return;
					} else {
						ViewUebungsmodus.getInstance().getReset().setDisable(false);
					}
				}
			}
		}
	};

	// MenuItem Arbeitspaket teilen
	private EventHandler<ActionEvent> OnMenuItemApEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent e) {

			@SuppressWarnings("unused")
			Bearbeitungsfenster bearbeitungsFenster = new Bearbeitungsfenster(rect);
		}
	};

	EventHandler<ActionEvent> OnMenuItemResetEventHandler = new EventHandler<ActionEvent>() {
		/**
		 * Beim Klick auf den Men�-Punkt "Zur�cksetzen" wird zun�chst in der Logik die
		 * Methode reset() am geklickten Arbeitspaket aufgerufen. In der GUI werden alle
		 * ResFelder, die dem geklickten Arbeitspaket angeh�ren aus dem Diagramm
		 * gel�scht.
		 *
		 * Anhand des abge�nderten Koordinatensystems, das aus der Logik zur�ckkommt
		 * (reset()) wird das zur�ckgesetzte Paket in der oberen linke Ecke neu
		 * gezeichnet
		 */
		@Override
		public void handle(ActionEvent e) {
			ResEinheit[][] koordinatenSystem = rect.getResEinheit().getTeilpaket().getArbeitspaket().reset(0,
					resCanvas);

			for (int i = 0; i < diagramm.getResFeldArray().length; i++) {
				for (int j = 0; j < diagramm.getResFeldArray()[i].length; j++) {
					ResFeld resFeld = diagramm.getResFeldArray()[i][j];
					if (resFeld != null) {
						if (resFeld.getResEinheit().getTeilpaket().getArbeitspaket() == rect.getResEinheit()
								.getTeilpaket().getArbeitspaket()) {
							ViewUebungsmodus.getInstance().getPane().getChildren().remove(resFeld);
							diagramm.getResFeldArray()[i][j] = null;
						}
					}
				}
			}

			for (int i = 0; i < koordinatenSystem.length; i++) {
				for (int j = 0; j < koordinatenSystem[i].length; j++) {
					if (koordinatenSystem[i][j] != null) {
						if (koordinatenSystem[i][j].getTeilpaket().getArbeitspaket() == rect.getResEinheit()
								.getTeilpaket().getArbeitspaket()) {
							ResFeld resFeld = new ResFeld(j * DisplayCanvas.resFeldBreite,
									i * DisplayCanvas.resFeldLaenge, koordinatenSystem[i][j]);
							resFeld.getResEinheit().setTeilpaket(koordinatenSystem[i][j].getTeilpaket());

							resFeld.setFill(rect.getFill());

							ViewUebungsmodus.getInstance().getPane().getChildren().add(resFeld);
							makeDraggable(resFeld);

							diagramm.getResFeldArray()[i][j] = resFeld;
						}
					}
				}
			}

		}
	};

	/////////////////////////////////////////////////////////////////////////
	// Erstellung der Grenzlinie fuer Anzahl Mitarbeiter parallel //
	///////////////////////////////////////////////////////////////////////

	public void erstelleGrenzLinie() {

		if (kapazitaetstreuModus.isSelected()) {
			for (int i = 0; i < 26; i++) {
				alleLinien[i] = new Line(
						DisplayCanvas.canvasStartpunktX + DisplayCanvas.abstandX + DisplayCanvas.spaltX,
						DisplayCanvas.canvasStartpunktY + DisplayCanvas.canvasLaenge - DisplayCanvas.abstandY
						- DisplayCanvas.spaltY - i * DisplayCanvas.resFeldBreite,
						DisplayCanvas.canvasStartpunktX + DisplayCanvas.canvasBreite - DisplayCanvas.abstandX,
						DisplayCanvas.canvasStartpunktY + DisplayCanvas.canvasLaenge - DisplayCanvas.abstandY
						- DisplayCanvas.spaltY - i * DisplayCanvas.resFeldBreite);

				alleLinien[i].setStroke(Color.RED);

				if (i != AufgabeLadenImport.maxPersonenParallel) {
					alleLinien[i].setOpacity(0);
				}
			}
			ViewUebungsmodus.getInstance().getPane().getChildren()
			.add(alleLinien[AufgabeLadenImport.maxPersonenParallel]);

		}
	}

	/////////////////////////////////////////////////////////////////////////
	// Erstellung des Valiedieren Button //
	///////////////////////////////////////////////////////////////////////

	public void erstelleValidierenButton() {
		validierenButton = new Button("Validieren");
		validierenButton.setLayoutX(
				DisplayCanvas.canvasStartpunktX + DisplayCanvas.canvasBreite + DisplayCanvas.gesamtAbstandX);
		validierenButton.setLayoutY(DisplayCanvas.canvasStartpunktY + DisplayCanvas.canvasLaenge
				- DisplayCanvas.abstandX - DisplayCanvas.spaltX);
		validierenButton.setOnAction(ValidierenAction);
		validierenButton.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));
		ViewUebungsmodus.getInstance().getPane().getChildren().add(validierenButton);
	}

	private EventHandler<ActionEvent> ValidierenAction = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			Validierung vali = new Validierung(diagramm.getResFeldArray());
			String ausgabe = "";
			if (termintreuModus.isSelected()) {
				ViewUebungsmodus.getInstance().getPane().getChildren().remove(fehlerMeldung);
				vali.AlgoTermintreu();
				for (int i = 0; i < vali.getFeedbackListe().size(); i++) {
					ausgabe += vali.getFeedbackListe().get(i).toString();
				}
				fehlerMeldung = new TextArea(ausgabe);
				fehlerMeldung.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));
				fehlerMeldung.setEditable(false);
				if (vali.getFeedbackListe().get(0).toString() == "Alles in Ordnung!") {
					fehlerMeldung.setStyle("-fx-text-fill: green;");
				} else {
					fehlerMeldung.setStyle("-fx-text-fill: red;");
				}
				fehlerMeldung.setLayoutX(DisplayCanvas.canvasBreite);
				fehlerMeldung.setLayoutY(0 - DisplayCanvas.abstandY - DisplayCanvas.tabelleArbeitspaketLaenge
						+ korrekturvorschlaege.getPrefHeight());
				fehlerMeldung.setPrefWidth(DisplayCanvas.breiteFehlermeldung);
				fehlerMeldung.setPrefHeight(DisplayCanvas.hoeheFehlermeldung);
				fehlerMeldung.setWrapText(true);
				ViewUebungsmodus.getInstance().getPane().getChildren().add(fehlerMeldung);
			} else {
				ViewUebungsmodus.getInstance().getPane().getChildren().remove(fehlerMeldung);
				vali.AlgoKapazitaetstreu(AufgabeLadenImport.maxPersonenParallel);
				for (int i = 0; i < vali.getFeedbackListe().size(); i++) {
					ausgabe += vali.getFeedbackListe().get(i).toString();
				}
				fehlerMeldung = new TextArea(ausgabe);
				fehlerMeldung.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));
				fehlerMeldung.setEditable(false);
				if (vali.getFeedbackListe().get(0).toString() == "Alles in Ordnung!") {
					fehlerMeldung.setStyle("-fx-text-fill: green;");
				} else {
					fehlerMeldung.setStyle("-fx-text-fill: red;");
				}
				fehlerMeldung.setLayoutX(DisplayCanvas.canvasBreite);
				fehlerMeldung.setLayoutY(0 - DisplayCanvas.abstandY - DisplayCanvas.tabelleArbeitspaketLaenge
						+ korrekturvorschlaege.getPrefHeight());
				fehlerMeldung.setPrefWidth(DisplayCanvas.breiteFehlermeldung);
				fehlerMeldung.setPrefHeight(DisplayCanvas.hoeheFehlermeldung);
				fehlerMeldung.setWrapText(true);
				//ViewUebungsmodus.getInstance().getPane().getChildren().add(fehlerMeldung);
			}

		}
	};

	/////////////////////////////////////////////////////////////////////////
	// Erstellung der Legende f�r die einzelnen Farben der Arbeitspakete //
	///////////////////////////////////////////////////////////////////////
	private Pane legende = new Pane();
	private ObservableMap<Arbeitspaket, Color> colorObservableMap = FXCollections.observableHashMap();

	// ResEinheiten -> ObservableMap ResEinheit, Vektor2i
	// ArrayList<blabala> = asfasf;
	// arrayList.get(0)[i][j] == arrayList.get(1)[i][j]
	// arrayList.get(1)[i][j] -> ResEinheit
	// ResEinheit -> update Observableblababla
	public void erstelleLegende(HashMap<Arbeitspaket, Color> arbeitspaketeMitFarbe) {
		legende.setLayoutX(DisplayCanvas.canvasStartpunktX);
		legende.setLayoutY(DisplayCanvas.canvasStartpunktY + DisplayCanvas.canvasLaenge);
		legende.setPrefWidth(DisplayCanvas.canvasBreite);
		legende.setPrefHeight(DisplayCanvas.legendeHoehe);
		legende.setStyle("-fx-background-radius: 30;");
		legende.setStyle("-fx-background-color: #c0c0c0;");
		Label label = null;
		Circle circle = null;
		for (Map.Entry<Arbeitspaket, Color> entry : arbeitspaketeMitFarbe.entrySet()) {
			colorObservableMap.put(entry.getKey(), entry.getValue());
			if (label == null) {
				circle = new Circle(DisplayCanvas.abstandX, DisplayCanvas.legendeKreisStartpunktY,
						DisplayCanvas.legendeKreisRadius);
				circle.fillProperty().bind(Bindings.valueAt(colorObservableMap, entry.getKey()));
				// circle.setFill(entry.getValue());
			} else {
				circle = new Circle(label.getLayoutX() + DisplayCanvas.legendeAbstand,
						DisplayCanvas.legendeKreisStartpunktY, DisplayCanvas.legendeKreisRadius);
				circle.fillProperty().bind(Bindings.valueAt(colorObservableMap, entry.getKey()));
				// circle.setFill(entry.getValue());
			}

			label = new Label(entry.getKey().getId());
			label.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));
			label.setLayoutX(circle.getCenterX() + DisplayCanvas.abstandX);
			label.layoutYProperty().bind(legende.heightProperty().subtract(label.heightProperty()).divide(2));
			legende.getChildren().addAll(circle, label);

		}
	}

	/////////////////////////////////////////////////////////////////////////
	// Erstellung der unterschiedlichen Datentypen f�r die Tabelle links //
	///////////////////////////////////////////////////////////////////////

	private TableView<Pair<String, Object>> table = new TableView<>();
	private ObservableList<Pair<String, Object>> data;

	@SuppressWarnings("unchecked")
	private void befuelleTabelle() {
		// Erstellen der Informationsleiste links
		data = FXCollections.observableArrayList(
				pair("Arbeitspaket", rect.getResEinheit().getTeilpaket().getArbeitspaket().getId()),
				pair("Farbe", rect.getFill()),
				pair("FAZ", rect.getResEinheit().getTeilpaket().getArbeitspaket().getFaz()),
				pair("FEZ", rect.getResEinheit().getTeilpaket().getArbeitspaket().getFez()),
				pair("SAZ", rect.getResEinheit().getTeilpaket().getArbeitspaket().getSaz()),
				pair("SEZ", rect.getResEinheit().getTeilpaket().getArbeitspaket().getSez()),
				pair("Vorgangsdauer", rect.getResEinheit().getTeilpaket().getArbeitspaket().getVorgangsdauer()),
				pair("Mitarbeiter", rect.getResEinheit().getTeilpaket().getArbeitspaket().getMitarbeiteranzahl()),
				pair("Aufwand", rect.getResEinheit().getTeilpaket().getArbeitspaket().getAufwand()));

		table.setItems(data);
	}

	@SuppressWarnings("unchecked")
	private void erstelleTabelle() {

		table.setEditable(true);
		table.setLayoutX(DisplayCanvas.tabelleLayoutX);
		table.setLayoutY(DisplayCanvas.tabelleLayoutY);
		table.setPrefHeight(DisplayCanvas.tabelleLaenge);
		table.setStyle("-fx-font:" + DisplayCanvas.schriftGroesse + " Arial;");

		TableColumn<Pair<String, Object>, String> name = new TableColumn<>("Name");
		name.setCellValueFactory(new PairKeyFactory());
		name.setMinWidth(DisplayCanvas.tabelleBreite / 2);
		name.setSortable(false);

		TableColumn<Pair<String, Object>, Object> wert = new TableColumn<>("Wert");
		wert.setCellValueFactory(new PairValueFactory());
		wert.setMinWidth(DisplayCanvas.tabelleBreite / 2);
		wert.setSortable(false);

		table.getColumns().addAll(name, wert);
		wert.setCellFactory(
				new Callback<TableColumn<Pair<String, Object>, Object>, TableCell<Pair<String, Object>, Object>>() {
					@Override
					public TableCell<Pair<String, Object>, Object> call(
							TableColumn<Pair<String, Object>, Object> column) {
						return new PairValueCell();
					}
				});
	}

	private Pair<String, Object> pair(String name, Object value) {
		return new Pair<>(name, value);
	}

	class PairKeyFactory
	implements Callback<TableColumn.CellDataFeatures<Pair<String, Object>, String>, ObservableValue<String>> {
		@Override
		public ObservableValue<String> call(TableColumn.CellDataFeatures<Pair<String, Object>, String> data) {
			return new ReadOnlyObjectWrapper<>(data.getValue().getKey());
		}
	}

	class PairValueFactory
	implements Callback<TableColumn.CellDataFeatures<Pair<String, Object>, Object>, ObservableValue<Object>> {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public ObservableValue<Object> call(TableColumn.CellDataFeatures<Pair<String, Object>, Object> data) {
			Object value = data.getValue().getValue();
			return (value instanceof ObservableValue) ? (ObservableValue) value : new ReadOnlyObjectWrapper<>(value);
		}
	}

	class PairValueCell extends TableCell<Pair<String, Object>, Object> {
		@Override
		protected void updateItem(Object item, boolean empty) {
			super.updateItem(item, empty);

			if (item != null) {
				if (item instanceof String) {
					setText((String) item);
					setGraphic(null);
				} else if (item instanceof Integer) {
					setText(Integer.toString((Integer) item));
					setGraphic(null);
				} else if (item instanceof Color) {
					colorPicker = new ColorPicker();
					colorPicker.setStyle("-fx-color-label-visible: false;");
					colorPicker.setValue((Color) rect.getFill());
					setGraphic(colorPicker);
					wechsleFarbe();
				}
			} else {
				setText(null);
				setGraphic(null);
			}
		}
	}

	private void wechsleFarbe() {
		colorPicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				for (ResFeld[] resAr : diagramm.getResFeldArray()) {
					for (ResFeld teilpaket : resAr) {
						if (teilpaket != null) {
							if (teilpaketClicked.getArbeitspaket() == teilpaket.getResEinheit().getTeilpaket()
									.getArbeitspaket()) {
								teilpaket.setFill(colorPicker.getValue());
								colorObservableMap.replace(teilpaketClicked.getArbeitspaket(), colorPicker.getValue());
							}
						}
					}
				}
			}
		});
	}

	//////////////////////////////////////////////////////////////////////////////////
	// Erstellung der Tabelle zur Anzeige der Arbeitspakete //
	//////////////////////////////////////////////////////////////////////////////////

	private TableView<Arbeitspaket> tabelleArbeitspakete = new TableView<>();
	private ObservableList<Arbeitspaket> dataPakete;

	@SuppressWarnings("unchecked")
	private void erstelleTabelleArbeitspakete() {
		dataPakete = FXCollections.observableArrayList();
		arbeitspaketeArrayList = resCanvas.getArbeitspaketListe();
		arbeitspaketeArrayList.forEach(p -> dataPakete.add(p));

		int breite = DisplayCanvas.tabelleArbeitspaketBreite;
		if (dataPakete.size() > 2) {
			breite -= 15;
		}

		TableColumn<Arbeitspaket, String> apId = new TableColumn<>("Arbeitspaket-ID");
		apId.setMinWidth(breite / 7);
		apId.setCellValueFactory(new PropertyValueFactory<Arbeitspaket, String>("id"));
		apId.setSortType(TableColumn.SortType.ASCENDING);

		TableColumn<Arbeitspaket, Integer> apFaz = new TableColumn<>("FAZ");
		apFaz.setMinWidth(breite / 7);
		apFaz.setCellValueFactory(new PropertyValueFactory<Arbeitspaket, Integer>("faz"));

		TableColumn<Arbeitspaket, Integer> apSaz = new TableColumn<>("SAZ");
		apSaz.setMinWidth(breite / 7);
		apSaz.setCellValueFactory(new PropertyValueFactory<Arbeitspaket, Integer>("saz"));

		TableColumn<Arbeitspaket, Integer> apFez = new TableColumn<>("FEZ");
		apFez.setMinWidth(breite / 7);
		apFez.setCellValueFactory(new PropertyValueFactory<Arbeitspaket, Integer>("fez"));

		TableColumn<Arbeitspaket, Integer> apSez = new TableColumn<>("SEZ");
		apSez.setMinWidth(breite / 7);
		apSez.setCellValueFactory(new PropertyValueFactory<Arbeitspaket, Integer>("sez"));

		TableColumn<Arbeitspaket, Integer> apAnzMitarbeiter = new TableColumn<>("Max. Personen");
		apAnzMitarbeiter.setMinWidth(breite / 7);
		apAnzMitarbeiter.setCellValueFactory(new PropertyValueFactory<Arbeitspaket, Integer>("mitarbeiteranzahl"));

		TableColumn<Arbeitspaket, Integer> apAufwand = new TableColumn<>("Aufwand (PT)");
		apAufwand.setMinWidth(breite / 7);
		apAufwand.setCellValueFactory(new PropertyValueFactory<Arbeitspaket, Integer>("aufwand"));

		tabelleArbeitspakete.setItems(dataPakete);
		tabelleArbeitspakete.setEditable(true);
		tabelleArbeitspakete.setLayoutX(DisplayCanvas.tabelleArbeitspaketLayoutX);
		tabelleArbeitspakete.setLayoutY(DisplayCanvas.tabelleArbeitspaketLayoutY);
		tabelleArbeitspakete.setPrefSize(DisplayCanvas.tabelleArbeitspaketBreite,
				DisplayCanvas.tabelleArbeitspaketLaenge);
		tabelleArbeitspakete.setStyle("-fx-font:" + DisplayCanvas.schriftGroesse + " Arial;");
		tabelleArbeitspakete.getColumns().addAll(apId, apFaz, apSaz, apFez, apSez, apAnzMitarbeiter, apAufwand);
		tabelleArbeitspakete.getSortOrder().add(apId);
	}

	// Markieren des gew�hlten Arbeitspakets in der Anzeige-Tabelle
	private void markiereArbeitspaketInTabelle(Arbeitspaket ap) {
		tabelleArbeitspakete.getSelectionModel().select(ap);
		tabelleArbeitspakete.scrollTo(ap);
	}

	private RadioButton termintreuModus = new RadioButton();
	private RadioButton kapazitaetstreuModus = new RadioButton();
	private final ToggleGroup modusToggleGroup = new ToggleGroup();

	private void erstelleButtons() {
		termintreuModus.setLayoutX(DisplayCanvas.buttonLoesungsmodusLayoutX);
		termintreuModus.setLayoutY(DisplayCanvas.buttonLoesungsmodusLayoutY);
		termintreuModus.setPrefWidth(DisplayCanvas.buttonLoesungsmodusBreite);
		termintreuModus.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));
		termintreuModus.setText("Termintreu");
		termintreuModus.setOnMouseClicked(OnButtonTermintreuPressedEventHandler);
		termintreuModus.setToggleGroup(modusToggleGroup);

		kapazitaetstreuModus
		.setLayoutX(DisplayCanvas.buttonLoesungsmodusLayoutX * 2 + DisplayCanvas.buttonLoesungsmodusBreite);
		kapazitaetstreuModus.setLayoutY(DisplayCanvas.buttonLoesungsmodusLayoutY);
		kapazitaetstreuModus.setPrefWidth(DisplayCanvas.buttonLoesungsmodusBreite);
		kapazitaetstreuModus.setFont(new Font("Arial", DisplayCanvas.schriftGroesse));
		kapazitaetstreuModus.setText("Kapazit�tstreu");
		kapazitaetstreuModus.setToggleGroup(modusToggleGroup);
		kapazitaetstreuModus.setOnMouseClicked(OnButtonKapazitaetstreuPressedEventHandler);
		kapazitaetstreuModus.setSelected(true);

	}

	private EventHandler<MouseEvent> OnButtonKapazitaetstreuPressedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			for (int i = 0; i < alleLinien.length; i++) {
				if (i == AufgabeLadenImport.maxPersonenParallel) {
					alleLinien[i].setOpacity(100);
				} else {
					alleLinien[i].setOpacity(0);
				}
			}

		}
	};

	private EventHandler<MouseEvent> OnButtonTermintreuPressedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			for (int i = 0; i < alleLinien.length; i++) {
				alleLinien[i].setOpacity(0);
			}
		}
	};

	public ArrayList<Rectangle> erstelleRahmen() {
		for(Arbeitspaket ap : resCanvas.getArbeitspaketListe()) {
			for(Teilpaket tp : ap.getTeilpaketListe()) {
				ResEinheit reseinheit = tp.getResEinheitListe().get(tp.getVorgangsdauer() * (tp.getMitarbeiteranzahl()-1));
				rectangle = new Rectangle(reseinheit.getPosition().getxKoordinate() * DisplayCanvas.resFeldBreite,
						reseinheit.getPosition().getyKoordinate() * DisplayCanvas.resFeldLaenge,
						reseinheit.getTeilpaket().getVorgangsdauer() * DisplayCanvas.resFeldBreite,
						reseinheit.getTeilpaket().getMitarbeiteranzahl() * DisplayCanvas.resFeldLaenge);
				rectangle.setFill(Color.TRANSPARENT);
				rectangle.setStroke(Color.BLACK);
				rectangle.setStrokeWidth(0.5);
				rectangle.setMouseTransparent(true);

				System.out.println(rectangle);
				rahmenArray.add(rectangle);

			}
		}
		return rahmenArray;
	}

	public TableView<Arbeitspaket> getTabelleArbeitspakete() {
		return tabelleArbeitspakete;
	}

	public TableView<Pair<String, Object>> getTable() {
		return table;
	}

	public Pane getLegende() {
		return legende;
	}

	public Button getValidierenButton() {
		return validierenButton;
	}

	public ToggleGroup getModusToggleGroup() {
		return modusToggleGroup;
	}

	public RadioButton getButtonTermintreuModus() {
		return termintreuModus;
	}

	public RadioButton getButtonKapazitaetstreuModus() {
		return kapazitaetstreuModus;
	}

	public Line getKapaGrenze(int i) {
		return alleLinien[i];
	}

	public Button getButtonMaxPersonenMinus() {
		return buttonMaxPersonenMinus;
	}

	public Button getButtonMaxPersonenPlus() {
		return buttonMaxPersonenPlus;
	}

	public TextField getTextFieldMaxPersonen() {
		return textFieldMaxPersonen;
	}

	public Label getMaxPersonen() {
		return maxPersonen;
	}

	public Label getKorrekturvorschlaege() {
		return korrekturvorschlaege;
	}

	public TextArea getFehlermeldung() {
		return fehlerMeldung;
	}

}
