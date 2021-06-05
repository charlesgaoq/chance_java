package com;

import java.util.ArrayList;
import java.util.List;

public class TestListIndex {

    public static void main(String[] args) {
        List<Integer> t = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) {
            t.add(i);
        }
        int index = 0;
        print(t.get(index++));
        print(t.get(index++), t.get(index++));

    }

    public static void print(int... i) {
        for (int j = 0; j < i.length; j++) {
            System.out.print(i[j] + " ");
        }
        System.out.println("  -- ");
    }
}
