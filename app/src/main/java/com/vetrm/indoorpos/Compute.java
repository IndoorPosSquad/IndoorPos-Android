package com.vetrm.indoorpos;

/**
 * Created by simpleon on 27/3/15.
 */
public class Compute {
    static int step = 1;
    static int pos = 0;

    static float[][] pseudolites = new float[][] {
            {10.0f, 10.0f, 3.0f},
            {0.0f, 0.0f, 3.0f},
            {0.0f, 10.0f, 3.0f}
    };

    static float[] solve3d(float[] pranges) {
        pos = setNewCount(pos);

        float x = pos + 1;
        float y = pos + 2;
        float z = pos + 3;

        return  new float[]{x, y, z};
    }

    static int setNewCount(int x) {
        if (Math.abs(pos) >= 20) {
            step = - step;
        }

        return x + step;
    }
}
