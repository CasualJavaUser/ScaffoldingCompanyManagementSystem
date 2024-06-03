package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "client")
public class Client {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    public Client() {}

    public Client(String companyName, String phone, String email) {
        this.companyName = companyName;
        this.phone = phone;
        this.email = email;
    }
}
