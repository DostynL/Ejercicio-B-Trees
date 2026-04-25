package com.sebdeveloper6952;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
    // Public API
    // -----------------------------------------------------------------------

    @Override
    public int size() { return size; }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public V get(K key) {
        Entry<K, V> entry = findEntry(key);
        return entry == null ? null : entry.value;
    }

    @Override
    public boolean containsKey(K key) {
        return findEntry(key) != null;
    }

    @Override
    public V put(K key, V value) { return null; }

    @Override
    public V remove(K key) { return null; }

    public List<K> keysInOrder() {
        List<K> out = new ArrayList<>(size);
        collect(root, out);
        return out;
    }

    // -----------------------------------------------------------------------
    // Lookup
    // -----------------------------------------------------------------------

    private Entry<K, V> findEntry(K key) {
        Objects.requireNonNull(key, "key");
        Node<K, V> node = root;
        while (node != null) {
            int cmp0 = compare(key, node.entries[0].key);
            if (cmp0 == 0) return node.entries[0];
            if (cmp0 < 0) { node = node.children[0]; continue; }
            if (node.is2Node()) { node = node.children[1]; continue; }
            int cmp1 = compare(key, node.entries[1].key);
            if (cmp1 == 0) return node.entries[1];
            node = (cmp1 < 0) ? node.children[1] : node.children[2];
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

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