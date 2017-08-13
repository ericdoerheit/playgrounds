package de.ericdoerheit.basics;

/**
 * Created by ericdoerheit on 19/07/16.
 */
public class ObjectComparison {
    public static void main(String[]args) {
        System.out.println("A == A: " + "A" == "A");

        final Integer i1 = new Integer(1);
        final Integer i2 = new Integer(1);
        System.out.println("1 == 1: " + (i1 == i2));
    }
}
