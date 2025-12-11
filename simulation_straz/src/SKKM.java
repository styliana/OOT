import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SKKM implements ISubject {
    private List<IObserver> observers = new ArrayList<>();
    private UnitsCollection unitsCollection;

    public SKKM(UnitsCollection collection) {
        this.unitsCollection = collection;
        // Rejestracja jednostek jako obserwatorów
        IIterator<FireUnit> it = collection.iterator();
        while(it.hasNext()) {
            addObserver(it.next());
        }
    }

    @Override
    public void addObserver(IObserver o) { observers.add(o); }

    @Override
    public void removeObserver(IObserver o) { observers.remove(o); }

    @Override
    public void notifyObservers() {
        for (IObserver o : observers) {
            o.update();
        }
    }

    public void handleEvent(Location eventLoc, EventType type) {
        System.out.println("\n--- NOWE ZDARZENIE: " + type + " w " + eventLoc + " ---");

        // 1. Wybór Strategii
        IEventStrategy strategy;
        if (type == EventType.POZAR) {
            strategy = new FireEventStrategy();
        } else {
            strategy = new LocalThreatEventStrategy();
        }

        int carsNeeded = strategy.getRequiredCars();
        System.out.println("Potrzebne samochody: " + carsNeeded);

        // 2. Sortowanie jednostek wg odległości
        List<FireUnit> sortedUnits = new ArrayList<>(unitsCollection.getList());
        sortedUnits.sort(Comparator.comparingDouble(u -> u.getLocation().distanceTo(eventLoc)));

        // 3. Dysponowanie
        int carsSent = 0;
        for (FireUnit unit : sortedUnits) {
            if (carsSent >= carsNeeded) break;

            int available = unit.getAvailableTrucksCount();
            if (available > 0) {
                int take = Math.min(available, carsNeeded - carsSent);
                // TU ZMIANA: Przekazujemy typ i lokalizację zdarzenia
                unit.dispatchTrucks(take, type, eventLoc);
                carsSent += take;
                System.out.println("  -> Zadysponowano " + take + " z " + unit.getName() +
                        " (Odległość: " + String.format("%.4f", unit.getLocation().distanceTo(eventLoc)) + ")");
            }
        }

        if (carsSent < carsNeeded) {
            System.out.println("  -> UWAGA: Brak wystarczających sił! Wysłano tylko " + carsSent);
        }
    }
}