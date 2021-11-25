package com.example.demo.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("ROLE_MANAGER")
public class Manager extends User {
    @Builder
    public Manager() {
        super();
    }
}
