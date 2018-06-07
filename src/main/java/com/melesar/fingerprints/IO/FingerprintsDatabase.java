package com.melesar.fingerprints.IO;

import com.melesar.fingerprints.FeatureList;

import java.io.*;
import java.util.ArrayList;

public class FingerprintsDatabase
{
    private File file;

    private final String path = "database/fingerprints.dat";

    public void add (FeatureList newFeaturelist) throws IOException, ClassNotFoundException
    {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            ObjectOutputStream objectStream = new ObjectOutputStream(fileOutputStream);

            ArrayList<FeatureList> contents = read();
            contents.add(newFeaturelist);

            objectStream.writeObject(contents);
        }
    }

    public ArrayList<FeatureList> read () throws IOException, ClassNotFoundException
    {
        ObjectInputStream objectInput;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            objectInput = new ObjectInputStream(fileInputStream);
            return (ArrayList<FeatureList>) objectInput.readObject();
        } catch (EOFException ex) {
            return new ArrayList<>();
        }
    }

    public FingerprintsDatabase() throws IOException
    {
        file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

}
