import java.util.Random;

// Stan: Działania na miejscu
public class ActionState implements ICarState {
    private int timeRemaining;

    public ActionState() {
        // Czas działań 5-25s
        this.timeRemaining = 5 + new Random().nextInt(21);
    }

    @Override
    public void handleTick(FireTruck truck) {
        if (timeRemaining > 0) {
            timeRemaining--;
        } else {
            // Kolorowy komunikat o zakończeniu i powrocie (Zielony)
            System.out.println(ConsoleColors.GREEN + "[" + truck.getUnitName() + "] Samochód " + truck.getId() + ": Działania zakończone. Powrót." + ConsoleColors.RESET);
            truck.setState(new ReturningState());
        }
    }

    @Override
    public String getStatusName() { return "AKCJA (" + timeRemaining + "s)"; }
}