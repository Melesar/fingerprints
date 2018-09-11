/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biometric.Main;

import Biometric.Functions.AudiotoByte;
import Biometric.Functions.DCT;
import Biometric.Functions.DTW;
import Biometric.AudioModifiers.Process;
import Biometric.AudioModifiers.RemoveSilence;
import Biometric.Utilities.Bank;
import Biometric.Utilities.FramesList;
import Biometric.Utilities.SpectrumsList;

import java.io.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;


/**
 * @author Patryk
 */
public class Record
{
    public static void main(String[] args) throws LineUnavailableException, InterruptedException, Exception
    {
        int samplingRate = 44100;
       /*Register("Record1.wav");
       System.out.printf("\n" + "Next record ");
       Register("Record2.wav");
       System.out.printf("\n" + "Next record ");
       Register("Record3.wav");*/
        DTW dtw = new DTW();

        System.out.printf("\n" + "Sum DTW 1st and 2nd sample " + dtw.Compare(CaptureSound("Record1.wav"), CaptureSound("Record2.wav")));
        System.out.printf("\n" + "Sum DTW 2nd and 3rd sample " + dtw.Compare(CaptureSound("Record2.wav"), CaptureSound("Record3.wav")));
        System.out.printf("\n" + "Sum DTW 1st and 3rd sample " + dtw.Compare(CaptureSound("Record1.wav"), CaptureSound("Record3.wav")));
        // System.out.printf("\n" + "Minimum " + dtw.GetMin(200,10,500));
    }


    public static double[] CaptureSound(String file)
    {

        AudiotoByte audio = new AudiotoByte();
        SpectrumsList slist = new SpectrumsList();
        Process test = new Process();
        byte[] table = audio.readWAVAudioFileData(file);
        RemoveSilence removeSilence = new RemoveSilence(test.BytetoDoubleArray(table), 44100);

        RemoveSilence remove = new RemoveSilence(test.BytetoDoubleArray(table), 44100);
        double[] tabledouble = removeSilence.remove();
        FramesList flist = new FramesList();
        //audio.BytetoString(table);


        flist.Frames = test.SliceSignal(tabledouble, 512);
        slist.Samples = test.PowerSpectrum(flist.Frames);

        System.out.printf("\n" + "Size of list: " + flist.Frames.size());
        Bank bank = new Bank();
        double[] banks = test.ConvertFFTBin(bank.filters, 44100, slist.Samples);
        double[] logbank = test.LogEnergy(banks);
        DCT dct = new DCT();
        dct.transform(logbank);
//double[] dctrerult=dct.Transform(logbank);
        for (int i = 0; i < logbank.length; i++) {
            //  System.out.printf("\n" + "Power Spectrum: " + logbank[i]);


        }
        return logbank;
    }

    public static void Register(final String file) throws LineUnavailableException, InterruptedException, Exception
    {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.printf("Line is not supported");
        }
        final TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open();
        System.out.printf("\n" + "Starting Recording");
        targetDataLine.start();
        Thread stopper = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AudioInputStream audioStream = new AudioInputStream(targetDataLine);

                    File wavFile = new File(file);
                    try {
                        AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wavFile);
                    } catch (IOException ex) {
                        Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (Exception ex) {
                    Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


        });

        stopper.start();

        Thread.sleep(5000);
        targetDataLine.stop();

        targetDataLine.close();


        System.out.printf("\n" + "Recording ended ");

    }


}
