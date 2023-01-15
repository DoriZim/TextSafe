package bepo.textsafe.textsafe.controller;

import bepo.textsafe.textsafe.util.Serialization;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainController {
    private ObservableList<String> data = FXCollections.observableArrayList();
    private ObservableList<String> name = FXCollections.observableArrayList();

    public void loadContent() {
        try {
            name.setAll(Serialization.deserializeName());
            data.setAll(Serialization.deserializeData());
        } catch(Exception e) {
            System.err.println("Name or data couldn't be loaded");
        }
    }

    public ObservableList<String> getAllNames() {
        return name;
    }

    public boolean saveAllData() {
        try {
            Serialization.serializeData(data);
            Serialization.serializeName(name);
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
        if(!data.isEmpty()) {
            data.set(index, newData);
        }
        return saveAllData();
    }

    public void changeData(String newData, int index) {
        data.set(index, newData);
    }

    //todo - implement feature
    public void changeName(String newName, int index) {
        name.set(index, newName);
    }

    public void deleteData(int index) {
        data.remove(index);
        name.remove(index);

        saveAllData();
    }

    public void addData(String newName) {
        name.add(newName);
        data.add("");
    }
}