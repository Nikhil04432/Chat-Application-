
//Find Maximum and Minimum in a List

import java.util.*;

public class ListMinMax {
    public static void main(String[] args) {
        
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        List<Integer> list = Arrays.asList(44, 7, -178, 89, 63, 97, 12);

        System.out.println("Elements in list"+ list);

        for(int i : list){
            max = Math.max(max,i);
            min = Math.min(min,i);
         }

        System.out.println(list);
        System.out.println("Minimum Element is : "+ min);
        System.out.println("Maximum Element is : "+ max);
    }
}
