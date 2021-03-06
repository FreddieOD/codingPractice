package com.dynamic;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

public class ThreeUrns {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int numberOfBeans = Integer.valueOf(br.readLine());
        int maxNumberOfBeansToMove = Integer.valueOf(br.readLine());

        MyOwnMap<Integer, Integer, BigInteger> mapOfSubstructures = new MyOwnMap<>(100000000);
        ThreeUrns threeUrns = new ThreeUrns();
        double startTime = System.currentTimeMillis();
        System.out.println(threeUrns.getNumOfMoves(numberOfBeans, 0, maxNumberOfBeansToMove, mapOfSubstructures));
        double endTime = System.currentTimeMillis();
        double totalTime = endTime - startTime;
        System.out.println(totalTime / 1000D);
    }

    public BigInteger getNumOfMoves(int numberOfBeansInFirstUrn, int numberOfBeansInSecondUrn, int maxNumOfBeansToMove, MyOwnMap map) {
        BigInteger count = BigInteger.valueOf(0);
        if (map.get(numberOfBeansInFirstUrn, numberOfBeansInSecondUrn) != null) {
            return (BigInteger) map.get(numberOfBeansInFirstUrn, numberOfBeansInSecondUrn).getValue();
        }
        if (numberOfBeansInFirstUrn == 0 && numberOfBeansInSecondUrn == 0) {
            return BigInteger.valueOf(1);
        }
        for (int i = 1; i <= min(maxNumOfBeansToMove, numberOfBeansInFirstUrn); i++) {
            count = count.add(getNumOfMoves(numberOfBeansInFirstUrn - i, numberOfBeansInSecondUrn + i, maxNumOfBeansToMove, map));
        }
        for (int i = 1; i <= min(maxNumOfBeansToMove, numberOfBeansInSecondUrn); i++) {
            count = count.add(getNumOfMoves(numberOfBeansInFirstUrn, numberOfBeansInSecondUrn - i, maxNumOfBeansToMove, map));
        }
        map.put(numberOfBeansInFirstUrn, numberOfBeansInSecondUrn, count);
        return count;
    }

    private int min(int i, int j) {
        return i < j ? i : j;
    }


    //Map with a key that has two values. Collision resolution by means of linked list. Not the most efficient (could use tree)
    //Does not dynamically resize either. Need to implement this.
    private static class MyOwnMap<K1, K2, V extends BigInteger> {
        private MyOwnBinaryTree<K1, K2, V>[] entries;
        private int size;

        public MyOwnMap(int size) {
            this.size = size;
            entries = new MyOwnBinaryTree[size];
        }

        public void put(K1 key1, K2 key2, V value) {
            MyOwnKey<K1, K2> key = new MyOwnKey(key1, key2);
            MyOwnEntry<K1, K2, V> entry = new MyOwnEntry<>(key1, key2, value, null, null);

            MyOwnBinaryTree<K1, K2, V> entryInEntries = entries[key.hashCode() % size];
            if (entryInEntries == null) {
                entries[key.hashCode() % size] = new MyOwnBinaryTree<>(key1, key2, value);
            } else {
                entryInEntries.insert(entry);
            }
        }

        public MyOwnEntry get(K1 key1, K2 key2) {
            MyOwnKey<K1, K2> key = new MyOwnKey(key1, key2);
            MyOwnBinaryTree<K1, K2, V> entry = entries[key.hashCode() % size];
            if (entry == null) {
                return null;
            } else {
                return entries[key.hashCode() % size].search(key);
            }
        }
    }

    private static class MyOwnEntry<K1, K2, V extends BigInteger> {
        private V value;
        private MyOwnKey<K1, K2> key;
        private MyOwnEntry left;
        private MyOwnEntry right;


        public MyOwnEntry(K1 key1, K2 key2, V value, MyOwnEntry left, MyOwnEntry right) {
            this.key = new MyOwnKey<>(key1, key2);
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public MyOwnKey getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public MyOwnEntry getLeft() {
            return left;
        }

        public void setLeft(MyOwnEntry left) {
            this.left = left;
        }

        public void setRight(MyOwnEntry right) {
            this.right = right;
        }

        public MyOwnEntry getRight() {
            return right;
        }

        public boolean lessThan(MyOwnEntry node) {
            return this.getKey().hashCode() <= node.getKey().hashCode();
        }
    }

    private static class MyOwnKey<K1, K2> {
        private K1 key1;
        private K2 key2;

        public MyOwnKey(K1 key, K2 value) {
            this.key1 = key;
            this.key2 = value;
        }

        public K1 getKey1() {
            return key1;
        }

        public K2 getKey2() {
            return key2;
        }

        //this weird bit shifting voodoo has a proof in Donald Knuth's book somewhere. It's designed to disperse the values
        //evenly across the array.
        @Override
        public int hashCode() {
            int hash = 7;
            hash = hash * 31 + key1.hashCode();
            hash = hash * 31 + key2.hashCode();
            hash ^= (hash >>> 20) ^ (hash >>> 12);
            return hash ^ (hash >>> 7) ^ (hash >>> 4);

        }

        public boolean equals(MyOwnKey key) {
            return this.key1 == key.getKey1() && this.key2 == key.getKey2();
        }
    }

    public static class MyOwnBinaryTree<K1, K2, V extends BigInteger> {
        private MyOwnEntry<K1, K2, V> root = null;

        public MyOwnBinaryTree() {
        }

        public MyOwnBinaryTree(K1 key1, K2 key2, V value) {
            root = new MyOwnEntry<>(key1, key2, value, null, null);
        }

        public MyOwnEntry search(MyOwnKey key) {
            return search(key, this.root);
        }

        private MyOwnEntry search(MyOwnKey key, MyOwnEntry currentNode) {
            if (currentNode == null) {
                return null;
            } else if (key.equals(currentNode.getKey())) {
                return currentNode;
            } else if (key.hashCode() <= currentNode.getKey().hashCode()) {
                return search(key, currentNode.getLeft());
            } else {
                return search(key, currentNode.getRight());
            }
        }

        //start at root
        public boolean insert(MyOwnEntry node) {
            return insert(node, this.root);
        }

        private boolean insert(MyOwnEntry node, MyOwnEntry currentNode) {
            if (currentNode == null) {
                this.root = node;
                return true;
            }

            //Don't insert if there are equal keys in the tree (duplicate info).
            if (!currentNode.getKey().equals(node.getKey())) {
                if (node.lessThan(currentNode)) {
                    if (currentNode.getLeft() == null) {
                        currentNode.setLeft(node);
                        return true;
                    }
                    return insert(node, currentNode.getLeft());
                } else {
                    if (currentNode.getRight() == null) {
                        currentNode.setRight(node);
                        return true;
                    }
                    return insert(node, currentNode.getRight());
                }
            }
            return false;
        }

        private MyOwnEntry findMinNodeInSubTree(MyOwnEntry currentNode) {
            if (currentNode.getLeft() == null) {
                return currentNode;
            } else {
                return findMinNodeInSubTree(currentNode.getLeft());
            }
        }

        private MyOwnEntry delete(MyOwnKey key, MyOwnEntry node) {
            if(node == null) return node;

            if (key.hashCode() < node.getKey().hashCode()) {
                node.left = delete(key, node.left);
            } else if (key.hashCode() > node.getKey().hashCode()) {
                node.right = delete(key, node.right);
            } else {
                if (node.right == null) {
                    return node.left;
                } else if (node.left == null) {
                    return node.right;
                }
                MyOwnEntry minNodeOfRightSubtree = findMinNodeInSubTree(node.getRight());
                node.key = minNodeOfRightSubtree.getKey();
                node.value = minNodeOfRightSubtree.getValue();

                node.right = delete(node.key, node.getRight());
            }
            return node;
        }
    }
}
