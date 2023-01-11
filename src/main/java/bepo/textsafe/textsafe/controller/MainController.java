package bepo.textsafe.textsafe.controller;

import bepo.textsafe.textsafe.util.Serialization;

public class MainController {
    private String data;

    public String getData() throws Exception {
        data = Serialization.deserializeData();

        return data;
    }

    public boolean saveData(String data) {
        try {
            Serialization.serializeData(data);
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }

        return true;
    }
}