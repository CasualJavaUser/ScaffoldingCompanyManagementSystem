package org.example.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
public class Client {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", nullable = false)
    private List<Offer> offers = new ArrayList<>();

    public Client() {}

    public Client(String companyName, String phone, String email) {
        this.companyName = companyName;
        this.phone = phone;
        this.email = email;
    }

    public void addOffer(Offer offer) {
        offers.add(offer);
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public int getOfferCount() {
        return offers.size();
    }

    public List<Offer> getOffersCopy() {
        return new ArrayList<>(offers);
    }
}
