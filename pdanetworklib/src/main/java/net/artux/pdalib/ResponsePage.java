package net.artux.pdalib;

import java.util.List;

public class ResponsePage<T> {

    private int lastPage;
    private List<T> data;
    private int dataSize;
    private Long queryDataSize;
    private int number;
    private int size;
    private String sortDirection;
    private String sortBy;

    public int getLastPage() {
        return lastPage;
    }

    public List<T> getData() {
        return data;
    }

    public int getDataSize() {
        return dataSize;
    }

    public Long getQueryDataSize() {
        return queryDataSize;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public String getSortBy() {
        return sortBy;
    }
}