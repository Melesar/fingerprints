package Biometric.Functions;

import org.apache.commons.math3.complex.Complex;

import java.util.Arrays;
import java.util.Objects;

public class DCT {

    public  void transform(double[] vector) {
        Objects.requireNonNull(vector);
        Complex [] temp;
        int len = vector.length;
        int halfLen = len / 2;
        double[] real = new double[len];
        for (int i = 0; i < halfLen; i++) {
            real[i] = vector[i * 2];
            real[len - 1 - i] = vector[i * 2 + 1];
        }
        if (len % 2 == 1)
            real[halfLen] = vector[len - 1];
        Arrays.fill(vector, 0.0);
        FFT.fft(real,vector,true);


        for (int i = 0; i < len; i++) {
            double tem = i * Math.PI / (len * 2);
            double element=real[i]*Math.cos(tem)+vector[i]*Math.sin(tem);
            if(element>2&&element<13){
                vector[i] =element;
            }
            else
            {
                vector[i] =0;
            }

        }
    }


}
