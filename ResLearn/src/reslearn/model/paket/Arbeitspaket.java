package reslearn.model.paket;

import java.util.ArrayList;
import java.util.Iterator;

import reslearn.main.Main;
import reslearn.model.resCanvas.ResCanvas;
import reslearn.model.utils.Vektor2i;

public class Arbeitspaket extends Paket {

	/**
	 * Benennung des Arbeitspakets
	 */
	private String id;
	/**
	 * Fruehester Anfangszeitpunkt
	 */
	private int faz;
	/**
	 * Spaetester Anfangszeitpunkt
	 */
	private int saz;
	/**
	 * Fruehester Endzeitpunkt
	 */
	private int fez;
	private ArbeitspaketZustand arbeitspaketZustand;
	/**
	 * Spaetester Endzeitpunkt
	 */
	private int sez;
	private ArrayList<Teilpaket> teilpaketListe;

	public Arbeitspaket() {

	}

	// TODO: Validierung der Vorgangsdauer
	public Arbeitspaket(String id, int faz, int fez, int saz, int sez, int vorgangsdauer, int mitarbeiteranzahl,
			int aufwand) {
		super(vorgangsdauer, mitarbeiteranzahl, aufwand);
		this.id = id;
		this.faz = faz;
		this.saz = saz;
		this.fez = fez;
		this.sez = sez;
		teilpaketListe = new ArrayList<Teilpaket>();
		teilpaketListe.add(new Teilpaket(this));
	}

	public void neuSetzen(int abstand, ResCanvas resCanvas) {
		Teilpaket ersteTP = teilpaketListe.get(0);
		ResEinheit erstesRes = ersteTP.getResEinheitListe().get(0);

		int alteXposition = erstesRes.position.getxKoordinate();
		int neueXPosition = alteXposition - abstand;

		teilpaketListe.clear();

		Teilpaket vereint = new Teilpaket(this);
		teilpaketListe.add(vereint);

		var resEinheitenListe = vereint.getResEinheitListe();
		Iterator<ResEinheit> it = resEinheitenListe.iterator();

		ResEinheit[][] koordinatenSystem = resCanvas.getKoordinatenSystem();

		for (int y = this.mitarbeiteranzahl - 1; y >= 0; y--) {
			for (int x = neueXPosition; x < neueXPosition + this.vorgangsdauer; x++) {
				if (koordinatenSystem[y][x] == null) {
					if (it.hasNext()) {
						koordinatenSystem[y][x] = it.next();
						koordinatenSystem[y][x].setPosition(new Vektor2i(y, x));
					}
				}
			}
		}

		// TODO: Ausgeben l�schen
		Main.ausgeben(koordinatenSystem);

		resCanvas.herunterfallen(vereint);

	}

	public void teilpaketHinzufuegen(Teilpaket teilpaket) {
		teilpaketListe.add(teilpaket);
	}

	@Override
	public void bewegen(ResCanvas resCanvas, int yMove, int xMove) {
		for (Teilpaket teilpaket : teilpaketListe) {
			teilpaket.bewegen(resCanvas, yMove, xMove);
		}
	}

	@Override
	public void bewegeX(ResCanvas resCanvas, int xMove) {
		for (Teilpaket teilpaket : teilpaketListe) {
			teilpaket.bewegeX(resCanvas, xMove);
		}
	}

	@Override
	public void bewegeY(ResCanvas resCanvas, int yMove) {
		for (Teilpaket teilpaket : teilpaketListe) {
			teilpaket.bewegeY(resCanvas, yMove);
		}
	}

	public int getFaz() {
		return faz;
	}

	public void setFaz(int faz) {
		this.faz = faz;
	}

	public int getSaz() {
		return saz;
	}

	public void setSaz(int saz) {
		this.saz = saz;
	}

	public int getFez() {
		return fez;
	}

	public void setFez(int fez) {
		this.fez = fez;
	}

	public int getSez() {
		return sez;
	}

	public void setSez(int sez) {
		this.sez = sez;
	}

	public ArrayList<Teilpaket> getTeilpaketListe() {
		return teilpaketListe;
	}

	public void setTeilpaketListe(ArrayList<Teilpaket> teilpaketListe) {
		this.teilpaketListe = teilpaketListe;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArbeitspaketZustand getArbeitspaketZustand() {
		return arbeitspaketZustand;
	}

	public void setArbeitspaketZustand(ArbeitspaketZustand arbeitspaketZustand) {
		this.arbeitspaketZustand = arbeitspaketZustand;
	}

}