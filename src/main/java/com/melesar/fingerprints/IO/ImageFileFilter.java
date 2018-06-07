package com.melesar.fingerprints.IO;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFileFilter extends FileFilter
{
    @Override
    public boolean accept(File f)
    {
        if (f.isDirectory()) {
            return true;
        } else {
            String fileName = f.getName().toLowerCase();
            return fileName.endsWith(".jpg") ||
                    fileName.endsWith(".bmp") ||
                    fileName.endsWith(".png");
        }
    }

    @Override
    public String getDescription()
    {
        return "Images (.bmp, .png, .jpg)";
    }
}
