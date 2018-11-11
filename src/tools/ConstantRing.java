package tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConstantRing
{
    private final List<Integer> constants;
    private int min;
    private int max;

    public ConstantRing (int[] arr)
    {
        constants = Arrays.stream(arr).boxed().collect(Collectors.toList());
        min = Collections.min(constants);
        max = Collections.max(constants);;
    }

    int next(int in)
    {
        int n = Collections.binarySearch(constants, in);
        if (n<0)
        {
            n = -n - 1;
        }
        else
        {
            n++;
        }
        if (n == constants.size())
            return constants.get(0);
        return constants.get(n);
    }

    int prev(int in)
    {
        int n = Collections.binarySearch(constants, in);
        if (n<0)
        {
            n = -n - 1;
        }
        n--;
        if (n < 0)
            return constants.get(constants.size() - 1);
        return constants.get(n);
    }

    public static void main (String[] args)
    {
        ConstantRing ring = new ConstantRing(new int[]{9,10,12,13,15,16,18,19,21,22,24,25,27,28,30,31});
//        System.out.println(ring.next(1));  // 9
//        System.out.println(ring.next(9));  // 10
//        System.out.println(ring.next(10)); // 12
//        System.out.println(ring.next(11)); // 12
//        System.out.println(ring.next(31)); // 9
//        System.out.println(ring.next(32)); // 9

        System.out.println(ring.prev(1));  // 31
        System.out.println(ring.prev(9));  // 31
        System.out.println(ring.prev(10)); // 9
        System.out.println(ring.prev(11)); // 10
        System.out.println(ring.prev(31)); // 30
        System.out.println(ring.prev(32)); // 31
    }
}
