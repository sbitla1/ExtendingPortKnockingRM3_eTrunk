package keypair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Test1 {


    public static void main(String args[]) {

        int input1[] = new int[]{1, 2, 1, 2};
        int input2[] = new int[]{7, 7, 7};
        int input3[] = new int[]{1, 2, 2, 3};
        //Custom input
        int input4[] = new int[]{1,1,1};

        Test1 obj = new Test1();
        System.out.println(obj.solution(input1));
        System.out.println(obj.solution(input2));
        System.out.println("Input 3: " + obj.solution(input3));
        System.out.println("Input 4: " + obj.solution(input4));
    }

    public boolean solution(int[] A) {
        if (A.length % 2 != 0) {
            return false;
        }
        Set<Integer> indexSet = new HashSet();

        for (int i = 0; i < A.length; i++) {
            if (indexSet.contains(i)) {
                continue;
            }
            boolean pairFound = false;

            int pair = 0;
            for (int j = 0; j < A.length; j++) {
                if (i == j || indexSet.contains(j)) {
                    continue;
                }
                if (A[i] == A[j]) {
                    pairFound = true;
                    pair = j;
                    break;
                }

            }
            if (pairFound) {
                indexSet.add(i);
                indexSet.add(pair);
            }
        }
        return indexSet.size() == A.length;
    }
}
