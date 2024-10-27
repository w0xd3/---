package sort;

import java.util.Arrays;

/**
 * @version 1.0
 * @Author Wancy
 * @Date 2024/10/21 0:19
 */
public class ShellSort implements IArraySort{
    @Override
    public int[] sort(int[] sourceArray) {
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);
        for (int step = arr.length / 2; step >= 1; step /= 2) {
            int i = 0;
            while (i + step < arr.length) {
                if(arr[i] > arr[i + step]){
                    int tmp = arr[i + step];
                    arr[i + step] = arr[i];
                    arr[i] = tmp;
                }
                i += step;
            }
        }
        return arr;
    }
}
