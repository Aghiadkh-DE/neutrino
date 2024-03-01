package com.paperlessdesktop.finder.fileloader;

import java.io.File;
import java.io.Serial;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.RecursiveAction;

/**
 * Multi Threaded Quick sort class
 */
public class ThreadedQuickSort extends RecursiveAction{
    @Serial
    private static final long serialVersionUID = 3475410622189212731L;

    private final List<File> filesList;

    private final int low;
    private final int high;

    /**
     * List of files that holds names as strings.
     */
    private final List<String> fileList;

    /**
     * Cyclic barrier to consume when done
     */
    private CyclicBarrier barrier;

    /**
     * Constructor to be called to start the algorithm.
     * @param filesList List of files that holds names as File instances
     * @param fileList List of files that holds names as strings.
     * @param barrier Barrier reference
     */
    public ThreadedQuickSort(List<File> filesList, List<String> fileList, CyclicBarrier barrier){
        this.filesList = filesList;
        this.low = 0;
        this.high = filesList.size() - 1;
        this.fileList = fileList;
        this.barrier = barrier;
    }

    private ThreadedQuickSort(List<File> filesList, List<String> fileList, int low, int high){
        this.filesList = filesList;
        this.low = low;
        this.high = high;
        this.fileList = fileList;
    }

    /**
     * Swap 2 indices in the given lists.
     * @param filesList List of files that holds names as File instances
     * @param guiList List of files that holds names as strings.
     * @param i First Index
     * @param j Second Index
     */
    private void swap(List<File> filesList, List<String> guiList, int i, int j) {
        if(guiList.get(i).equals(guiList.get(j))){
            return;
        }
		File temp = filesList.get(i);
		filesList.set(i, filesList.get(j));
		filesList.set(j, temp);

        String fileNameTemp = guiList.get(i);
        guiList.set(i, guiList.get(j));
        guiList.set(j, fileNameTemp);
	}

    /**
     * The partition function for the quick sort algorithm
     * @param filesList List of files that holds names as File instances
     * @param guiList List of files that holds names as strings.
     * @param low start index
     * @param high end index
     * @return Return index to split the lists by.
     */
    private int partition(List<File> filesList, List<String> guiList, int low, int high) {
		String pivot = filesList.get(high).getName();  	
		int i = (low - 1);
		
		for (int j = low; j < high; j++) {
			if(filesList.get(j).getName().compareToIgnoreCase(pivot) <= 0) {
				i++;
				swap(filesList, guiList, i, j);
			}
		}
		swap(filesList, guiList, i + 1, high);	
		return i + 1; 
	}

    @Override
    protected void compute() {
        if(low < high){
            //pi -> partitioning index
            int pi = partition(filesList, fileList, low, high);
            ThreadedQuickSort leftRecursion = new ThreadedQuickSort(filesList, fileList, low, pi - 1);
            ThreadedQuickSort rightRecursion = new ThreadedQuickSort(filesList, fileList,  pi + 1, high);

            //forking left sub problem in a new thread
            leftRecursion.fork();
            //computing right sub problem with this thread
            rightRecursion.compute();   
            //wait until the left sub problem is finished
            leftRecursion.join();   
        } 
        if(barrier != null){
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }        
    }

    public List<String> getFileList() {
        return fileList;
    }
}
