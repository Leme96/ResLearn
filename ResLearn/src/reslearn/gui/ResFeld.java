package reslearn.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import reslearn.model.paket.ResEinheit;

public class ResFeld {
	
	GraphicsContext gc;
	private static int breite = 20;
	private static int laenge = 20;
	ResFeld[][] resfeld;
	
	public ResFeld(GraphicsContext gc, Canvas canvas, int abstandX, int abstandY, int spaltX, int spaltY, int i, int j) {
		zeichneFeld(gc, canvas, abstandX, abstandY, spaltX, spaltY, i, j);
	}
	
	public ResFeld(GraphicsContext gc, int breite, int laenge) {
		this.gc = gc;
		ResFeld.breite = breite;
		ResFeld.laenge = laenge;
	}
	
    public void zeichneFeld(GraphicsContext gc, Canvas canvas, int abstandX, int abstandY, int spaltX, int spaltY, int i, int j) {
        gc.setFill(Color.rgb(255, 255, 255));
        gc.setLineWidth(1);
        gc.strokeRect(spaltX + abstandX + i*2, - spaltY + abstandY+ j*2, breite, laenge);
        gc.fillRect(spaltX + abstandX + i*2, - spaltY + abstandY+ j*2, breite, laenge);
        
    }
    
    
    public void setzeFeld(GraphicsContext gc, Canvas canvas, int abstandX, int abstandY, int spaltX, int spaltY, int i, int j, ResEinheit resEinheit) {
    	gc.setFill(setzeFarbe(resEinheit));
        gc.fillRect(spaltX + abstandX + i*2, - spaltY + abstandY+ j*2, breite, laenge);
    }
    
    public static Color setzeFarbe(ResEinheit resEinheit) {
		switch (resEinheit.getTeilpaket().getArbeitspaket().getId()) {
		case "A": 
			return Color.RED;
		case "B":
			return Color.GREEN;
		case "C":
			return Color.BLUE;
		case "D":
			return Color.YELLOW;
		case "E":
			return Color.ORANGE;
		default:
			return Color.WHITE;
		}
	}
    
    public int getBreite() {
    	return breite;
    }
    
    public void setBreite(int breite) {
    	this.breite = breite;
    }
    
    public int getLaenge() {
    	return laenge;
    }
    
    public void setLaenge(int laenge) {
    	this.laenge = laenge;
    }
	
    public int getFlaeche() {
    	return laenge*breite;
    }
}