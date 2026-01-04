package model;

public class MyArrayList<T> {
    private Object[] data;
    private int size;

    public MyArrayList() {
        this.data = new Object[8];
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public void add(T item) {
        if (size == data.length) grow();
        data[size++] = item;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return (T) data[index];
    }

    public void set(int index, T item) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        data[index] = item;
    }

    private void grow() {
        Object[] newData = new Object[data.length * 2];
        for (int i = 0; i < data.length; i++) newData[i] = data[i];
        data = newData;
    }
}
