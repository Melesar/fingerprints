package com.melesar.audio;

import Biometric.Functions.DTW;
import Biometric.Main.Record;

public class RecordingManager
{
    private final String REFERENCE_NAME = "reference.wav";
    private final String SAMPLE_NAME = "sample.wav";

    private final double SIMILARITY_THRESHOLD = 20;

    public boolean recordReference()
    {
        try {
            Record.Register(REFERENCE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean compareSample()
    {
        try {
            Record.Register(SAMPLE_NAME);
            double[] referenceData = Record.CaptureSound(REFERENCE_NAME);
            double[] sampleData = Record.CaptureSound(SAMPLE_NAME);

            DTW dtw = new DTW();

            double similarity = dtw.Compare(referenceData, sampleData);
            return similarity <= SIMILARITY_THRESHOLD;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
