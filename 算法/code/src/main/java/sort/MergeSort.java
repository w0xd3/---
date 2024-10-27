package sort;

import java.util.Arrays;

/**
 * @version 1.0
 * @Author Wancy
 * @Date 2024/10/21 21:25
 */
public class MergeSort implements IArraySort{
    @Override
    public int[] sort(int[] sourceArray) {
        if(sourceArray.length < 2) return sourceArray;

        int[] tmp = Arrays.copyOf(sourceArray, sourceArray.length);
        int mid = tmp.length / 2;

        int[] left = Arrays.copyOfRange(tmp, 0, mid);
        int[] right = Arrays.copyOfRange(tmp, mid, tmp.length);

        return merge(sort(left), sort(right));
    }

    private int[] merge(int[] left, int[] right) {

        int[] temp = new int[right.length + left.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while(i < left.length && j < right.length){
            if(left[i] < right[j]){
                temp[k] = left[i++];
            }else {
                temp[k] = right[j++];
            }
            k++;
        }
        while(i < left.length){
            temp[k++] = left[i++];
        }
        while (j < right.length){
            temp[k++] = right[j++];
        }

        return temp;
    }
}

