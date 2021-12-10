package ru.itis.lzw;

import javafx.util.Pair;
import ru.itis.lzw.algorithm.BWT;
import ru.itis.lzw.algorithm.LZW;

import java.util.Scanner;


public class Main {

    private final static BWT bwt = new BWT();
    private final static LZW lzw = new LZW();
    private final static LZW.Prepare lzwPrepare = new LZW.Prepare();
    private final static LZW.Decode lzwDecode = new LZW.Decode();

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);



        String source = lzwPrepare.readFile("D:\\Another\\Univercity\\Тесты\\LZW\\src\\main\\test.txt");
        Pair<String, Integer> bwtResult = bwt.modifiedSequence(source);

        source = bwtResult.getKey();
        lzw.setDictionary(lzwPrepare.initDictionary(source));

        String encoded = lzw.algorithm(source);

        System.out.println("Размер исходной строки: " + (source.getBytes().length * 8) + " бит");
        System.out.println("Размер закодированной строки строки: " + encoded.length() + " бит");

        System.out.println("\n------------ DECODE ------------");

        System.out.println(bwtResult);
        lzwDecode.initDictionary(source);
        String decode = lzwDecode.decode(encoded);

        System.out.println(bwt.restoreInitString(bwtResult));
    }
}
