package sort;

import java.util.Arrays;

/**
 * @version 1.0
 * @Author Wancy
 * @Date 2024/10/20 23:12
 */
public class SelectionSort implements IArraySort{
    @Override
    public int[] sort(int[] sourceArray) {
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

        for (int i = 0; i < arr.length - 1; i++) {
            // 假设当前位置为最小值
            int minIndex = i;

            // 在剩余未排序部分寻找最小值
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }

            // 交换当前值与找到的最小值
            if (minIndex != i) {
                int temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;
            }
        }

        return arr;
    }
}
