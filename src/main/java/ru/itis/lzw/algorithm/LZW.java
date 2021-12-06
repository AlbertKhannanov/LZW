package ru.itis.lzw.algorithm;

import ru.itis.lzw.model.DictionaryPart;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class LZW {

    private LinkedHashMap<String, DictionaryPart> dictionary = new LinkedHashMap<>();

    public LinkedHashMap<String, DictionaryPart> getDictionary() {
        return dictionary;
    }

    public void setDictionary(LinkedHashMap<String, DictionaryPart> dictionary) {
        this.dictionary = dictionary;
    }

    public String algorithm(String source) {
        StringBuilder result = new StringBuilder();

        StringBuilder tempStr = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            tempStr.append(source.charAt(i));
            if (dictionary.containsKey(tempStr.toString())) {
                continue;
            } else {
                String W = tempStr.substring(0, tempStr.length() - 1);
                String K = tempStr.substring(tempStr.length() - 1);
                result.append(addZeroBits(dictionary.get(W).getBinaryCode(), dictionary.size()));
                dictionary.put(
                        tempStr.toString(),
                        new DictionaryPart(
                                dictionary.size(),
                                Integer.toBinaryString(dictionary.size())
                        )
                );

                tempStr.setLength(0);
                tempStr.append(K);
            }

            if (i + 1 == source.length()) {
                result.append(addZeroBits(dictionary.get(tempStr.toString()).getBinaryCode(), dictionary.size()));
            }
        }

        return result.toString();
    }

    public String addZeroBits(String current, Integer dictionarySize) {
        StringBuilder stringBuilder = new StringBuilder(current);

        while (stringBuilder.length() < Math.log(dictionarySize) / Math.log(2)) {
            stringBuilder.insert(0, "0");
        }

        return stringBuilder.toString();
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

        public LinkedHashMap<String, DictionaryPart> initDictionary(String source) {
            LinkedHashMap<String, DictionaryPart> result = new LinkedHashMap<>();
            HashSet<String> characters = new HashSet<>();

            int temp = 0;
            for (int i = 0; i < source.length(); i++) {
                if (!characters.contains(String.valueOf(source.charAt(i)))) {
                    result.put(String.valueOf(source.charAt(i)), new DictionaryPart(temp, Integer.toBinaryString(temp++)));
                }
                characters.add(String.valueOf(source.charAt(i)));
            }

            for (String character : result.keySet()) {
                result.get(character).setBinaryCode(addZeroBits(result.get(character).getBinaryCode(), result.size()));
            }

            return result;
        }

        public String addZeroBits(String current, Integer dictionarySize) {
            StringBuilder stringBuilder = new StringBuilder(current);

            while (stringBuilder.length() < Math.log(dictionarySize) / Math.log(2)) {
                stringBuilder.insert(0, "0");
            }

            return stringBuilder.toString();
        }
    }

    public static class Decode {

    }
}
