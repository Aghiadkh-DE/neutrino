package com.paperlessdesktop.util;

import javafx.collections.ObservableList;

import java.util.Iterator;
import java.util.List;

public final class ArrayOperations {
    /**
     * Remove elements from the source List
     * @param elementsToRemove Elements to be removed from the source list
     * @param sourceList Source List to update
     * @param <T> Generic Type that should be saved among both parameters
     */
    public static <T> void removeAll(ObservableList<T> elementsToRemove, List<T> sourceList){
        Iterator<T> iterator = sourceList.iterator();
        while (iterator.hasNext()){
            T element = iterator.next();

            for(T temp : elementsToRemove){
                if(element.equals(temp)){
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public static <T> void addAll(ObservableList<T> elementsToAdd, List<T> destinationList){
        destinationList.addAll(elementsToAdd);
    }
}
