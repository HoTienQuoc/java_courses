package linkedlist_vs_arraylist_big_o_notation.list.homework.parameterized;

public interface ListIterableParameterized<T> {

    /**
     * Returns object of ListIterator.
     *
     * @return object of ListIterator
     */
    ListIteratorParameterized<T> listIterator();
}
