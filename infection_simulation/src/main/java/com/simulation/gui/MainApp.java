package com.simulation.gui;

import com.simulation.Simulation;
import com.simulation.memento.SimulationCaretaker;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import javax.swing.JOptionPane; // NOWY IMPORT

/**
 * Główna klasa uruchomieniowa.
 * Tworzy okno (JFrame), panel (JPanel) i pętlę symulacji (Timer).
 */
public class MainApp {

    public static void main(String[] args) {
        final int AREA_WIDTH = 500;
        final int AREA_HEIGHT = 400;
        final int INITIAL_POPULATION = 60;
        final int FRAME_DELAY_MS = 40;

        // Pytam użytkownika, czy włączyć odporność początkową
        int choice = JOptionPane.showConfirmDialog(
                null,
                "Czy chcesz, aby część populacji posiadała odporność początkową? (Przypadek 2)\n" +
                        "(Wybierz 'Nie' dla Przypadku 1, gdzie wszyscy są wrażliwi)",
                "Wybór Trybu Symulacji",
                JOptionPane.YES_NO_OPTION
        );

        // Ustawiamy szansę na odporność (np. 30% jeśli 'Tak', 0% jeśli 'Nie')
        double initialImmunityChance = (choice == JOptionPane.YES_OPTION) ? 0.3 : 0.0;
        // ---------------------------------------------

        // Przekazujemy nową zmienną do konstruktora Symulacji
        Simulation simulation = new Simulation(AREA_WIDTH, AREA_HEIGHT, INITIAL_POPULATION, initialImmunityChance);
        SimulationCaretaker caretaker = new SimulationCaretaker(simulation);

        JFrame frame = new JFrame("Symulacja Epidemii (Laboratorium 3)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        SimulationPanel panel = new SimulationPanel(simulation);
        frame.add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Zapisz Stan");
        JButton loadButton = new JButton("Wczytaj Stan");

        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> caretaker.saveState());
        loadButton.addActionListener(e -> {
            caretaker.restoreLastState();
            panel.repaint();
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer simulationTimer = new Timer(FRAME_DELAY_MS, e -> {
            simulation.step();
            panel.repaint();
        });

        simulationTimer.start();
    }
}