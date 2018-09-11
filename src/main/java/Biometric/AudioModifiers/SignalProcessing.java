/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biometric.AudioModifiers;

import java.util.*;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/*
 * @author Patryk
 */
public class SignalProcessing {

    private static final float MEL = 1127.01048f;
    private static final float EZERO = -1.0E10f;

    static int dct_table_size = 0;
    static float[] dct_workspace;
    static double M_2PI = 2 * Math.PI;
    static float[] pLocalReal;
    static float[] pLocalImag;
    static float[] pWeightReal;
    static float[] pWeightImag;

    static int dct_table_size_fft = 0;
    static double[] dct_workspace2;
    static double[] pLocalReal2;
    static double[] pLocalImag2;
    static double[] pWeightReal2;
    static double[] pWeightImag2;
    FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

    public double Freq2Mel(double freq) {

        return MEL * Math.log(freq / 700 + 1.0);
    }

    public double Sample2Mel(int sample, int num, double fs) {
        double freq;
        freq = (double) (sample + 1) / (double) (num) * (fs / 2.0);
        return Freq2Mel(freq);

    }

    double CalcEnergy(double[] x, double eps, int leng) {
        double energy = 0.0;
        for (int k = 0; k < leng; k++) {
            energy += Math.pow(x[k], 2);
        }
        return ((energy <= 0) ? EZERO : Math.log(energy));
    }

    public void Hamming(double[] x, int leng) {
        double arg;
        arg = M_2PI / (leng - 1);
        for (int k = 0; k < leng; k++) {
            x[k] *= (0.54 - 0.46 * Math.cos(k * arg));

        }
    }

    public void DFT(float[] pReal, float[] pImag, int nDFTLength) {
        float[] pTempReal;
        float[] pTempImag;
        pTempReal = Arrays.copyOf(pReal, pReal.length);
        pTempImag = Arrays.copyOf(pImag, pImag.length);
        for (int k = 0; k < nDFTLength; k++) {
            int dTempReal = 0;
            int dTempImag = 0;
            for (int n = 0; n < nDFTLength; n++) {
                dTempReal += pTempReal[n]
                        * Math.cos(2.0 * Math.PI * n * k / (double) nDFTLength)
                        + pTempImag[n] * Math.sin(2.0 * Math.PI * n * k / (double) nDFTLength);
                dTempImag
                        += -pTempReal[n]
                        * Math.sin(2.0 * Math.PI * n * k / (double) nDFTLength)
                        + pTempImag[n] * Math.cos(2.0 * Math.PI * n * k / (double) nDFTLength);
            }
            pReal[k] = dTempReal;
            pImag[k] = dTempImag;

        }
        pTempReal = null;
        pTempImag = null;
    }

    public void DCT_Creat_Table_FFT(int nSize) {
        int k, n;

        if (nSize == dct_table_size_fft) {
           
        } else {
            if (dct_workspace2 != null) {
            }
            dct_workspace2 = null;
        }
        pLocalReal2 = null;
        pLocalImag2 = null;
        pWeightReal2 = null;
        pWeightImag2 = null;

        if (nSize <= 0) {
            dct_table_size_fft = 0;
            
        } else {
            dct_table_size_fft = nSize;
            dct_workspace2 = new double[dct_table_size_fft * 6];
            pWeightReal2 = dct_workspace2;
            pWeightImag2 = new double[dct_workspace2.length + dct_table_size_fft];
            pLocalReal2 = new double[dct_workspace2.length + 2 * dct_table_size_fft];
            pLocalImag2 = new double[dct_workspace2.length + 4 * dct_table_size_fft];
            for (k = 0; k < dct_table_size_fft; k++) {
                pWeightReal2[k] = Math.cos(k * Math.PI / (2.0 * dct_table_size_fft))
                        / Math.sqrt(2.0 * dct_table_size_fft);
                
                pWeightImag2[k] =
             -Math.sin(k * Math.PI / (2.0 * dct_table_size_fft)) /
             Math.sqrt(2.0 * dct_table_size_fft);
            }
            pWeightReal2[0] /= Math.sqrt(2.0);
      pWeightImag2[0] /= Math.sqrt(2.0);
            
        }
    }
    
   public void DCT_Based_On_FFT(float []pReal, float []pImag,  float []pInReal,
                      float []pInImag)
{
    int n, k;


   for (n = 0; n < dct_table_size_fft; n++) {
      pLocalReal2[n] = (double) pInReal[n];
      pLocalImag2[n] = (double) pInImag[n];
      pLocalReal2[dct_table_size_fft + n] =
          (double) pInReal[dct_table_size_fft - 1 - n];
      pLocalImag2[dct_table_size_fft + n] =
          (double) pInImag[dct_table_size_fft - 1 - n];
   }

fft.transform(pLocalReal2, TransformType.FORWARD);
fft.transform(pLocalImag2, TransformType.FORWARD);   


   for (k = 0; k < dct_table_size_fft; k++) {
      pReal[k] = (float)
          (pLocalReal2[k] * pWeightReal2[k] - pLocalImag2[k] * pWeightImag2[k]);
      pImag[k] = (float)
          (pLocalReal2[k] * pWeightImag2[k] + pLocalImag2[k] * pWeightReal2[k]);
   }
for (k = 0; k < dct_table_size_fft; k++) {
      pReal[k] = (float)
          (pLocalReal2[k] * pWeightReal2[k] - pLocalImag2[k] * pWeightImag2[k]);
      pImag[k] = (float)
          (pLocalReal2[k] * pWeightImag2[k] + pLocalImag2[k] * pWeightReal2[k]);
   }

}
   
  public void DCT_Based_On_DFT(float []pReal, float []pImag, float []pInReal,
                     float []pInImag)
{
    int n, k;

   for (n = 0; n < dct_table_size; n++) {
      pLocalReal[n] = pInReal[n];
      pLocalImag[n] = pInImag[n];
      pLocalReal[dct_table_size + n] = pInReal[dct_table_size - 1 - n];
      pLocalImag[dct_table_size + n] = pInImag[dct_table_size - 1 - n];
   }

   DFT(pLocalReal, pLocalImag, dct_table_size * 2);


   for (k = 0; k < dct_table_size; k++) {
      pReal[k] =
          pLocalReal[k] * pWeightReal[k] - pLocalImag[k] * pWeightImag[k];
      pImag[k] =
          pLocalReal[k] * pWeightImag[k] + pLocalImag[k] * pWeightReal[k];
   }
}
  
  public void preEmphasise(double []x, double []y,  double alpha,  int leng)
{
   int k;
   y[0] = x[0] * (1.0 - alpha);
   for (k = 1; k < leng; k++)
      y[k] = x[k] - x[k - 1] * alpha;
}
  
 public void spec(double []x, double []sp,  int leng)
{
   int k, no;
   double []y; 
   double[]mag;

   no = leng / 2;

   y = new double[leng + no]; 
           
   mag =new double[y.length + leng];

   //fft.(x, y, leng);
   for (k = 1; k < no; k++) {
      mag[k] = x[k] * x[k] + y[k] * y[k];
      sp[k] = Math.sqrt(mag[k]);
   }
   y=null;
}
 
 public void fbank(double []x, double []fb,  double eps, double fs,
           int leng, int n)
{
   int i, k, l, fnum, no, startNum, chanNum = 0;
   int []noMel;
   double []w;
   double []countMel;
   double maxMel, startFreq, endFreq, kMel;

   no = leng / 2;
   noMel = new int [Integer.MAX_VALUE];
   countMel = new double[n + 1 + no];
   w = new  double[countMel.length + n + 1];
   maxMel = Freq2Mel(fs / 2.0);

   for (k = 0; k <= n; k++)
      countMel[k] = (double) (k + 1) / (double) (n + 1) * maxMel;
   for (k = 1; k < no; k++) {
      kMel = Sample2Mel(k - 1, no, fs);
      while (countMel[chanNum] < kMel && chanNum <= n)
         chanNum++;
      noMel[k] = chanNum;
   }

   for (k = 1; k < no; k++) {
      chanNum = noMel[k];
      kMel = Sample2Mel(k - 1, no, fs);
      w[k] = (countMel[chanNum] - kMel) / (countMel[0]);
   }

   for (k = 1; k < no; k++) {
      fnum = noMel[k];
      if (fnum > 0)
         fb[fnum] += x[k] * w[k];
      if (fnum <= n)
         fb[fnum + 1] += (1 - w[k]) * x[k];
   }

   noMel=null;
   countMel=null;

   for (k = 1; k <= n; k++) {
      if (fb[k] < eps)
         fb[k] = eps;
      fb[k] = Math.log(fb[k]);
   }
}
 
 public void dct(double []in, double []d,  int size, int m,
          boolean dftmode)
{
   char []s, c;
   

   float []pReal; 
   float []pImag;
   int k, n, i, j, iter;

   double []x; 
   double []y;
   int size2;
   float []x2; 
   float []y2;

   x = new double[size2 = size + size];
   y = new double [x.length + size];
   pReal = new float [size2];
   pImag = new float [pReal.length + size];
   x2 = new float [size2];
   y2 = new float [ x2.length + size];

   for (k = 0; k < size; k++) {
      x[k] = in[k + 1];
      x2[k] = (float) x[k];
      y2[k] = (float) y[k];
   }

   iter = 0;
   i = size;
   while ((i /= 2) != 0) {
      iter++;
   }
   j = 1;
   for (i = 1; i <= iter; i++) {
      j *= 2;
   }
   if (size != j || dftmode) {
     DCT_Creat_Table_FFT(size);
      DCT_Based_On_DFT(pReal, pImag,  x2,  y2);
   } else {
      DCT_Creat_Table_FFT(size);
      DCT_Based_On_FFT(pReal, pImag, x2,  y2);
   }

   for (k = 0; k < m; k++) {
      d[k] = (double) pReal[k];
   }
}
  
  void CepLifter(double []x, double []y,  int m,  int leng)
{
   int k;
   double theta;
   for (k = 0; k < m; k++) {
      theta = Math.PI * (double) k / (double) leng;
      y[k] = (1.0 + (double) leng / 2.0 * Math.sin(theta)) * x[k];
   }
}
  
  void mfcc(double []in, double []mc, double sampleFreq,double alpha,
           double eps,  int wlng,  int flng, int m,
           int n,  int ceplift, boolean dftmode,
          boolean usehamming)
{
    double []x = null; 
double []px; 
double []wx; 
double []sp; 
double []fb; 
double []dc;
   double energy = 0.0, c0 = 0.0;
   int k = wlng;
   int size;

   if (x == null) {
      x = new double[wlng + wlng + flng + flng + n + 1 + m + 1];
      px =new double[ x.length + wlng];
      wx = new double[px.length + wlng] ;
      sp = new double [wx.length + flng];
      fb = new double [sp.length + flng];
      dc = new double[fb.length + n + 1] ;
   } else {
      x= null;
      x = new double[wlng + wlng + flng + flng + n + 1 + m + 1];
      px =new double[ x.length + wlng];
      wx = new double[px.length + wlng] ;
      sp = new double [wx.length + flng];
      fb = new double [sp.length + flng];
      dc = new double[fb.length + n + 1] ;
      size = wlng;
   }
x= Arrays.copyOf(in, k);
   
   /* calculate energy */
   energy = CalcEnergy(x, eps, wlng);
   preEmphasise(x, px, alpha, wlng);
   /* apply hamming window */
   if (usehamming)
      Hamming(px, wlng);
   for (k = 0; k < wlng; k++)
      wx[k] = px[k];
   spec(wx, sp, flng);
   fbank(sp, fb, eps, sampleFreq, flng, n);
   /* calculate 0'th coefficient */
   for (k = 1; k <= n; k++)
      c0 += fb[k];
   c0 *= Math.sqrt(2.0 / (double) n);
   dct(fb, dc, n, m, dftmode);
   /* liftering */
   if (ceplift > 0)
      CepLifter(dc, mc, m, ceplift);
   else
       mc= Arrays.copyOf(dc, dc.length);
      

   for (k = 0; k < m - 1; k++)
      mc[k] = mc[k + 1];
   mc[m - 1] = c0;
   mc[m] = energy;

}

}
