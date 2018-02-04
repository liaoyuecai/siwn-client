package com.swin;

import lombok.Data;

import java.io.Serializable;

@Data
public class Person {
    private int age;
    private String name;
    private int high;

    public Person(int age, String name, int high) {
        this.age = age;
        this.name = name;
        this.high = high;
    }
}
