public class Location {
    private double lat;
    private double lon;

    public Location(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() { return lat; }
    public double getLon() { return lon; }

    // Prosta odległość euklidesowa (wystarczająca na potrzeby symulacji lab)
    // W rzeczywistości użyłoby się wzoru Haversine
    public double distanceTo(Location other) {
        return Math.sqrt(Math.pow(this.lat - other.lat, 2) + Math.pow(this.lon - other.lon, 2));
    }

    @Override
    public String toString() {
        return String.format("[%.4f, %.4f]", lat, lon);
    }
}