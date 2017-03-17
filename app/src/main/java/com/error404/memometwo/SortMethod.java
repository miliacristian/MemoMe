package com.error404.memometwo;

public class SortMethod {// asc/desc+colore/titolo/datacreazione/datamodifica

    private String ascDesc;//può diventare boolean 0=crescente 1=descrescente
    private String sortType;//può diventare anche enum(deve essere uguale agli attributi della tabella memos su cui è definito l'ordinamento

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
