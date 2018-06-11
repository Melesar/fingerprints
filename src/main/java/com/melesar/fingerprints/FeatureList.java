package com.melesar.fingerprints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class FeatureList implements Serializable, Iterable<Feature>
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

    @Override
    public Iterator<Feature> iterator()
    {
        return features.iterator();
    }
}
