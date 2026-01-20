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

    // Ustawienia siatki
    private static final int GRID_W = 100;
    private static final int GRID_H = 80;
    private static final int CELL_SIZE = 8;

    // Algorytmy i logika
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

    // Opcje wyświetlania
    private boolean showRaw = true;
    private boolean showBlobs = true;
    private boolean showTracks = true;

    // Dane aktualnej klatki
    private int[][] currentFrame;
    private List<Blob> detectedBlobs;
    private List<Track> activeTracks;
    private int currentThreshold;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initLogic();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        // Konfiguracja Canvas
        radarCanvas = new Canvas(GRID_W * CELL_SIZE, GRID_H * CELL_SIZE);
        VBox canvasContainer = new VBox(radarCanvas);
        canvasContainer.setPadding(new Insets(10));
        canvasContainer.setStyle("-fx-border-color: #444; -fx-border-width: 2px;");
        root.setCenter(canvasContainer);

        // Panel sterowania
        VBox bottomPanel = new VBox(10);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle("-fx-background-color: #333;");

        // Wiersz 1: Kontrolki widoku
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
        lblSlider.setMinWidth(120);

        Slider targetSlider = new Slider(0, 100, 3);
        targetSlider.setShowTickLabels(true);
        targetSlider.setShowTickMarks(true);
        targetSlider.setMajorTickUnit(10);
        targetSlider.setPrefWidth(500);

        targetSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int count = newVal.intValue();
            lblSlider.setText("Liczba celów: " + count);
            updateTargetCount(count);
        });

        sliderBox.getChildren().addAll(lblSlider, targetSlider);

        // Wiersz 3: Pasek statusu
        statusLabel = new Label("Inicjalizacja...");
        statusLabel.setTextFill(Color.LIGHTGRAY);

        bottomPanel.getChildren().addAll(controls, sliderBox, statusLabel);
        root.setBottom(bottomPanel);

        // Pętla animacji (Game Loop) - ok. 15-20 FPS dla stabilności MHT
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (isRunning && now - lastUpdate >= 60_000_000) {
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
        updateTargetCount(3);
    }

    private void updateTargetCount(int count) {
        while (realTargets.size() < count) {
            double x = random.nextInt(GRID_W - 10) + 5;
            double y = random.nextInt(GRID_H - 10) + 5;
            double vx = (random.nextDouble() - 0.5) * 1.5;
            double vy = (random.nextDouble() - 0.5) * 1.5;
            realTargets.add(new SimulatedTarget(x, y, vx, vy));
        }
        while (realTargets.size() > count) {
            realTargets.remove(realTargets.size() - 1);
        }
    }

    private void updateSimulation() {
        // 1. Ruch celów (Świat rzeczywisty)
        List<Blob.Point> targetPositions = new ArrayList<>();
        for (SimulatedTarget t : realTargets) {
            t.move();
            targetPositions.add(new Blob.Point((int)t.x, (int)t.y));
        }

        // 2. Generowanie obrazu radaru (Sensor)
        currentFrame = generator.generateFrame(targetPositions);

        // 3. Przetwarzanie sygnału (Otsu + CCL) [cite: 16, 46]
        currentThreshold = binarizer.calculateThreshold(currentFrame, GRID_W, GRID_H);
        detectedBlobs = extractor.extract(currentFrame, currentThreshold, GRID_W, GRID_H);

        // 4. Śledzenie (MHT - Asocjacja probabilistyczna) [cite: 88, 108]
        tracker.updateTracks(detectedBlobs);
        activeTracks = tracker.getTracks();
    }

    private void draw() {
        if (currentFrame == null) return;

        GraphicsContext gc = radarCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, radarCanvas.getWidth(), radarCanvas.getHeight());

        // Rysowanie surowego sygnału
        if (showRaw) {
            for (int y = 0; y < GRID_H; y++) {
                for (int x = 0; x < GRID_W; x++) {
                    int val = currentFrame[y][x];
                    if (val > 25) { // Próg wizualny szumu
                        double brightness = val / 255.0;
                        gc.setFill(Color.hsb(120, 0.8, brightness));
                        gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        // Rysowanie wykrytych blobów (Bounding Boxes) [cite: 43]
        if (showBlobs && detectedBlobs != null) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(1.2);
            for (Blob b : detectedBlobs) {
                // Rozmiar ramki skalowany odchyleniem standardowym [cite: 68]
                double size = Math.max(b.stdDevX, b.stdDevY) * 5;
                gc.strokeRect(
                        (b.meanX * CELL_SIZE) - size/2,
                        (b.meanY * CELL_SIZE) - size/2,
                        size, size
                );
            }
        }

        // Rysowanie ścieżek MHT (Trajektorie historyczne) [cite: 10, 89]
        if (showTracks && activeTracks != null) {
            gc.setLineWidth(1.5);
            for (Track t : activeTracks) {
                if (t.history.size() > 1) {
                    gc.setStroke(Color.CYAN);
                    gc.beginPath();
                    // Użycie DoublePoint dla płynności linii
                    Track.DoublePoint start = t.history.get(0);
                    gc.moveTo(start.x * CELL_SIZE + CELL_SIZE/2.0, start.y * CELL_SIZE + CELL_SIZE/2.0);

                    for (int i = 1; i < t.history.size(); i++) {
                        Track.DoublePoint p = t.history.get(i);
                        gc.lineTo(p.x * CELL_SIZE + CELL_SIZE/2.0, p.y * CELL_SIZE + CELL_SIZE/2.0);
                    }
                    gc.stroke();
                }

                // Identyfikator ścieżki
                gc.setFill(Color.WHITE);
                gc.fillText("ID:" + t.id, t.x * CELL_SIZE + 8, t.y * CELL_SIZE - 5);
            }
        }

        statusLabel.setText(String.format("Próg Otsu: %d | Obiekty (Realne): %d | Bloby: %d | Ścieżki: %d",
                currentThreshold,
                realTargets.size(),
                (detectedBlobs != null ? detectedBlobs.size() : 0),
                (activeTracks != null ? activeTracks.size() : 0)));
    }

    static class SimulatedTarget {
        double x, y, vx, vy;
        public SimulatedTarget(double x, double y, double vx, double vy) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
        }
        public void move() {
            x += vx; y += vy;
            // Odbicia od granic obszaru roboczego
            if (x < 2 || x >= GRID_W - 2) vx = -vx;
            if (y < 2 || y >= GRID_H - 2) vy = -vy;
        }
    }
}