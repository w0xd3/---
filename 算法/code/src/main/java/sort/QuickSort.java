package sort;

import java.util.Arrays;

/**
 * @version 1.0
 * @Author Wancy
 * @Date 2024/10/22 13:24
 */
// 2 1 1 5
public class QuickSort implements IArraySort{
    @Override
    public int[] sort(int[] sourceArray) {
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);
        qsort(arr, 0, arr.length - 1);
        return arr;
    }

    public void qsort(int[] arr, int left, int right){
        if(left >= right) return;
        int p = partition(arr, left, right);
        qsort(arr, left, p - 1);
        qsort(arr, p + 1, right);
    }

    public int partition(int[] arr, int left, int right){
        // pivot = left
        int index = left + 1;
        for (int cur = index; cur <= right; cur++) {
            if(arr[cur] < arr[left]){
                swap(arr, index++, cur);
            }
        }
        // index - 1 是最后一个比pivot小的数
        swap(arr, index - 1, left);
        return index - 1;
    }

    public void swap(int[] arr, int i, int j){
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

}
