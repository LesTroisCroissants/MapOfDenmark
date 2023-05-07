package program.model;

// This class is heavily influenced by Robert Sedgewick and Kevin Wayne's library "algs4.jar" implementation of "ResizingArrayBag" which has been modified slightly for use in our mapgraph.

public class ResizingArray<Item> {
    private final int INITIAL_CAPACITY = 8;
    private Item[] array;         // array of items
    private int size;            // number of elements

    public ResizingArray() {
        array = (Item[]) new Object[INITIAL_CAPACITY];
        size = 0;
    }

    private void resize(int newCapacity) {
        Item[] auxiliary = (Item[]) new Object[newCapacity];
        for (int i = 0; i < size; i++)
            auxiliary[i] = array[i];
        array = auxiliary;
    }

    public Item get(int index){
        return array[index];
    }

    public void put(Item item) {
        if (size == array.length) resize(2* array.length);
        array[size++] = item;
    }

    // [1, 2, 3, null, null, 4] | size = 4 // TODO: this could give us problems
    public void put(Item item, int index){
        while (index >= array.length)
            resize(2 * array.length);

        array[index] = item;
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}
