package com.revisao.demo.enums;

public enum CarroStatus {

    DISPONIVEL("disponivel"),
    MANUTENCAO("manutencao");

    private String status;

    CarroStatus(String status) {
        this.status=status;
    }

    private String getStatus(){
        return status;
    }

    private void setStatus(String status){
        this.status=status;
    }
}
