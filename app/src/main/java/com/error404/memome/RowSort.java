package com.error404.memome;


public class RowSort {//classe che definisce l oggetto "ordinamento" fromato da:
// tipo ordinamento(colore/titolo/datacreazione/datamodifica/emoji)
    //crescente/decrescente

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
