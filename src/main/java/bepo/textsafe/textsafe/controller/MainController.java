package bepo.textsafe.textsafe.controller;

import bepo.textsafe.textsafe.util.Serialization;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainController {
    private ObservableList<String> data = FXCollections.observableArrayList();

    public ObservableList<String> getAllData() {
        try {
            data.setAll(Serialization.deserializeData());
        } catch(Exception e) {
            System.err.println("Data couldn't be loaded");
        }

        return data;
    }

    public boolean saveAllData() {
        try {
            Serialization.serializeData(data);
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }

        return true;
    }

    public String getData(int index) {
        try {
            return data.get(index);
        } catch(Exception e) {
            System.err.println("No data found");
            return "";
        }
    }

    public boolean saveData(String newData, int index) {
        data.set(index, newData);

        return saveAllData();
    }

    public void changeData(String newData, int index) {
        data.set(index, newData);
    }

    public void deleteData(int index) {
        data.remove(index);
        saveAllData();
    }

    public void addData() {
        data.add("");
    }
}