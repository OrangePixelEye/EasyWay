package com.bento.easyway;

import java.io.Serializable;

public class User implements Serializable {
    String name;
    String numero;
    String password;
    String cargo;
    String docReference;

    public String getName() {
        return name;
    }

    public String getNumero() {
        return numero;
    }

    public String getDocReference(){
        return docReference;
    }

    public String getCargo(){
        return cargo;
    }

    public String getPassword() {
        return password;
    }

    User(){}

    User(String name,String password,String num){
        this.name = name;
        this.numero = num;
        this.password = password;
    }
}
