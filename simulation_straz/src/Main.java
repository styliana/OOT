import javax.swing.*;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // 1. Inicjalizacja Jednostek
        UnitsCollection units = new UnitsCollection();
        units.addUnit(new FireUnit("JRG-1", 50.0614, 19.9360));
        units.addUnit(new FireUnit("JRG-2", 50.0345, 19.9230));
        units.addUnit(new FireUnit("JRG-3", 50.0760, 19.8900));
        units.addUnit(new FireUnit("JRG-4", 50.0820, 20.0200));
        units.addUnit(new FireUnit("JRG-5", 50.0910, 19.9500));
        units.addUnit(new FireUnit("JRG-6", 50.0150, 19.9900));
        units.addUnit(new FireUnit("JRG-7", 50.0950, 20.0400));
        units.addUnit(new FireUnit("SA PSP", 50.0700, 20.0300));
        units.addUnit(new FireUnit("JRG Skawina", 49.9750, 19.8200));
        units.addUnit(new FireUnit("LSP Balice", 50.0770, 19.7800));

        SKKM skkm = new SKKM(units);

        // --- KONFIGURACJA GUI (MAPY) ---
        JFrame frame = new JFrame("Symulacja PSP Kraków");
        SimulationMap mapPanel = new SimulationMap(units);

        frame.add(mapPanel);
        frame.setSize(800, 800); // Większe okno dla czytelności
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Rejestracja mapy jako obserwatora (żeby się odświeżała co tick)
        skkm.addObserver(mapPanel);
        // -------------------------------

        Random random = new Random();

        // Granice obszaru
        double minLat = 49.95855025648944;
        double maxLat = 50.154564013341734;
        double minLon = 19.688292482742394;
        double maxLon = 20.02470275868903;

        System.out.println("Rozpoczynam symulację...");

        // Zwiększamy liczbę kroków dla lepszej zabawy z mapą
        for (int i = 0; i < 200; i++) {
            System.out.println("\n[CZAS: " + i + "s]");

            // Symulacja zgłoszeń
            if (random.nextInt(100) < 30) {
                double rLat = minLat + (maxLat - minLat) * random.nextDouble();
                double rLon = minLon + (maxLon - minLon) * random.nextDouble();
                Location eventLoc = new Location(rLat, rLon);

                EventType type = (random.nextInt(100) < 30) ? EventType.POZAR : EventType.MIEJSCOWE_ZAGROZENIE;

                skkm.handleEvent(eventLoc, type);
            }

            skkm.notifyObservers();

            // Opóźnienie 500ms - idealne, żeby widzieć ruch na mapie
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
        }
    }
}