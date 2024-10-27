package sort;

import java.util.Arrays;

/**
 * @version 1.0
 * @Author Wancy
 * @Date 2024/10/22 13:37
 */
public class CountSort implements IArraySort{
    @Override
    public int[] sort(int[] sourceArray) {
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);
        int max = findMax(arr);
        int[] freq = new int[max + 1];

        for (int j : arr) {
            freq[j]++;
        }

        int k = 0;
        for (int i = 0; i < freq.length; i++) {
            while(freq[i] > 0){
                arr[k++] = i;
                freq[i]--;
            }
        }

        return arr;
    }

    public int findMax(int[] arr){
        int max = 0;
        for (int i : arr) {
            max = Math.max(i, max);
        }
        return max;
    }

}
