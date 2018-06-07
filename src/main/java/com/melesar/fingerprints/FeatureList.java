package com.melesar.fingerprints;

import java.io.Serializable;
import java.util.ArrayList;

public class FeatureList implements Serializable
{
    private ArrayList<Feature> features;

    public FeatureList(ArrayList<Feature> features)
    {
        this.features = features;
    }

    public ArrayList<Feature> getFeatures()
    {
        return features;
    }
}
