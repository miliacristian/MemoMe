package com.error404.memome;


public class RowSort {// asc/desc+colore/titolo/datacreazione/datamodifica

    private String ascDesc;
    private String sortType;

    public RowSort(String ascDesc,String sortType){
        this.ascDesc=ascDesc;
        this.sortType=sortType;
    }
    public String getAscDesc() {
        return ascDesc;
    }

    public String getSortType() {
        return sortType;
    }
    
}
