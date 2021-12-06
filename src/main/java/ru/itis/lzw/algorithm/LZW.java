package ru.itis.lzw.algorithm;

import javafx.util.Pair;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class LZW {

    private LinkedHashMap<String, Pair<Integer, Integer>> dictionary = new LinkedHashMap<>();

    public LinkedHashMap<String, Pair<Integer, Integer>> getDictionary() {
        return dictionary;
    }

    public void setDictionary(LinkedHashMap<String, Pair<Integer, Integer>> dictionary) {
        this.dictionary = dictionary;
    }

    public String algorithm(String source) {
        StringBuilder stringBuilder = new StringBuilder();

        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            temp.append(source.charAt(i));
            if (dictionary.containsKey(temp.toString())) {
                continue;
            } else {
                String XY = temp.toString();
                String X = temp.substring(0, temp.length() - 1);
                String Y = temp.substring(temp.length() - 1);
                dictionary.put(
                        XY,
                        new Pair<>(dictionary.size(), dictionary.get(X).getKey())
                );

                temp.setLength(0);
                temp.append(Y);
            }
        }

        return "";
    }

    public static class Prepare {

        public String readFile(String path) {
            StringBuilder result = new StringBuilder();
            try (FileReader reader = new FileReader(path)) {
                int c;
                while ((c = reader.read()) != -1) {
                    result.append((char) c);
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            return result.toString();
        }

        public LinkedHashMap<String, Pair<Integer, Integer>> initDictionary(String source) {
            LinkedHashMap<String, Pair<Integer, Integer>> result = new LinkedHashMap<>();
            HashSet<String> characters = new HashSet<>();

            int temp = 0;
            for (int i = 0; i < source.length(); i++) {
                if (!characters.contains(String.valueOf(source.charAt(i))))
                    result.put(String.valueOf(source.charAt(i)), new Pair<>(temp, temp++));
                characters.add(String.valueOf(source.charAt(i)));
            }

            return result;
        }
    }

    public static class Decode {

    }
}
