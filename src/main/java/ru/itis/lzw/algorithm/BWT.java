package ru.itis.lzw.algorithm;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

public class BWT {

    public Pair<String, Integer> modifiedSequence(String src) {
        // выводим все символы под подходящем
        ArrayList<String> cycleOffset = getCycleOffsets(src);

        StringBuilder resultTemp = new StringBuilder();
        int index = 0;
        for (String cyc : cycleOffset) {
            int lastSymbolIndex = 0;
            for (int i = 0; i < cyc.length();) {
                int utf8Code = cyc.codePointAt(i);
                if (i + Character.charCount(utf8Code) == cyc.length())
                    lastSymbolIndex = Character.charCount(utf8Code);
                i += Character.charCount(utf8Code);
            }
            resultTemp.append(cyc.substring(cyc.length() - lastSymbolIndex));
            if (cyc.equals(src)) {
                index = cycleOffset.indexOf(cyc);
            }
        }

        return new Pair<>(resultTemp.toString(), index);
    }

    public String restoreInitString(Pair<String, Integer> bwtEncode) {
        ArrayList<StringBuilder> bwtDecode = new ArrayList<>();

        for (int i = 0; i < bwtEncode.getKey().length();) {
            int utf8Code = bwtEncode.getKey().codePointAt(i);
            bwtDecode.add(new StringBuilder(bwtEncode.getKey().substring(i, i + Character.charCount(utf8Code))));
            i += Character.charCount(utf8Code);
        }
        quickSort(bwtDecode, 0, bwtDecode.size() - 1);

        int lastSymbolIndex = 0;
        for (int j = 0; j < bwtEncode.getKey().length(); ) {
            int utf8CodeTemp = bwtEncode.getKey().codePointAt(j);
            if (j + Character.charCount(utf8CodeTemp) == bwtEncode.getKey().length())
                lastSymbolIndex = Character.charCount(utf8CodeTemp);
            j += Character.charCount(utf8CodeTemp);
        }

        for (int i = 0; i < bwtEncode.getKey().length() - lastSymbolIndex;) {
            int utf8Codetemp = bwtEncode.getKey().codePointAt(i);
            int temp = 0;
            for (int j = 0; j < bwtEncode.getKey().length();) {
                int utf8Code = bwtEncode.getKey().codePointAt(j);
                bwtDecode.get(temp++).insert(0, bwtEncode.getKey().substring(j, j + Character.charCount(utf8Code)));
                j += Character.charCount(utf8Code);
            }
            quickSort(bwtDecode, 0, bwtDecode.size() - 1);
            i += Character.charCount(utf8Codetemp);
        }

        return bwtDecode.get(bwtEncode.getValue()).toString();
    }

    public void quickSort(ArrayList<StringBuilder> list, int low, int high) {
        if (list.size() == 0)
            return;

        if (low >= high)
            return;

        int middle = low + (high - low) / 2;
        StringBuilder opora = new StringBuilder(list.get(middle));

        int i = low, j = high;
        while (i <= j) {
            while (list.get(i).toString().compareTo(opora.toString()) < 0) {
                i++;
            }

            while (list.get(j).toString().compareTo(opora.toString()) > 0) {
                j--;
            }

            if (i <= j) {
                StringBuilder temp = new StringBuilder(list.get(i));

                list.get(i).setLength(0);
                list.get(i).append(list.get(j));

                list.get(j).setLength(0);
                list.get(j).append(temp);
                i++;
                j--;
            }
        }

        if (low < j)
            quickSort(list, low, j);

        if (high > i)
            quickSort(list, i, high);
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
}
