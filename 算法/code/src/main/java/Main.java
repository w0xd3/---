import sort.*;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @version 1.0
 * @Author Wancy
 * @Date 2024/10/20 23:16
 */
public class Main {
    public static void main(String[] args) {
        // 1 6 2 5 4 9 3
        IArraySort sort = new CountSort();

        Scanner scan = new Scanner(System.in);
        String[] split = scan.nextLine().split(" ");
        int[] sourceArray = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            sourceArray[i] = Integer.parseInt(split[i]);
        }

        System.out.println(Arrays.toString(sourceArray));
        System.out.println(Arrays.toString(sort.sort(sourceArray)));
    }
}
