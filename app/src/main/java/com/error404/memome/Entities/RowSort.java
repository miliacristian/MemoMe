package com.error404.memome.Entities;


public class RowSort {//classe che definisce l oggetto "ordinamento" formato da:
// tipo ordinamento(colore,titolo,datacreazione,datamodifica o emoji)
    //crescente o decrescente

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
