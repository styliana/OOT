package com.simulation.gui;

import com.simulation.Individual;
import com.simulation.Simulation;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Panel (płótno) do rysowania wizualizacji symulacji.
 */
public class SimulationPanel extends JPanel {

    private Simulation simulation;

    public SimulationPanel(Simulation simulation) {
        this.simulation = simulation;
        setPreferredSize(new java.awt.Dimension((int)simulation.getWidth(), (int)simulation.getHeight()));
        setBackground(Color.DARK_GRAY);
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Włączenie antyaliasingu dla ładniejszych kółek
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Kopiujemy listę, aby uniknąć błędów współbieżności podczas rysowania
        List<Individual> individualsCopy;
        synchronized (simulation.getIndividuals()) {
            individualsCopy = new java.util.ArrayList<>(simulation.getIndividuals());
        }

        // Rysuj każdego osobnika
        for (Individual ind : individualsCopy) {
            // Pobieramy kolor bezpośrednio ze stanu osobnika!
            g2d.setColor(ind.getCurrentState().getColor());

            int x = (int) ind.getPosition().getX();
            int y = (int) ind.getPosition().getY();

            // Rysuj kółko 8x8 pikseli
            g2d.fillOval(x - 4, y - 4, 8, 8);
        }

        // Rysuj czas symulacji
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.format("Czas: %.2f s", simulation.getSimulationTime()), 10, 20);
        g2d.drawString(String.format("Populacja: %d", individualsCopy.size()), 10, 35);
    }
}