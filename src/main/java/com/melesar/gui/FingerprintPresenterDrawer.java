package com.melesar.gui;

import javax.swing.*;
import java.awt.*;

public class FingerprintPresenterDrawer implements ListCellRenderer<FingerprintPresenter>
{
    @Override
    public Component getListCellRendererComponent(JList<? extends FingerprintPresenter> list, FingerprintPresenter value, int index, boolean isSelected, boolean cellHasFocus)
    {
        return value.getContent();
    }
}
