package com.example.ecom.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shippers")
@Entity
public class Shipper {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int shipperID;
    private long phone;
    private String shipperName;
}
