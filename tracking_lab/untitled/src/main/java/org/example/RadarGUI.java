package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RadarGUI extends Application {

    // Ustawienia
    private static final int GRID_W = 100;
    private static final int GRID_H = 80;
    private static final int CELL_SIZE = 8;

    // Algorytmy
    private RadarGenerator generator;
    private OtsuBinarizer binarizer;
    private BlobExtractor extractor;
    private MHTTracker tracker;
    private List<SimulatedTarget> realTargets;
    private Random random = new Random();

    // Elementy GUI
    private Canvas radarCanvas;
    private Label statusLabel;
    private boolean isRunning = true;

    // Opcje widoku
    private boolean showRaw = true;
    private boolean showBlobs = true;
    private boolean showTracks = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // 1. Inicjalizacja logiki
        initLogic();

        // 2. Budowa interfejsu
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        // Płótno do rysowania (Canvas)
        radarCanvas = new Canvas(GRID_W * CELL_SIZE, GRID_H * CELL_SIZE);
        VBox canvasContainer = new VBox(radarCanvas);
        canvasContainer.setPadding(new Insets(10));
        canvasContainer.setStyle("-fx-border-color: #444; -fx-border-width: 2px;");
        root.setCenter(canvasContainer);

        // Panel sterowania (Dół)
        VBox bottomPanel = new VBox(10);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle("-fx-background-color: #333;");

        // Wiersz 1: Przyciski i Checkboxy
        HBox controls = new HBox(15);
        Button btnPause = new Button("Pauza / Start");
        btnPause.setOnAction(e -> isRunning = !isRunning);

        CheckBox cbRaw = new CheckBox("Surowe dane");
        cbRaw.setSelected(true);
        cbRaw.setTextFill(Color.WHITE);
        cbRaw.selectedProperty().addListener((obs, old, val) -> showRaw = val);

        CheckBox cbBlobs = new CheckBox("Wykryte Bloby");
        cbBlobs.setSelected(true);
        cbBlobs.setTextFill(Color.WHITE);
        cbBlobs.selectedProperty().addListener((obs, old, val) -> showBlobs = val);

        CheckBox cbTracks = new CheckBox("Ścieżki (MHT)");
        cbTracks.setSelected(true);
        cbTracks.setTextFill(Color.WHITE);
        cbTracks.selectedProperty().addListener((obs, old, val) -> showTracks = val);

        controls.getChildren().addAll(btnPause, cbRaw, cbBlobs, cbTracks);

        // Wiersz 2: Suwak liczby celów
        HBox sliderBox = new HBox(10);
        Label lblSlider = new Label("Liczba celów: 3");
        lblSlider.setTextFill(Color.WHITE);
        lblSlider.setMinWidth(100);

        Slider targetSlider = new Slider(0, 100, 3); // Min 0, Max 100, Start 3
        targetSlider.setShowTickLabels(true);
        targetSlider.setShowTickMarks(true);
        targetSlider.setMajorTickUnit(10);
        targetSlider.setBlockIncrement(1);
        targetSlider.setPrefWidth(400);

        targetSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int count = newVal.intValue();
            lblSlider.setText("Liczba celów: " + count);
            updateTargetCount(count);
        });

        sliderBox.getChildren().addAll(lblSlider, targetSlider);

        // Wiersz 3: Status
        statusLabel = new Label("Inicjalizacja...");
        statusLabel.setTextFill(Color.LIGHTGRAY);

        bottomPanel.getChildren().addAll(controls, sliderBox, statusLabel);
        root.setBottom(bottomPanel);

        // 3. Pętla animacji (Game Loop)
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // Ograniczenie do ok. 15 FPS dla czytelności symulacji
                if (isRunning && now - lastUpdate >= 66_000_000) {
                    updateSimulation();
                    lastUpdate = now;
                }
                draw();
            }
        };
        timer.start();

        Scene scene = new Scene(root);
        primaryStage.setTitle("Laboratorium 5: Radar Tracking System (MHT)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initLogic() {
        generator = new RadarGenerator(GRID_W, GRID_H);
        binarizer = new OtsuBinarizer();
        extractor = new BlobExtractor();
        tracker = new MHTTracker();
        realTargets = new ArrayList<>();

        // Startujemy z 3 celami
        updateTargetCount(3);
    }

    // Dynamiczna aktualizacja liczby celów
    private void updateTargetCount(int count) {
        // Jeśli mamy za mało - dodajemy
        while (realTargets.size() < count) {
            double x = random.nextInt(GRID_W - 10) + 5;
            double y = random.nextInt(GRID_H - 10) + 5;
            // Losowa prędkość między -1.0 a 1.0
            double vx = (random.nextDouble() - 0.5) * 2.0;
            double vy = (random.nextDouble() - 0.5) * 2.0;
            realTargets.add(new SimulatedTarget(x, y, vx, vy));
        }
        // Jeśli mamy za dużo - usuwamy
        while (realTargets.size() > count) {
            realTargets.remove(realTargets.size() - 1);
        }
    }

    // --- LOGIKA SYMULACJI ---
    private int[][] currentFrame;
    private List<Blob> detectedBlobs;
    private List<Track> activeTracks;
    private int currentThreshold;

    private void updateSimulation() {
        // 1. Ruch celów
        List<Blob.Point> targetPositions = new ArrayList<>();
        for (SimulatedTarget t : realTargets) {
            t.move();
            targetPositions.add(new Blob.Point((int)t.x, (int)t.y));
        }

        // 2. Generowanie radaru
        currentFrame = generator.generateFrame(targetPositions);

        // 3. Przetwarzanie
        currentThreshold = binarizer.calculateThreshold(currentFrame, GRID_W, GRID_H);
        detectedBlobs = extractor.extract(currentFrame, currentThreshold, GRID_W, GRID_H);

        // 4. Śledzenie
        tracker.updateTracks(detectedBlobs);
        activeTracks = tracker.getTracks();
    }

    // --- RYSOWANIE ---
    private void draw() {
        if (currentFrame == null) return;

        GraphicsContext gc = radarCanvas.getGraphicsContext2D();

        // Tło
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, radarCanvas.getWidth(), radarCanvas.getHeight());

        // Surowe dane (Piksele)
        if (showRaw) {
            for (int y = 0; y < GRID_H; y++) {
                for (int x = 0; x < GRID_W; x++) {
                    int val = currentFrame[y][x];
                    if (val > 20) {
                        double brightness = val / 255.0;
                        gc.setFill(Color.hsb(120, 1.0, brightness)); // Zieleń
                        gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        // Bloby (Czerwone ramki)
        if (showBlobs && detectedBlobs != null) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(1);
            for (Blob b : detectedBlobs) {
                double size = Math.max(b.stdDevX, b.stdDevY) * 4;
                gc.strokeRect(
                        (b.meanX * CELL_SIZE) - size/2,
                        (b.meanY * CELL_SIZE) - size/2,
                        size, size
                );
            }
        }

        // Ścieżki MHT (Linie)
        if (showTracks && activeTracks != null) {
            gc.setStroke(Color.CYAN);
            gc.setLineWidth(2);
            gc.setFill(Color.WHITE);

            for (Track t : activeTracks) {
                if (t.history.size() > 1) {
                    gc.beginPath();
                    Blob.Point start = t.history.get(0);
                    gc.moveTo(start.x * CELL_SIZE + CELL_SIZE/2.0, start.y * CELL_SIZE + CELL_SIZE/2.0);

                    for (int i = 1; i < t.history.size(); i++) {
                        Blob.Point p = t.history.get(i);
                        gc.lineTo(p.x * CELL_SIZE + CELL_SIZE/2.0, p.y * CELL_SIZE + CELL_SIZE/2.0);
                    }
                    gc.stroke();
                }
                gc.fillText("ID:" + t.id, t.x * CELL_SIZE + 10, t.y * CELL_SIZE);
            }
        }

        statusLabel.setText(String.format("Próg Otsu: %d | Obiekty (Realne): %d | Wykryte Bloby: %d | Ścieżki: %d",
                currentThreshold,
                realTargets.size(),
                (detectedBlobs != null ? detectedBlobs.size() : 0),
                (activeTracks != null ? activeTracks.size() : 0)));
    }

    // Cel symulowany
    static class SimulatedTarget {
        double x, y, vx, vy;
        public SimulatedTarget(double x, double y, double vx, double vy) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
        }
        public void move() {
            x += vx; y += vy;
            // Odbijanie od ścian
            if (x < 2 || x >= GRID_W - 2) vx = -vx;
            if (y < 2 || y >= GRID_H - 2) vy = -vy;
        }
    }
}