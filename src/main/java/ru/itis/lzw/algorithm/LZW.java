package ru.itis.lzw.algorithm;

import javafx.util.Pair;
import ru.itis.lzw.model.DictionaryPart;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class LZW {

    private LinkedHashMap<String, DictionaryPart> dictionary = new LinkedHashMap<>();

    public LinkedHashMap<String, DictionaryPart> getDictionary() {
        return dictionary;
    }

    private static final int from = 1;

    public void setDictionary(LinkedHashMap<String, DictionaryPart> dictionary) {
        this.dictionary = dictionary;
    }

    public String algorithm(String source) {
        StringBuilder result = new StringBuilder();

        StringBuilder tempStr = new StringBuilder();
        for (int i = 0; i < source.length();) {
            int utf8Code = source.codePointAt(i);

            tempStr.append(source, i, i + Character.charCount(utf8Code));
            if (!dictionary.containsKey(tempStr.toString())) {
                int lastSymbolIndex = 0;
                for (int j = 0; j < tempStr.length();) {
                    int utf8CodeTemp = tempStr.codePointAt(j);
                    if (j + Character.charCount(utf8CodeTemp) == tempStr.length())
                        lastSymbolIndex = Character.charCount(utf8CodeTemp);
                    j += Character.charCount(utf8CodeTemp);
                }

                String W = tempStr.substring(0, tempStr.length() - lastSymbolIndex);
                String K = tempStr.substring(tempStr.length() - lastSymbolIndex);

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

            if (i + Character.charCount(source.codePointAt(i)) == source.length()) {
                result.append(addZeroBits(dictionary.get(tempStr.toString()).getBinaryCode(), dictionary.size() + from));
            }

            i += Character.charCount(utf8Code);
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

    public void writeToFile(String path, String restoreInitText) {
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(restoreInitText);
            writer.flush();
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
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
            for (int i = 0; i < source.length();) {
                int utf8Code = source.codePointAt(i);
                String cur = source.substring(i, i + Character.charCount(utf8Code));
                if (!characters.contains(cur)) {
                    result.put(cur, new DictionaryPart(temp, Integer.toBinaryString(temp++)));
                }
                characters.add(cur);
                i += Character.charCount(utf8Code);
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

        public LinkedHashMap<String, String> dictionary = new LinkedHashMap<>();

        public LinkedHashMap<String, String> getDictionary() {
            return dictionary;
        }

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

        public Pair<Pair<String, Integer>, String> getDataForDecoder(String rawData) {
            String alphabet;
            Integer index;
            String encoded;

            String[] splitSourceDataWithEncoded = rawData.split("(?<=\\d)_S_P_L-I-T_D_A-T_-A");
            String[] splitIndexAndAlphabet = splitSourceDataWithEncoded[0].split("_S_P_L-I-T_A_L_P_H_A-B-_E_T(?=\\d)");

            alphabet = splitIndexAndAlphabet[0];
            index = Integer.parseInt(splitIndexAndAlphabet[1]);
            encoded = splitSourceDataWithEncoded[1];

            return new Pair<>(new Pair<>(alphabet, index), encoded);
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

                System.out.println(W);
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
                    System.out.println("Новая запись в словаре: " + dictionary.get(Integer.toBinaryString(dictionary.size() - 1)) + "\t" + Integer.toBinaryString(dictionary.size()));
                    System.out.println("------------------");

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

            for (int i = 0; i < source.length();) {
                int utf8Code = source.codePointAt(i);

                tempSet.add(source.substring(i, i + Character.charCount(utf8Code)));

                i += Character.charCount(utf8Code);
            }

            int temp = from;
            for (int i = 0; i < source.length();) {
                int utf8Code = source.codePointAt(i);
                String cur = source.substring(i, i + Character.charCount(utf8Code));
                if (!characters.contains(cur)) {
                    dictionary.put(
                            addZeroBits(Integer.toBinaryString(temp++), tempSet.size()),
                            cur
                    );
                }
                characters.add(cur);
                i += Character.charCount(utf8Code);
            }
        }

        public String addZeroBits(String current, Integer dictionarySize) {
            StringBuilder stringBuilder = new StringBuilder(current);

            while (stringBuilder.length() < Math.log(dictionarySize) / Math.log(2)) {
                stringBuilder.insert(0, "0");
            }

            return stringBuilder.toString();
        }

        public void writeToFile(String path, String restoreInitText) {
            try (FileWriter writer = new FileWriter(path, false)) {
                writer.write(restoreInitText);
                writer.flush();
            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
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
                    result.append(dictionary.get(code));
                }
            }
            if (!isInDict) {
                result.append(dictionary.get(tempCode), 0, Character.charCount(dictionary.get(tempCode).codePointAt(0)));
            }

            return result.toString();
        }
    }
}
