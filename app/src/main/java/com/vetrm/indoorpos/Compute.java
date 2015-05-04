package com.vetrm.indoorpos;

import android.util.Log;

import java.util.Arrays;

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



    private static float sgn(float x) {
        if (x > 0) {
            return 1.0f;
        } else {
            return -1.0f;
        }
    }

    private static float S(float x) {
        return x * x;
    }

    private static float Det(float arcs[][],int n) {
        int i, j, k;
        if (n == 2)
            return arcs[0][0] * arcs[1][1] - arcs[1][0] * arcs[0][1];
        float ans = 0;
        float[][] tmp = new float[3][3];

        for (i = 0; i < n; i++){
            for (j = 0; j < n-1; j++)
                for(k = 0; k < n-1; k++)
                    tmp[j][k] = arcs[j+1][(k >= i) ? k+1 : k];
            float t = Det(tmp, n-1);
            if(i % 2 == 0)
                ans += arcs[0][i] * t;
            else
                ans -= arcs[0][i] * t;
        }
        if (ans == 0) {
            ans = 0.01f;
        }
        return ans;
    }

    private static void Solve2d(float reciever[][], float pseudolites[][],
                                float pranges1, float pranges2) {

        float[] origin = new float[2];
        float len;
        float tan_theta;
        float sin_theta;
        float cos_theta;
        float d1;
        float h1;
        float[][] invrotation = new float[2][2];
        float[] pranges = new float[2];

        pranges[0] = pranges1;
        pranges[1] = pranges2;

        origin[0] = pseudolites[0][0];
        origin[1] = pseudolites[0][1];

        pseudolites[0][0] = 0;
        pseudolites[0][1] = 0;
        pseudolites[1][0] = pseudolites[1][0] - origin[0];
        pseudolites[1][1] = pseudolites[1][1] - origin[1];

        len = (float) Math.sqrt(Math.pow(pseudolites[1][0], 2) + Math.pow(pseudolites[1][1], 2));

        tan_theta = pseudolites[1][1] / pseudolites[1][0];
        cos_theta = (float) (sgn(pseudolites[1][0]) / Math.sqrt(Math.pow(tan_theta, 2) + 1));
        sin_theta = (float) (sgn(pseudolites[1][1]) * Math.abs(tan_theta) / Math.sqrt(Math.pow(tan_theta, 2)+1));

        invrotation[0][0] = cos_theta;
        invrotation[0][1] = -sin_theta;
        invrotation[1][0] = sin_theta;
        invrotation[1][1] = cos_theta;

        d1 = (float) (((Math.pow(pranges[0], 2) - Math.pow(pranges[1], 2)) / len + len ) / 2);

        h1 = (float) Math.sqrt(Math.pow(pranges[0], 2) - Math.pow(d1, 2));

        reciever[0][0] = d1;
        reciever[0][1] = h1;
        reciever[1][0] = d1;
        reciever[1][1] = -h1;

        reciever[0][0] = invrotation[0][0]*d1 + invrotation[0][1]*h1;
        reciever[0][1] = invrotation[1][0]*d1 + invrotation[1][1]*h1;
        reciever[0][0] += origin[0];
        reciever[0][1] += origin[1];

        reciever[1][0] = invrotation[0][0]*d1 + invrotation[0][1]*-h1;
        reciever[1][1] = invrotation[1][0]*d1 + invrotation[1][1]*-h1;
        reciever[1][0] += origin[0];
        reciever[1][1] += origin[1];
    }


    public static XYZ Solve3d(XYZ[] pl_xyz, float pranges[]) {
        XYZ result = new XYZ();

        XYZ[] txyz = new XYZ[]{new XYZ(), new XYZ(), new XYZ()};
        int i, j;
        for (i = 0; i < 3; i++) {
            txyz[i].x = pl_xyz[i].x;
            txyz[i].y = pl_xyz[i].y;
            txyz[i].z = pl_xyz[i].z;
        }

        float h_BA, h_CA;
        h_BA = txyz[1].z - txyz[0].z;
        h_CA = txyz[2].z - txyz[0].z;
        txyz[1].z = pl_xyz[0].z;
        txyz[2].z = pl_xyz[0].z;

        float _AB, _AC, _BC, _cos_BAC;
        _AB = (float) Math.sqrt(Math.pow(txyz[1].x - txyz[0].x, 2) +
                Math.pow(txyz[1].y - txyz[0].y, 2) +
                Math.pow(txyz[1].z - txyz[0].z, 2));
        _AC = (float) Math.sqrt(Math.pow(txyz[2].x - txyz[0].x, 2) +
                Math.pow(txyz[2].y - txyz[0].y, 2) +
                Math.pow(txyz[2].z - txyz[0].z, 2));
        _BC = (float) Math.sqrt(Math.pow(txyz[2].x - txyz[1].x, 2) +
                Math.pow(txyz[2].y - txyz[1].y, 2) +
                Math.pow(txyz[2].z - txyz[1].z, 2));
        _cos_BAC = (float) ((Math.pow(_AC, 2) + Math.pow(_AB, 2) - Math.pow(_BC, 2)) /
                (2 * _AC * _AB));

        //计算变换矩阵
        float[][] _trans = new float[3][3];
        //X
        _trans[0][0] = (txyz[1].x - txyz[0].x) / _AB;
        _trans[0][1] = (txyz[1].y - txyz[0].y) / _AB;
        _trans[0][2] = (txyz[1].z - txyz[0].z) / _AB;

        float _d, _H;
        _d = _AC * _cos_BAC;
        _H = (float) Math.sqrt(Math.pow(_AC, 2) - Math.pow(_d, 2));

        float[] D = new float[3];
        D[0] = txyz[0].x + _d * _trans[0][0];
        D[1] = txyz[0].y + _d * _trans[0][1];
        D[2] = txyz[0].z + _d * _trans[0][2];

        //Y
        _trans[1][0] = (txyz[2].x - D[0]) / _H;
        _trans[1][1] = (txyz[2].y - D[1]) / _H;
        _trans[1][2] = (txyz[2].z - D[2]) / _H;

        _trans[2][0] = 0;
        _trans[2][1] = 0;
        _trans[2][2] = 1;

        float AB, AC, BC, cos_BAC;
        AB = (float) Math.sqrt(Math.pow(pl_xyz[1].x - pl_xyz[0].x, 2) +
                Math.pow(pl_xyz[1].y - pl_xyz[0].y, 2) +
                Math.pow(pl_xyz[1].z - pl_xyz[0].z, 2));
        AC = (float) Math.sqrt(Math.pow(pl_xyz[2].x - pl_xyz[0].x, 2) +
                Math.pow(pl_xyz[2].y - pl_xyz[0].y, 2) +
                Math.pow(pl_xyz[2].z - pl_xyz[0].z, 2));
        BC = (float) Math.sqrt(Math.pow(pl_xyz[2].x - pl_xyz[1].x, 2) +
                Math.pow(pl_xyz[2].y - pl_xyz[1].y, 2) +
                Math.pow(pl_xyz[2].z - pl_xyz[1].z, 2));
        cos_BAC = (float) (Math.pow(AC, 2) + Math.pow(AB, 2) - Math.pow(BC, 2)) /
                (2 * AC * AB);

        float d, H;
        d = AC * cos_BAC;
        H = (float) Math.sqrt(Math.pow(AC, 2) - Math.pow(d, 2));

        //计算变换矩阵2
        float[][] trans = new float[3][3];
        //X
        trans[0][0] = _AB / AB;
        trans[0][1] = 0;
        trans[0][2] = h_BA / AB;

        trans[1][0] = (_d - (_AB * d / AB)) / H;
        trans[1][1] = _H / H;
        trans[1][2] = (h_CA - (h_BA * d / AB)) / H;

        //求余子式
        float[][] tmp =new float[3][3];
        for (i = 0; i < 3; i++)
            for (j = 0; j < 3; j++)
                tmp[i][j] = trans[i][j];

        trans[2][0] = tmp[0][1] * tmp[1][2] - tmp[1][1] * tmp[0][2];
        trans[2][1] = -(tmp[0][0] * tmp[1][2] - tmp[0][2] * tmp[1][0]);
        trans[2][2] = tmp[0][0] * tmp[1][1] - tmp[0][1] * tmp[1][0];


        float temp;
        temp = (float) Math.sqrt(Math.pow(trans[2][0], 2) +
                Math.pow(trans[2][1], 2) +
                Math.pow(trans[2][2], 2));

        trans[2][0] /= temp;
        trans[2][1] /= temp;
        trans[2][2] /= temp;
        // 求余子式结束

        float h, d1, d2, d3, d4, COS, X, Y, err;
        h = 0.0f;
        d1 = 0.0f; d2 = 0.0f; d3 = 0.0f; d4 = 0.0f;
        COS = 0.0f;
        X = 0.0f; Y = 0.0f;
        err = 0.0f;

        // 海仑三角公式求底面积
        float _P = (BC + AB + AC) / 2;
        float _S = (float) Math.sqrt(_P * (_P - BC) * (_P - AC) * (_P - AB));

        // 欧拉四面体公式求体积
        float _n = BC;
        float _q = AC;
        float _p = AB;
        float _r = pranges[0];
        float _m = pranges[1];
        float _l = pranges[2];

        float[][] _eular_det = new float[][]
                {{S(_p), (S(_p)+S(_q)-S(_n))/2, (S(_p)+S(_r)-S(_m))/2},
                        {(S(_p)+S(_q)-S(_n))/2, S(_q), (S(_q)+S(_r)-S(_l))/2},
                        {(S(_p)+S(_r)-S(_m))/2, (S(_q)+S(_r)-S(_l))/2, S(_r)}};

        float _V2 = Det(_eular_det, 3);
        float _V = (float) Math.sqrt(_V2/36.0);

        // 求未知点到三卫星平面的高度
        h = _V / _S * 3.0f;

        // 由高度得未知点的横纵坐标
        d1 = (float) Math.sqrt(Math.pow(pranges[0], 2) - Math.pow(h, 2));
        d2 = (float) Math.sqrt(Math.pow(pranges[1], 2) - Math.pow(h, 2));
        d3 = (float) Math.sqrt(Math.pow(pranges[2], 2) - Math.pow(h, 2));
        COS = (d1 * d1 + AB * AB - d2 * d2) / (2 * d1 * AB);
        X = d1 *COS;
        Y = (float) Math.sqrt(Math.pow(d1, 2) - Math.pow(X, 2));

        // 未知点在变换空间的坐标
        float[] res = new float[3];
        res[0] = X;
        res[1] = Y;

        if (App.getInstance().isDown()) {
            res[2] = -h;
        } else {
            res[2] = h;
        }

        // 分两步将结果变换回原坐标系
        float[] target_xyz = new float[3];
        // 矩阵乘 target_xyz = res * trans
        for (i = 0; i < 3; i++) {
            target_xyz[i] = 0.0f;
            for (j = 0; j < 3; j++)
                target_xyz[i] += res[j] * trans[j][i];
        }

        float[] ans = new float[3];
        // 矩阵乘 ans = target_xyz * _trans
        for (i = 0; i < 3; i++) {
            ans[i] = 0.0f;
            for (j = 0; j < 3; j++)
                ans[i] += target_xyz[j] * _trans[j][i];
        }

        // 变换坐标系是以0卫星的坐标作为原点
        // 因此将卫星0的坐标加上去
        result.x = ans[0] + pl_xyz[0].x;
        result.y = ans[1] + pl_xyz[0].y;
        result.z = ans[2] + pl_xyz[0].z;

        return result;
    }

    private static void log(Object obj) {
        Log.d("Compute", "" + obj);
    }

}

class XYZ {
    public float x;
    public float y;
    public float z;

    public XYZ() {
        x = 0.0f;
        y = 0.0f;
        z = 0.0f;
    }

    public XYZ(int x, int y, int z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }

    public XYZ(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "XYZ{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
