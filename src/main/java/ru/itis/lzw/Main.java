package ru.itis.lzw;

import ru.itis.lzw.algorithm.LZW;

public class Main {

    private final static LZW lzw = new LZW();
    private final static LZW.Prepare lzwPrepare = new LZW.Prepare();
    private final static LZW.Decode lzwDecode = new LZW.Decode();

    public static void main(String[] args) {

        String source = lzwPrepare.readFile("D:\\Another\\Univercity\\Тесты\\LZW\\src\\main\\test.txt");
        lzw.setDictionary(lzwPrepare.initDictionary(source));
        System.out.println(lzw.getDictionary());

        System.out.println(lzw.algorithm(source));

        System.out.println(lzw.getDictionary());
    }
}
