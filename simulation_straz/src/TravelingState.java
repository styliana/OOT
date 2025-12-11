import java.util.Random;

// Stan: Zajęty (dojazd) 
public class TravelingState implements ICarState {
    private int timeRemaining;
    private boolean isFalseAlarm;

    public TravelingState() {
        // Losowy czas dojazdu 0-3s [cite: 65]
        this.timeRemaining = new Random().nextInt(4);
        // 5% szans na alarm fałszywy [cite: 65]
        this.isFalseAlarm = new Random().nextInt(100) < 5;
    }

    @Override
    public void handleTick(FireTruck truck) {
        if (timeRemaining > 0) {
            timeRemaining--;
        } else {
            // Dojechał na miejsce
            if (isFalseAlarm) {
                System.out.println("[" + truck.getUnitName() + "] Samochód " + truck.getId() + ": Alarm fałszywy! Powrót.");
                truck.setState(new ReturningState()); // [cite: 66]
            } else {
            // Dojechał na miejsce
            if (isFalseAlarm) {
                System.out.println(ConsoleColors.PURPLE + "[" + truck.getUnitName() + "] Samochód " + truck.getId() + ": Alarm fałszywy! Powrót." + ConsoleColors.RESET);
                truck.setState(new ReturningState());
            } else {
                // Tu sprawdzamy typ zdarzenia, żeby wypisać odpowiedni komunikat!
                String actionName = "Podejmuje działania";
                String color = ConsoleColors.RESET;

                if (truck.getCurrentEvent() == EventType.POZAR) {
                    actionName = "Gasi POŻAR";
                    color = ConsoleColors.RED;
                } else if (truck.getCurrentEvent() == EventType.MIEJSCOWE_ZAGROZENIE) {
                    actionName = "Likwiduje MIEJSCOWE ZAGROŻENIE";
                    color = ConsoleColors.YELLOW;
                }

                System.out.println(color + "[" + truck.getUnitName() + "] Samochód " + truck.getId() + ": " + actionName + "." + ConsoleColors.RESET);
                truck.setState(new ActionState());
            }
        }
        }
    }
    @Override
    public String getStatusName() { return "DOJAZD (" + timeRemaining + "s)"; }
}