package ru.itis.lzw.algorithm;

import javafx.util.Pair;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class LZW {

    private LinkedHashMap<String, String> dictionary = new LinkedHashMap<>();

    public LinkedHashMap<String, String> getDictionary() {
        return dictionary;
    }

    private static final int from = 0;

    public void initDictionary(String alphabet) {
        for (int i = 0; i < alphabet.length();) {
            int utf8Code = alphabet.codePointAt(i);

            dictionary.put(
                    alphabet.substring(i, i + Character.charCount(utf8Code)),
                    addZeroBits(Integer.toBinaryString(dictionary.size()), alphabet.length())
            );

            i += Character.charCount(utf8Code);
        }
    }


    public String algorithm(String source) {
        StringBuilder result = new StringBuilder();

        StringBuilder currentString = new StringBuilder();
        for (int i = 0; i < source.length();) {
            int utf8Code = source.codePointAt(i);

            currentString.append(source, i, i + Character.charCount(utf8Code));
            if (!dictionary.containsKey(currentString.toString())) {
                int lastSymbolIndex = getLastSymbolIndex(currentString);

                String W = currentString.substring(0, currentString.length() - lastSymbolIndex);
                String K = currentString.substring(currentString.length() - lastSymbolIndex);

                result.append(addZeroBits(dictionary.get(W), dictionary.size() + from));
                dictionary.put(
                        currentString.toString(),
                        Integer.toBinaryString(dictionary.size() + from)
                );

                currentString.setLength(0);
                currentString.append(K);
            }

            if (i + Character.charCount(source.codePointAt(i)) == source.length()) {
                result.append(addZeroBits(dictionary.get(currentString.toString()), dictionary.size() + from));
                break;
            }

            i += Character.charCount(utf8Code);
        }

        return result.toString();
    }

    public String addZeroBits(String current, Integer size) {
        StringBuilder stringBuilder = new StringBuilder(current);

        while (stringBuilder.length() < Math.log(size) / Math.log(2)) {
            stringBuilder.insert(0, "0");
        }

        return stringBuilder.toString();
    }

    public void writeToFile(String path, String data) {
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(data);
            writer.flush();
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
    }

    public String getAlphabet(String source) {
        StringBuilder result = new StringBuilder();
        HashSet<String> symbols = new HashSet<>();

        for (int i = 0; i < source.length();) {
            int utf8Code = source.codePointAt(i);

            String curSymbol = source.substring(i, i + Character.charCount(utf8Code));
            if (!symbols.contains(curSymbol)) {
                result.append(curSymbol);
            }
            symbols.add(curSymbol);

            i += Character.charCount(utf8Code);
        }

        return result.toString();
    }

    private int getLastSymbolIndex(StringBuilder tempStr) {
        int lastSymbolIndex = 0;
        for (int j = 0; j < tempStr.length(); ) {
            int utf8CodeTemp = tempStr.codePointAt(j);
            if (j + Character.charCount(utf8CodeTemp) == tempStr.length())
                lastSymbolIndex = Character.charCount(utf8CodeTemp);
            j += Character.charCount(utf8CodeTemp);
        }
        return lastSymbolIndex;
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
    }

    public static class Decode {

        public LinkedHashMap<String, String> dictionary = new LinkedHashMap<>();

        public LinkedHashMap<String, String> getDictionary() {
            return dictionary;
        }

        public Pair<Pair<String, Integer>, String> readFile(String path) {
            StringBuilder result = new StringBuilder();
            try (FileReader reader = new FileReader(path)) {
                int c;
                while ((c = reader.read()) != -1) {
                    result.append((char) c);
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            return getDataForDecoder(result.toString());
        }

        public String decode(String encoded) {
            if (encoded.length() == 1) {
                return dictionary.get("0");
            }
            StringBuilder result = new StringBuilder();

            StringBuilder currentString = new StringBuilder(encoded.substring(0, howManyBitsNeed(dictionary.size())));

            for (int i = howManyBitsNeed(dictionary.size()); i < encoded.length(); i += howManyBitsNeed(dictionary.size())) {
                String WY = currentString + encoded.substring(i, i + howManyBitsNeed(dictionary.size() + 1));
                String W = currentString.toString();
                String Y = encoded.substring(i, i + howManyBitsNeed(dictionary.size() + 1));

                if (!dictionary.containsKey(WY)) {
                    for (String code : dictionary.keySet()) {
                        if (addZeroBits(code, dictionary.size()).equals(W)) {
                            result.append(dictionary.get(code));
                            break;
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
                    break;
                }
            }

            return result.toString();
        }

        public void initDictionary(String alphabet) {
            for (int i = 0; i < alphabet.length();) {
                int utf8Code = alphabet.codePointAt(i);

                dictionary.put(
                        addZeroBits(Integer.toBinaryString(dictionary.size()), alphabet.length()),
                        alphabet.substring(i, i + Character.charCount(utf8Code))
                );

                i += Character.charCount(utf8Code);
            }
        }

        public String addZeroBits(String current, Integer size) {
            StringBuilder stringBuilder = new StringBuilder(current);

            while (stringBuilder.length() < Math.log(size) / Math.log(2)) {
                stringBuilder.insert(0, "0");
            }

            return stringBuilder.toString();
        }

        public void writeToFile(String path, String data) {
            try (FileWriter writer = new FileWriter(path, false)) {
                writer.write(data);
                writer.flush();
            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }

        private Pair<Pair<String, Integer>, String> getDataForDecoder(String rawData) {
            String alphabet;
            Integer index;
            String encoded;

            String[] splitSourceDataWithEncoded = rawData.split("(?<=\\d)_S_P_L-I-T_D_A-T_-A");
            String[] splitIndexAndAlphabet = splitSourceDataWithEncoded[0].split("_S_P_L-I-T_A_L_P_H_A-B-_E_T(?=\\d)");

            alphabet = splitIndexAndAlphabet[0];
            index = Integer.parseInt(splitIndexAndAlphabet[1]);
            try {
                encoded = splitSourceDataWithEncoded[1];
            } catch (Exception e) {
                encoded = "";
            }

            return new Pair<>(new Pair<>(alphabet, index), encoded);
        }

        private Integer howManyBitsNeed(Integer size) {
            int counter = 1;
            while (counter < Math.log(size) / Math.log(2)) {
                counter++;
            }
            return counter;
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
                    result.append(dictionary.get(code), 0, Character.charCount(dictionary.get(code).codePointAt(0)));
                }
            }
            if (!isInDict) {
                result.append(dictionary.get(tempCode), 0, Character.charCount(dictionary.get(tempCode).codePointAt(0)));
            }

            return result.toString();
        }
    }
}
