package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RadarGUI extends Application {

    // Konfiguracja siatki
    private static final int GRID_W = 100;
    private static final int GRID_H = 80;
    private static final int CELL_SIZE = 8;

    // Komponenty logiczne
    private RadarGenerator generator;
    private OtsuBinarizer binarizer;
    private BlobExtractor extractor;
    private MHTTracker tracker;
    private List<SimulatedTarget> realTargets;
    private Random random = new Random();

    // Elementy interfejsu
    private Canvas radarCanvas;
    private Label statusLabel;
    private boolean isRunning = true;

    // Flagi widoczności
    private boolean showRaw = true;
    private boolean showBinary = false;
    private boolean showBlobs = true;
    private boolean showTracks = true;

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

        // --- PANEL BOCZNY (KONTROLNY) ---
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #333333; -fx-border-color: black; -fx-border-width: 2px;");

        Label lblTitle = new Label("RADAR MONITOR");
        lblTitle.setTextFill(Color.WHITE);
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button btnStartStop = new Button("START / STOP");
        btnStartStop.setMaxWidth(Double.MAX_VALUE);
        btnStartStop.setOnAction(e -> isRunning = !isRunning);

        // Opcje wyświetlania
        VBox viewOptions = new VBox(10);
        CheckBox cbRaw = createCheckBox("Pokaż Szum (Zielony)", true, v -> showRaw = v);
        CheckBox cbBin = createCheckBox("Pokaż Otsu (Biały)", false, v -> showBinary = v);
        CheckBox cbBlobs = createCheckBox("Pokaż Bloby (Czerwony)", true, v -> showBlobs = v);
        CheckBox cbTracks = createCheckBox("Pokaż Ścieżki (Cyan)", true, v -> showTracks = v);
        viewOptions.getChildren().addAll(cbRaw, cbBin, cbBlobs, cbTracks);

        // Suwak liczby celów (0-100)
        Label lblSlider = new Label("Liczba celów: 3");
        lblSlider.setTextFill(Color.WHITE);
        Slider targetSlider = new Slider(0, 100, 3);
        targetSlider.setShowTickLabels(true);
        targetSlider.setMajorTickUnit(25);
        targetSlider.valueProperty().addListener((obs, old, val) -> {
            int count = val.intValue();
            lblSlider.setText("Liczba celów: " + count);
            updateTargetCount(count);
        });

        // Pasek statusu
        statusLabel = new Label();
        statusLabel.setTextFill(Color.LIGHTGREEN);
        statusLabel.setFont(Font.font("Monospaced", 11));

        sidebar.getChildren().addAll(lblTitle, btnStartStop, new Separator(), viewOptions, new Separator(), lblSlider, targetSlider, statusLabel);

        // --- OBSZAR RADARU ---
        StackPane canvasPane = new StackPane();
        canvasPane.setStyle("-fx-background-color: black;");
        radarCanvas = new Canvas(GRID_W * CELL_SIZE, GRID_H * CELL_SIZE);
        canvasPane.getChildren().add(radarCanvas);

        BorderPane layout = new BorderPane();
        layout.setCenter(canvasPane);
        layout.setRight(sidebar);

        // Pętla animacji (Game Loop)
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                // Przetwarzanie klatki co ok. 66ms (15 FPS)
                if (isRunning && now - lastUpdate >= 66_000_000) {
                    updateSimulation();
                    lastUpdate = now;
                }
                draw();
            }
        };
        timer.start();

        primaryStage.setTitle("Laboratorium 5: Radar Tracking");
        primaryStage.setScene(new Scene(layout));
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
            realTargets.add(new SimulatedTarget(
                    random.nextInt(GRID_W - 10) + 5,
                    random.nextInt(GRID_H - 10) + 5,
                    (random.nextDouble() - 0.5) * 2.0,
                    (random.nextDouble() - 0.5) * 2.0
            ));
        }
        while (realTargets.size() > count) {
            realTargets.remove(realTargets.size() - 1);
        }
    }

    private void updateSimulation() {
        // 1. Ruch celów
        List<Blob.Point> targetPositions = new ArrayList<>();
        for (SimulatedTarget t : realTargets) {
            t.move();
            targetPositions.add(new Blob.Point((int)t.x, (int)t.y));
        }

        // 2. Generowanie ramki
        currentFrame = generator.generateFrame(targetPositions);

        // 3. Progowanie Otsu
        currentThreshold = binarizer.calculateThreshold(currentFrame, GRID_W, GRID_H);

        // 4. Ekstrakcja Blobów
        detectedBlobs = extractor.extract(currentFrame, currentThreshold, GRID_W, GRID_H);

        // 5. Śledzenie MHT
        tracker.updateTracks(detectedBlobs);
        activeTracks = tracker.getTracks();
    }

    private void draw() {
        if (currentFrame == null) return;
        GraphicsContext gc = radarCanvas.getGraphicsContext2D();

        // Czyść ekran
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, radarCanvas.getWidth(), radarCanvas.getHeight());

        // 1. Surowy szum (Zielony)
        if (showRaw) {
            for (int y = 0; y < GRID_H; y++) {
                for (int x = 0; x < GRID_W; x++) {
                    int val = currentFrame[y][x];
                    if (val > 25) {
                        double b = val / 255.0;
                        gc.setFill(Color.color(0, b, 0));
                        gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        // 2. Widok binarny (Białe kwadraty po Otsu)
        if (showBinary) {
            gc.setFill(Color.WHITE);
            for (int y = 0; y < GRID_H; y++) {
                for (int x = 0; x < GRID_W; x++) {
                    if (currentFrame[y][x] > currentThreshold) {
                        gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
                    }
                }
            }
        }

        // 3. Wykryte Bloby (Czerwone prostokąty)
        if (showBlobs && detectedBlobs != null) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(1.5);
            for (Blob b : detectedBlobs) {
                // Rozmiar ramki na podstawie odchylenia standardowego
                double size = Math.max(b.stdDevX, b.stdDevY) * 4;
                gc.strokeRect(
                        (b.meanX * CELL_SIZE) - size/2,
                        (b.meanY * CELL_SIZE) - size/2,
                        size, size
                );
            }
        }

        // 4. Ścieżki MHT (Linie Cyan z ID)
        if (showTracks && activeTracks != null) {
            for (Track t : activeTracks) {
                // Rysuj historię (ogon)
                if (t.history.size() > 1) {
                    gc.setStroke(Color.CYAN);
                    gc.setLineWidth(2);
                    gc.beginPath();
                    Track.DoublePoint start = t.history.get(0);
                    gc.moveTo(start.x * CELL_SIZE + 4, start.y * CELL_SIZE + 4);

                    for (int i = 1; i < t.history.size(); i++) {
                        Track.DoublePoint p = t.history.get(i);
                        gc.lineTo(p.x * CELL_SIZE + 4, p.y * CELL_SIZE + 4);
                    }
                    gc.stroke();
                }

                // Tekst z ID
                gc.setFill(Color.WHITE);
                gc.fillText("ID:" + t.id, t.x * CELL_SIZE + 10, t.y * CELL_SIZE);
            }
        }

        // Statystyki
        statusLabel.setText(String.format(
                "PRÓG OTSU: %d\n" +
                        "CELE REALNE: %d\n" +
                        "WYKRYTE BLOBY: %d\n" +
                        "AKTYWNE ŚCIEŻKI: %d",
                currentThreshold, realTargets.size(),
                (detectedBlobs != null ? detectedBlobs.size() : 0),
                (activeTracks != null ? activeTracks.size() : 0)));
    }

    // Helpery GUI
    private CheckBox createCheckBox(String text, boolean selected, java.util.function.Consumer<Boolean> action) {
        CheckBox cb = new CheckBox(text);
        cb.setSelected(selected);
        cb.setTextFill(Color.WHITE);
        cb.selectedProperty().addListener((obs, old, val) -> action.accept(val));
        return cb;
    }

    // Symulowany cel
    static class SimulatedTarget {
        double x, y, vx, vy;
        public SimulatedTarget(double x, double y, double vx, double vy) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
        }
        public void move() {
            x += vx; y += vy;
            // Odbicia od ścian
            if (x < 1 || x >= GRID_W - 1) vx = -vx;
            if (y < 1 || y >= GRID_H - 1) vy = -vy;
        }
    }
}