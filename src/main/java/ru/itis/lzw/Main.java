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

            Pair<String, Integer> bwtResult = bwt.modifySequence(source);

            source = bwtResult.getKey();

            String alphabet = lzw.getAlphabet(source);

            lzw.initDictionary(alphabet);

            String encoded = lzw.algorithm(source);

            lzw.writeToFile(
                    "./coderResult.txt",
                    alphabet + "_S_P_L-I-T_A_L_P_H_A-B-_E_T" + bwtResult.getValue() + "_S_P_L-I-T_D_A-T_-A" + encoded
            );
        } else if (mode == 2) {
            Pair<Pair<String, Integer>, String> data = lzwDecode.readFile("./coderResult.txt");

            String alphabet = data.getKey().getKey();
            Integer index = data.getKey().getValue();
            String encoded = data.getValue();

            if (encoded.equals("")) {
                lzwDecode.writeToFile("./decoderResult.txt", "");
                return;
            }

            lzwDecode.initDictionary(alphabet);

            String lzwDecoded = lzwDecode.decode(encoded);

            String bwtDecoded = bwt.restoreInitString(lzwDecoded, index);

            lzwDecode.writeToFile("./decoderResult.txt", bwtDecoded);
        }
    }
}
