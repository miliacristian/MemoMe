package com.error404.memometwo;
//chi la usa???
//finita e ordinata
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
    public void setAscDesc(String ascDesc) {
        this.ascDesc = ascDesc;
    }
    public String getSortType() {
        return sortType;
    }
    public void setSortType(String sortType) {
        this.sortType = sortType;
    }
}
