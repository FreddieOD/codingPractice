package com.Graphs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Djikstra {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("/home/freddie/Desktop/djikstra"));
        int numOfCases = Integer.valueOf(br.readLine());
        for (int i = 0; i < numOfCases; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int numOfVertices = Integer.valueOf(st.nextToken());
            int numOfEdges = Integer.valueOf(st.nextToken());
            int[] vertices = new int[numOfVertices];
            int[][] graph = new int[numOfVertices][numOfVertices];
            for (int k = 0; k < graph.length; k++) {
                for (int l = 0; l < graph.length; l++) {
                    graph[k][l] = -1;
                }
            }
            for (int j = 0; j < numOfEdges; j++) {
                st = new StringTokenizer(br.readLine());
                int vertex1 = Integer.valueOf(st.nextToken()) - 1;
                int vertex2 = Integer.valueOf(st.nextToken()) - 1;
                int edgeWeight = Integer.valueOf(st.nextToken());
                if (graph[vertex1][vertex2] != -1 && edgeWeight < graph[vertex1][vertex2]) {
                    graph[vertex1][vertex2] = edgeWeight;
                    graph[vertex2][vertex1] = edgeWeight;
                } else if (graph[vertex1][vertex2] == -1) {
                    graph[vertex1][vertex2] = edgeWeight;
                    graph[vertex2][vertex1] = edgeWeight;
                }

            }
            st = new StringTokenizer(br.readLine());
            int startVertex = Integer.valueOf(st.nextToken()) - 1;

            for (int j = 0; j < numOfVertices; j++) {
                if (j != startVertex) {
                    vertices[j] = Integer.MAX_VALUE;
                } else {
                    vertices[j] = 0;
                }
            }
            int[] minDistances = djikstra(graph, vertices, startVertex);
            printArray(minDistances, startVertex);
            if (i != numOfCases - 1) {
                System.out.println();
            }
        }
    }

    private static void printArray(int[] array, int startNode) {
        for (int i = 0; i < array.length; i++) {
            if (i != startNode) {
                //This is to check if a vertex is disconnected. The array[i] < 0 is when a node is not connected
                //to the subgraph of the start node but is connected to other nodes also not connected to the subgraph.
                //Integer.MAX_VALUE then gets added to a weight of an edge and becomes a large negative number due to
                //two's compliment negation of java.
                if(array[i] == Integer.MAX_VALUE || array[i] < 0){
                    System.out.print(-1);
                }else {
                    System.out.print(array[i]);
                }
                if (i != array.length - 1) {
                    System.out.print(" ");
                }
            }
        }
    }

    private static int[] djikstra(int[][] graph, int[] vertices, int startVertex) {
        int[] visited = new int[vertices.length];
        int[] notVisited = new int[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            notVisited[i] = 1;
        }
        visited[startVertex] = 1;
        notVisited[startVertex] = -1;

        while (!isEmptyArray(notVisited)) {
            for (int i = 0; i < graph.length; i++) {
                if (graph[startVertex][i] >= 0 && graph[startVertex][i] + vertices[startVertex] < vertices[i]) {
                    vertices[i] = graph[startVertex][i] + vertices[startVertex];
                }
            }
            startVertex = findMin(vertices, visited);
            visited[startVertex] = 1;
            notVisited[startVertex] = -1;
        }
        return vertices;
    }

    private static boolean isEmptyArray(int[] array) {
        for (int i : array) {
            if (i > -1) {
                return false;
            }
        }
        return true;
    }

    private static int findMin(int[] array, int[] visited) {
        if (array != null && array.length > 0) {
            int min = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < array.length; i++) {
                if (array[i] <= min && visited[i] == 0) {
                    min = array[i];
                    minIndex = i;
                }
            }
            return minIndex;
        }
        return -1;
    }
}
