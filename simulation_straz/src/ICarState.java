public interface ICarState {
    void handleTick(FireTruck truck); // Metoda wywoływana w każdej sekundzie symulacji
    String getStatusName();
}