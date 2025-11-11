package com.vector;

import com.vector.interfaces.IPolar2D;
import com.vector.interfaces.ISpherical3D;
import com.vector.interfaces.IVector;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        // Ustawienie Locale, aby separatorem dziesiętnym była kropka
        Locale.setDefault(Locale.US);

        // 1. Utworzenie trzech przykładowych wektorów
        IVector v1 = new Polar2DAdapter(new Vector2D(3, 4));
        IVector v2 = new Vector3DInheritance(1, 2, 3);
        IVector v3 = new Vector3DDecorator(5, 6, 7);

        List<IVector> vectors = Arrays.asList(v1, v2, v3);

        System.out.println("=========================================================");
        System.out.println("1. WSPÓŁRZĘDNE KARTEZJAŃSKIE I BIEGUNOWE/SFERYCZNE");
        System.out.println("=========================================================");

        // 2. Wyświetlenie współrzędnych dla każdego wektora
        for (int i = 0; i < vectors.size(); i++) {
            IVector v = vectors.get(i);
            System.out.printf("\n--- Wektor v%d (%s) ---\n", (i + 1), v.getClass().getSimpleName());
            // Układ kartezjański
            System.out.printf("   Wsp. kartezjańskie: %s\n", Arrays.toString(v.getComponents()));

            // Układ biegunowy (dla 2D) lub sferyczny (dla 3D)
            if (v instanceof IPolar2D) {
                IPolar2D polar = (IPolar2D) v;
                System.out.printf("   Wsp. biegunowe: (promień r = %.2f, kąt α = %.2f°)\n", polar.getAbs(), polar.getAngle());
            }
            if (v instanceof ISpherical3D) {
                ISpherical3D spherical = (ISpherical3D) v;
                System.out.printf("   Wsp. sferyczne: (promień r = %.2f, θ = %.2f°, φ = %.2f°)\n",
                        spherical.getRadius(), spherical.getTheta(), spherical.getPhi());
            }
        }

        System.out.println("\n=========================================================");
        System.out.println("2. ILOCZYN SKALARNY (wszystkie kombinacje)");
        System.out.println("=========================================================");

        // 3. Obliczenie iloczynu skalarnego dla wszystkich par
        for (int i = 0; i < vectors.size(); i++) {
            for (int j = 0; j < vectors.size(); j++) {
                IVector vec1 = vectors.get(i);
                IVector vec2 = vectors.get(j);
                System.out.printf("   v%d · v%d = %.2f\n", (i + 1), (j + 1), vec1.cdot(vec2));
            }
        }

        System.out.println("\n=========================================================");
        System.out.println("3. ILOCZYN WEKTOROWY (wszystkie kombinacje)");
        System.out.println("=========================================================");

        // 4. Obliczenie iloczynu wektorowego dla wszystkich par
        for (int i = 0; i < vectors.size(); i++) {
            for (int j = 0; j < vectors.size(); j++) {
                IVector vec1 = vectors.get(i);
                IVector vec2 = vectors.get(j);
                IVector resultVector = null;

                if (vec1 instanceof Vector3DInheritance) {
                    resultVector = ((Vector3DInheritance) vec1).cross(vec2);
                } else if (vec1 instanceof Vector3DDecorator) {
                    resultVector = ((Vector3DDecorator) vec1).cross(vec2);
                } else {
                    // Wektor 2D traktowany jako 3D z z=0
                    Vector3DInheritance tempVec3D = new Vector3DInheritance(vec1.getComponents()[0], vec1.getComponents()[1], 0);
                    resultVector = tempVec3D.cross(vec2);
                }
                System.out.printf("   v%d × v%d = %s\n", (i + 1), (j + 1), Arrays.toString(resultVector.getComponents()));
            }
        }
        System.out.println("=========================================================");
    }
}