package sort;

import java.util.Arrays;

/**
 * @version 1.0
 * @Author Wancy
 * @Date 2024/10/21 23:32
 */
public class HeapSort implements IArraySort{
    @Override
    public int[] sort(int[] sourceArray) {

        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);
        int n = arr.length;

        // 建堆 从倒数第一个非叶子节点开始
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, i, n);
        }

        // 排序
        for (int i = n - 1; i >= 0; i--) {
            int tmp = arr[0];
            arr[0] = arr[i];
            arr[i] = tmp;

            heapify(arr, 0, i);
        }

        return arr;
    }

    public void heapify(int[] arr, int cur, int n){
        int lson = cur * 2 + 1;
        int rson = cur * 2 + 2;
        int maxIndex = cur;
        if(lson < n && arr[lson] > arr[maxIndex]){
            maxIndex = lson;
        }
        if(rson < n && arr[rson] > arr[maxIndex]){
            maxIndex = rson;
        }
        if(maxIndex != cur){
            int tmp = arr[cur];
            arr[cur] = arr[maxIndex];
            arr[maxIndex] = tmp;
            heapify(arr, maxIndex, n);
        }
    }
}
