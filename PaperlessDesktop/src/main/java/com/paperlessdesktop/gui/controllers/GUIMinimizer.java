package com.paperlessdesktop.gui.controllers;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import com.paperlessdesktop.gui.App;
import javafx.application.Platform;



public final class GUIMinimizer implements ActionListener{
    public static TrayIcon trayIcon;

    public static void init(){
        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            return;
        }

        final PopupMenu popup = new PopupMenu();

        URL url = App.class.getResource("ocr.png");
        Image image = Toolkit.getDefaultToolkit().getImage(url);

        trayIcon = new TrayIcon(image);
        trayIcon.setImageAutoSize(true);

        final SystemTray tray = SystemTray.getSystemTray();


        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem("About");
        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
        Menu displayMenu = new Menu("Display");
        MenuItem errorItem = new MenuItem("Error");
        MenuItem warningItem = new MenuItem("Warning");
        MenuItem infoItem = new MenuItem("Info");
        MenuItem noneItem = new MenuItem("None");
        MenuItem exitItem = new MenuItem("Exit");


        //Add components to pop-up menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(cb1);
        popup.add(cb2);
        popup.addSeparator();
        popup.add(displayMenu);
        displayMenu.add(errorItem);
        displayMenu.add(warningItem);
        displayMenu.add(infoItem);
        displayMenu.add(noneItem);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip("");
        trayIcon.displayMessage("Tray test", "Demmmmmmmmmmo", TrayIcon.MessageType.INFO);

        trayIcon.addActionListener(e -> {
            Platform.runLater(() -> App.getPrimaryStage().show());
        });

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage("OYEEEEE", "WHAT", TrayIcon.MessageType.INFO);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    public static boolean OsSupportsSystemTray(){
        return SystemTray.isSupported();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
