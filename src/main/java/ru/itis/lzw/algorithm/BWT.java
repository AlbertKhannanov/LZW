package ru.itis.lzw.algorithm;

import javafx.util.Pair;

import java.util.*;

public class BWT {

    public Pair<String, Integer> modifySequence(String src) {
        // выводим все символы под подходящем
        ArrayList<String> cycleOffset = getCycleOffsets(src);

        StringBuilder resultTemp = new StringBuilder();
        int index = 0;
        for (String cyc : cycleOffset) {
            int lastSymbolIndex = getLastSymbolIndex(cyc);

            resultTemp.append(cyc.substring(cyc.length() - lastSymbolIndex));
            if (cyc.equals(src)) {
                index = cycleOffset.indexOf(cyc);
            }
        }

        return new Pair<>(resultTemp.toString(), index);
    }

    public String restoreInitString(String lzwDecoded, Integer index) {
        ArrayList<String> bwtDecode = new ArrayList<>();

        for (int i = 0; i < lzwDecoded.length();) {
            int utf8Code = lzwDecoded.codePointAt(i);
            bwtDecode.add(lzwDecoded.substring(i, i + Character.charCount(utf8Code)));
            i += Character.charCount(utf8Code);
        }
        Collections.sort(bwtDecode);

        int lastSymbolIndex = getLastSymbolIndex(lzwDecoded);

        for (int i = 0; i < lzwDecoded.length() - lastSymbolIndex;) {
            int utf8Codetemp = lzwDecoded.codePointAt(i);
            int temp = 0;
            for (int j = 0; j < lzwDecoded.length();) {
                int utf8Code = lzwDecoded.codePointAt(j);
                bwtDecode.set(temp, lzwDecoded.substring(j, j + Character.charCount(utf8Code)) + bwtDecode.get(temp++));
                j += Character.charCount(utf8Code);
            }
            Collections.sort(bwtDecode);
            i += Character.charCount(utf8Codetemp);
        }

        return bwtDecode.get(index);
    }

    private ArrayList<String> getCycleOffsets(String str) {
        // находим циклические сдвиги
        ArrayList<String> cycleOffsets = new ArrayList<>();

        StringBuilder temp = new StringBuilder(str);

        for(int i = 0; i < str.length();) {
            int utf8Code = str.codePointAt(i);

            temp.delete(0, i);
            temp.append(str, 0, i);
            cycleOffsets.add(temp.toString());

            temp.setLength(0);
            temp.append(str);
            i += Character.charCount(utf8Code);
        }
        Collections.sort(cycleOffsets);

        return cycleOffsets;
    }

    private int getLastSymbolIndex(String str) {
        int lastSymbolIndex = 0;
        for (int i = 0; i < str.length(); ) {
            int utf8Code = str.codePointAt(i);
            if (i + Character.charCount(utf8Code) == str.length())
                lastSymbolIndex = Character.charCount(utf8Code);
            i += Character.charCount(utf8Code);
        }
        return lastSymbolIndex;
    }
}
