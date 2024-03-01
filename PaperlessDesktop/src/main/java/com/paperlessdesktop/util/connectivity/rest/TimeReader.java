package com.paperlessdesktop.util.connectivity.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TimeReader {
    private final String address = "http://worldtimeapi.org/api/timezone/Europe/Berlin";

    public static final Logger logger = Logger.getLogger(TimeReader.class.getName());

    private JSONObject jsonObject;

    public LocalDate getDateOfToday(){
        String dateString = (String) jsonObject.get("datetime");

        StringBuilder sb = new StringBuilder();
        for(char c : dateString.toCharArray()){
            if(c == 'T' || c == 't'){
                break;
            }

            sb.append(c);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return LocalDate.parse(sb.toString(), formatter);
    }

    public boolean connectToAPI(){
        try {
            URL url = new URL(address);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            StringBuilder sb = new StringBuilder();
            Scanner scan = new Scanner(url.openStream());
            while (scan.hasNextLine()) {
                sb.append(scan.nextLine());
            }

            if(connection.getResponseCode() != 200){
                return false;
            }
            scan.close();

            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(sb.toString());


        } catch (ParseException | IOException e) {
            logger.throwing(TimeReader.class.toString(), "connectionToAPI", e);
            e.printStackTrace();
            return false;
        }

        return true;
    }    
}
