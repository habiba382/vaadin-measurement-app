
package com.example.entity;



import jakarta.persistence.*;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;



    @OneToOne
    @JoinColumn(name = "person_id")
    private Address address;

    // olemassaolevat getters/setters...

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }



    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
