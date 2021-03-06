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

    private static final int from = 0;

    public void setDictionary(LinkedHashMap<String, DictionaryPart> dictionary) {
        this.dictionary = dictionary;
    }

    public String algorithm(String source) {
        StringBuilder result = new StringBuilder();

        StringBuilder tempStr = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            tempStr.append(source.charAt(i));
            if (!dictionary.containsKey(tempStr.toString())) {
                String W = tempStr.substring(0, tempStr.length() - 1);
                String K = tempStr.substring(tempStr.length() - 1);
                result.append(addZeroBits(dictionary.get(W).getBinaryCode(), dictionary.size() + from));
                dictionary.put(
                        tempStr.toString(),
                        new DictionaryPart(
                                dictionary.size() + from,
                                Integer.toBinaryString(dictionary.size() + from)
                        )
                );

                tempStr.setLength(0);
                tempStr.append(K);
            }

            if (i + 1 == source.length()) {
                result.append(addZeroBits(dictionary.get(tempStr.toString()).getBinaryCode(), dictionary.size() + from));
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

            int temp = from;
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

        private LinkedHashMap<String, String> dictionary = new LinkedHashMap<>();
        private int initialSize = 0;

        public LinkedHashMap<String, String> getDictionary() {
            return dictionary;
        }

        public String decode(String encoded) {
            if (encoded.length() == 1) {
                return dictionary.get("0");
            }
            StringBuilder result = new StringBuilder();

            StringBuilder currentString = new StringBuilder(encoded.substring(0, howManyBitsNeed(dictionary.size())));

            for (int i = howManyBitsNeed(dictionary.size()); i < encoded.length(); i += howManyBitsNeed(dictionary.size())) {
                String WY = currentString + encoded.substring(i, i + howManyBitsNeed(dictionary.size()));
                String W = currentString.toString();
                String Y = encoded.substring(i, i + howManyBitsNeed(dictionary.size() + 1));

                if (!dictionary.containsKey(WY)) {
                    for (String code : dictionary.keySet()) {
                        if (addZeroBits(code, dictionary.size()).equals(W)) {
                            result.append(dictionary.get(code));
                        }
                    }

                    dictionary.put(
                            Integer.toBinaryString(dictionary.size()),
                            defineSymbols(W, Y)
                    );

                    currentString.setLength(0);
                    currentString.append(Y);
                } else {
                    currentString.setLength(0);
                    currentString.append(WY);
                }

                if (i + howManyBitsNeed(dictionary.size()) >= encoded.length()) {
                    for (String code : dictionary.keySet()) {
                        if (addZeroBits(code, dictionary.size()).equals(Y)) {
                            result.append(dictionary.get(code));
                        }
                    }
                }
            }

            return result.toString();
        }

        public void initDictionary(String source) {
            HashSet<String> characters = new HashSet<>();
            HashSet<String> tempSet = new HashSet<>();

            for (int i = 0; i < source.length(); i++) {
                tempSet.add(String.valueOf(source.charAt(i)));
            }

            int temp = from;
            for (int i = 0; i < source.length(); i++) {
                if (!characters.contains(String.valueOf(source.charAt(i)))) {
                    dictionary.put(addZeroBits(Integer.toBinaryString(temp++), tempSet.size()), String.valueOf(source.charAt(i)));
                }
                characters.add(String.valueOf(source.charAt(i)));
            }
            initialSize = dictionary.size();
        }

        public String addZeroBits(String current, Integer dictionarySize) {
            StringBuilder stringBuilder = new StringBuilder(current);

            while (stringBuilder.length() < Math.log(dictionarySize) / Math.log(2)) {
                stringBuilder.insert(0, "0");
            }

            return stringBuilder.toString();
        }

        private Integer howManyBitsNeed(Integer size) {
            int res = 1;
            while (res < Math.log(size) / Math.log(2)) {
                res++;
            }
            return res;
        }

        private String defineSymbols(String first, String second) {
            StringBuilder result = new StringBuilder();

            boolean isInDict = false;
            String tempCode = "";
            for (String code : dictionary.keySet()) {
                if (addZeroBits(code, dictionary.size()).equals(first)) {
                    result.insert(0, dictionary.get(code));
                    tempCode = code;
                }
                if (addZeroBits(code, dictionary.size() + 1).equals(second)) {
                    isInDict = true;
                    result.append(dictionary.get(code).charAt(0));
                }
            }
            if (!isInDict)
                result.append(dictionary.get(tempCode).charAt(0));

            return result.toString();
        }
    }
}
