package ru.itis.lzw.algorithm;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

public class BWT {

    public Pair<String, Integer> modifiedSequence(String src) {
        // выводим все символы под подходящем
        ArrayList<String> cycleOffset = getCycleOffsets(src);

        System.out.println(cycleOffset);

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

        for (int i = 0; i < bwtEncode.getKey().length(); i++) {
            bwtDecode.add(new StringBuilder(bwtEncode.getKey().substring(i, i + 1)));
        }
        quickSort(bwtDecode, 0, bwtDecode.size() - 1);

        for (int i = 0; i < bwtEncode.getKey().length() - 1; i++) {
            for (int j = 0; j < bwtEncode.getKey().length(); j++) {
                bwtDecode.get(j).insert(0, bwtEncode.getKey().charAt(j));
            }
            quickSort(bwtDecode, 0, bwtDecode.size() - 1);
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
        ArrayList<String> cycleOffset = new ArrayList<>();

        StringBuilder cycleTemp = new StringBuilder(str);
        for (int i = 0; i < str.length();) {
            int utf8Code = str.codePointAt(i);
            cycleOffset.add(cycleTemp.toString());
            cycleTemp.append(cycleTemp.substring(Character.charCount(utf8Code)));
            cycleTemp.append(cycleTemp.substring(0, Character.charCount(utf8Code)));
            cycleTemp.delete(0, str.length());
            i += Character.charCount(utf8Code);
        }
        Collections.sort(cycleOffset);

        return cycleOffset;
    }
}
