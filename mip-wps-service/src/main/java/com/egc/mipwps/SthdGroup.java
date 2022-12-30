package com.egc.mipwps;

import javax.persistence.*;

@Entity
@Table(name = "sthdgroup")
public class SthdGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    public SthdGroup(){

    }

    private String name;

    public SthdGroup(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}
