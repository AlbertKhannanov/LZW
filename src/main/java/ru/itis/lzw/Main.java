package ru.itis.lzw;

import ru.itis.lzw.algorithm.LZW;

public class Main {

    private final static LZW lzw = new LZW();
    private final static LZW.Prepare lzwPrepare = new LZW.Prepare();
    private final static LZW.Decode lzwDecode = new LZW.Decode();

    public static void main(String[] args) {

        String source = lzwPrepare.readFile("D:\\Another\\Univercity\\Тесты\\LZW\\src\\main\\test.txt");
        lzw.setDictionary(lzwPrepare.initDictionary(source));

        String encoded = lzw.algorithm(source);

        System.out.println("Размер исходной строки: " + (source.getBytes().length * 8) + " бит");
        System.out.println("Размер закодированной строки строки: " + encoded.length() + " бит");

        System.out.println("\n------------ DECODE ------------");

        lzwDecode.initDictionary(source);
        String decode = lzwDecode.decode(encoded);
        System.out.println(decode);
    }
}
