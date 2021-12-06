package ru.itis.lzw.model;

public class DictionaryPart {

    private Integer number;
    private String binaryCode;

    public DictionaryPart(Integer n, String bc) {
        number = n;
        binaryCode = bc;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getBinaryCode() {
        return binaryCode;
    }

    public void setBinaryCode(String binaryCode) {
        this.binaryCode = binaryCode;
    }

    @Override
    public String toString() {
        return "DictionaryPart{" +
                "number=" + number +
                ", binaryCode='" + binaryCode + '\'' +
                '}';
    }
}
