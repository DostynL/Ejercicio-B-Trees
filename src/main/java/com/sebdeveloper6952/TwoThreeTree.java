package com.sebdeveloper6952;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TwoThreeTree<K, V> implements Map<K, V> {

    private final Comparator<? super K> comparator;
    private Node<K, V> root;
    private int size;

    public TwoThreeTree() {
        this(null);
    }

    public TwoThreeTree(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    // -----------------------------------------------------------------------
    // Public API — por implementar
    // -----------------------------------------------------------------------

    @Override public int size()      { return size; }
    @Override public boolean isEmpty() { return size == 0; }
    @Override public V get(K key)    { return null; }
    @Override public boolean containsKey(K key) { return false; }
    @Override public V put(K key, V value) { return null; }
    @Override public V remove(K key) { return null; }

    public List<K> keysInOrder() {
        List<K> out = new ArrayList<>(size);
        collect(root, out);
        return out;
    }

    private void collect(Node<K, V> node, List<K> out) {
        if (node == null) return;
        if (node.isLeaf()) {
            for (int i = 0; i < node.numKeys; i++) out.add(node.entries[i].key);
            return;
        }
        collect(node.children[0], out);
        out.add(node.entries[0].key);
        collect(node.children[1], out);
        if (node.is3Node()) {
            out.add(node.entries[1].key);
            collect(node.children[2], out);
        }
    }

    // -----------------------------------------------------------------------
    // Helper de comparación
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private int compare(K a, K b) {
        if (comparator != null) return comparator.compare(a, b);
        return ((Comparable<? super K>) a).compareTo(b);
    }

    // -----------------------------------------------------------------------
    // Tipos internos
    // -----------------------------------------------------------------------

    static final class Entry<K, V> {
        final K key;
        V value;
        Entry(K key, V value) { this.key = key; this.value = value; }
    }

    static final class Node<K, V> {
        final Entry<K, V>[] entries;
        final Node<K, V>[]  children;
        int numKeys;

        @SuppressWarnings("unchecked")
        private Node() {
            this.entries  = (Entry<K, V>[]) new Entry[2];
            this.children = (Node<K, V>[])  new Node[3];
        }

        static <K, V> Node<K, V> leafOf(Entry<K, V> entry) {
            Node<K, V> n = new Node<>();
            n.entries[0] = entry;
            n.numKeys = 1;
            return n;
        }

        boolean isLeaf()  { return children[0] == null; }
        boolean is2Node() { return numKeys == 1; }
        boolean is3Node() { return numKeys == 2; }
    }
}