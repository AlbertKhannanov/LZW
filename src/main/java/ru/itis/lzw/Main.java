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

        System.out.println("========== Режим работы ===========");
        System.out.println("1: Кодирование \t ----- \t  2: Декодирование");
        int mode = scan.nextInt();

        if (mode == 1) {
            System.out.println("Введите путь до файла с данными: ");
            String path = scan.next();

            String source = lzwPrepare.readFile(path);

            Pair<String, Integer> bwtResult = bwt.modifiedSequence(source);

            source = bwtResult.getKey();

            lzw.initDictionary(source);

            String encoded = lzw.algorithm(source);

            lzw.writeToFile(
                    "./coderResult.txt",
                    bwtResult.getKey() + "_S_P_L-I-T_A_L_P_H_A-B-_E_T" + bwtResult.getValue() + "_S_P_L-I-T_D_A-T_-A" + encoded
            );
        } else if (mode == 2) {
            String rawData = lzwDecode.readFile("./coderResult.txt");
            Pair<Pair<String, Integer>, String> indexAndEncoded = lzwDecode.getDataForDecoder(rawData);

            String alphabet = indexAndEncoded.getKey().getKey();
            Integer index = indexAndEncoded.getKey().getValue();
            String encoded = indexAndEncoded.getValue();

            lzwDecode.initDictionary(alphabet);

            System.out.println(lzwDecode.dictionary);

            String lzwDecoded = lzwDecode.decode(encoded);

            String bwtDecoded = bwt.restoreInitString(new Pair<>(lzwDecoded, index));

            lzwDecode.writeToFile("./decoderResult.txt", bwtDecoded);
        }
    }
}
