package ru.itis.lzw.model;

import java.util.HashMap;

public class Dictionary {

    private HashMap<String, Integer> dictionary = new HashMap<>();
    private Integer currentNumber = 256;

    public void addToDictionary(String str) {
        dictionary.put(str, currentNumber++);
    }

    public HashMap<String, Integer> getDictionary() {
        return dictionary;
    }
}
