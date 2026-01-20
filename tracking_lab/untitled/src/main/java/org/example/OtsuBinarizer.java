package org.example;

public class OtsuBinarizer {

    public int calculateThreshold(int[][] image, int width, int height) {
        int[] histogram = new int[256];
        int totalPixels = width * height;

        // 1. Budowa histogramu
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                histogram[image[y][x]]++;
            }
        }

        float sumTotal = 0;
        for (int i = 0; i < 256; i++) sumTotal += i * histogram[i];

        float sumB = 0;
        int wB = 0;
        int wF = 0;
        float varMax = 0;
        int threshold = 0;

        // 2. Iteracja w poszukiwaniu max wariancji międzyklasowej
        for (int t = 0; t < 256; t++) {
            wB += histogram[t];
            if (wB == 0) continue;

            wF = totalPixels - wB;
            if (wF == 0) break;

            sumB += (float) (t * histogram[t]);

            float mB = sumB / wB;
            float mF = (sumTotal - sumB) / wF;

            // Wzór na wariancję międzyklasową (sigma_b^2)
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }
        return threshold;
    }
}