package cn.com.rosercode.hostm;

/**
 * @author rosercode
 * @date 2023/8/17 13:24
 */

public class GenericMethodExample {

    public static <T> void swap(T[] array, int index1, int index2) {
        if (index1 < 0 || index1 >= array.length || index2 < 0 || index2 >= array.length) {
            throw new IllegalArgumentException("Invalid indices");
        }

        T temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }

    public static void main(String[] args) {
        Integer[] intArray = {1, 2, 3, 4, 5};
        String[] strArray = {"apple", "banana", "cherry", "date"};

        System.out.println("Before swapping:");
        for (Integer num : intArray) {
            System.out.print(num + " ");
        }
        System.out.println();

        swap(intArray, 1, 3); // Swap the second and fourth elements

        System.out.println("After swapping:");
        for (Integer num : intArray) {
            System.out.print(num + " ");
        }
        System.out.println();

        System.out.println("Before swapping:");
        for (String str : strArray) {
            System.out.print(str + " ");
        }
        System.out.println();

        swap(strArray, 0, 2); // Swap the first and third elements

        System.out.println("After swapping:");
        for (String str : strArray) {
            System.out.print(str + " ");
        }
    }
}
