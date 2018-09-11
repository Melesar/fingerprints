package Biometric.AudioModifiers;

public class RemoveSilence {
    private double[] inputSignal; //input
    private double[] outputSignal;//output
    private int samplingRate;
    private int firstSamples;
    private int samplePerFrame;

    public RemoveSilence(double[] originalSignal, int samplingRate) {
        this.inputSignal = originalSignal;
        this.samplingRate = samplingRate;
        samplePerFrame = this.samplingRate / 1000;
        firstSamples = samplePerFrame * 200;
    }


    public double[] remove() {
        // sprawdzanie czy probka zawiera cisze czy nie
        double[] voiced = new double[inputSignal.length];
        double sum = 0;
        double sd = 0.0;
        double m = 0.0;
        // 1. Obliczanie średniej
        for (int i = 0; i < firstSamples; i++) {
            sum += inputSignal[i];
        }
        m = sum / firstSamples;// srednia
        sum = 0;

        // 2. Obliczanie odchylenia standardowego
        for (int i = 0; i < firstSamples; i++) {
            sum += Math.pow((inputSignal[i] - m), 2);
        }
        sd = Math.sqrt(sum / firstSamples);
        // 3.Jednowymiarowa funkcja odległości Mahalanobisa
        // np. |x-u|/s wieksze od ####3 lub nie,
        for (int i = 0; i < inputSignal.length; i++) {
            if ((Math.abs(inputSignal[i] - m) / sd) > 0.3) { //0.3 =Próg
                voiced[i] = 1;
            } else {
                voiced[i] = 0;
            }
        }
        // 4. Obliczanie sygnałów dźwięcznych i bezdźwięcznych
        // oznaczanie każdej klatki, która ma być dźwięczną lub bezdźwięczną
        int frameCount = 0;
        int usefulFramesCount = 1;
        int count_voiced = 0;
        int count_unvoiced = 0;
        System.out.printf("\n"+"Signal length before "+inputSignal.length);

        int voicedFrame[] = new int[inputSignal.length / samplePerFrame];
        // the following calculation truncates the remainder
        int loopCount = inputSignal.length - (inputSignal.length % samplePerFrame);
        for (int i = 0; i < loopCount; i += samplePerFrame) {
            count_voiced = 0;
            count_unvoiced = 0;
            for (int j = i; j < i + samplePerFrame; j++) {
                if (voiced[j] == 1) {
                    count_voiced++;
                } else {
                    count_unvoiced++;
                }
            }
            if (count_voiced > count_unvoiced) {
                usefulFramesCount++;
                voicedFrame[frameCount++] = 1;
            } else {
                voicedFrame[frameCount++] = 0;
            }
        }
        // 5. Usuwanie ciszy
        outputSignal = new double[usefulFramesCount * samplePerFrame];
        int k = 0;
        for (int i = 0; i < frameCount; i++) {
            if (voicedFrame[i] == 1) {
                for (int j = i * samplePerFrame; j < i * samplePerFrame + samplePerFrame; j++) {
                    outputSignal[k++] = inputSignal[j];
                }
            }
        }

        System.out.printf("\n"+"Signal length after "+outputSignal.length);
        return outputSignal;
    }
}
