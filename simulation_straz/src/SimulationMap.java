import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimulationMap extends JPanel implements IObserver {
    private UnitsCollection unitsCollection;

    // Granice mapy (z Main.java)
    private double minLat = 49.9585;
    private double maxLat = 50.1545;
    private double minLon = 19.6882;
    private double maxLon = 20.0247;

    public SimulationMap(UnitsCollection units) {
        this.unitsCollection = units;
        this.setBackground(new Color(30, 30, 30)); // Ciemne tło
    }

    @Override
    public void update() {
        repaint(); // Odśwież widok
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Wygładzanie krawędzi (antyaliasing)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        List<FireUnit> units = unitsCollection.getList();

        for (FireUnit unit : units) {
            // 1. Rysuj BAZĘ
            Point p = scaleCoord(unit.getLocation().getLat(), unit.getLocation().getLon(), width, height);

            g2d.setColor(Color.CYAN);
            g2d.fillRect(p.x - 6, p.y - 6, 12, 12); // Kwadrat bazy
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(unit.getName(), p.x + 8, p.y + 4);

            // 2. Rysuj SAMOCHODY
            for (FireTruck truck : unit.getTrucks()) {
                Location loc = truck.getTargetLocation();
                if (loc == null) continue; // Zabezpieczenie

                Point truckP = scaleCoord(loc.getLat(), loc.getLon(), width, height);

                // Przesunięcie, żeby samochody nie rysowały się idealnie jeden na drugim w bazie
                int offsetX = (truck.getId() * 4) - 10;
                int offsetY = (truck.getId() * 4) - 10;

                // Kolory zależne od stanu
                if (truck.getState() instanceof FreeState) g2d.setColor(Color.GREEN);
                else if (truck.getState() instanceof ReturningState) g2d.setColor(Color.BLUE);
                else {
                    // Akcja lub Dojazd
                    if (truck.getCurrentEvent() == EventType.POZAR) g2d.setColor(Color.RED);
                    else g2d.setColor(Color.YELLOW);
                }

                g2d.fillOval(truckP.x + offsetX, truckP.y + offsetY, 8, 8);
            }
        }
    }

    // Zamiana współrzędnych geograficznych na pixele
    private Point scaleCoord(double lat, double lon, int w, int h) {
        double x = (lon - minLon) / (maxLon - minLon) * w;
        // Y jest odwrócone w grafice komputerowej (0 jest u góry)
        double y = h - ((lat - minLat) / (maxLat - minLat) * h);
        return new Point((int)x, (int)y);
    }
}