package ru.itis.lzw;

import ru.itis.lzw.algorithm.LZW;
import ru.itis.lzw.model.DictionaryPart;

import java.util.LinkedHashMap;

public class Main {

    private final static LZW lzw = new LZW();
    private final static LZW.Prepare lzwPrepare = new LZW.Prepare();
    private final static LZW.Decode lzwDecode = new LZW.Decode();

    public static void main(String[] args) {

        String source = lzwPrepare.readFile("D:\\Another\\Univercity\\Тесты\\LZW\\src\\main\\test.txt");
        lzw.setDictionary(lzwPrepare.initDictionary(source));

        LinkedHashMap<String, DictionaryPart> initialDict = (LinkedHashMap<String, DictionaryPart>)lzw.getDictionary().clone();

        String encoded = lzw.algorithm(source);

        System.out.println(encoded);
        System.out.println(lzw.getDictionary());

        System.out.println("\n------------ DECODE ------------");

        lzwDecode.initDictionary(source);
        System.out.println(lzwDecode.decode(encoded));

        System.out.println(lzwDecode.getDictionary());
    }
}
