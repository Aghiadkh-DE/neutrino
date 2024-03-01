package com.paperlessdesktop.gui.controllers;

public class HelpController {
    private static HelpController instance;

    private HelpController(){}

    public static HelpController getInstance(){
        return instance == null ? (instance = new HelpController()) : instance;
    }
}
