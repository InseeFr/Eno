package fr.insee.eno.core.utils;

import java.util.TreeMap;

public class RomanNumber { // https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java

    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {

        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

    /**
     * Return the roman representation of number.
     *
     * @param number : must be > 0
     * @return : a String which is the roman representation of number
     * @throws IllegalArgumentException if number is not > 0
     */
    public static String toRoman(int number) {
        if (number <= 0)
            throw new IllegalArgumentException("RomanNumber.toNumber only accepts > 0 numbers (" + number + " provided)");
        return toRomanRecursively(number);
    }

    private static String toRomanRecursively(int number) {
        var l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }


}