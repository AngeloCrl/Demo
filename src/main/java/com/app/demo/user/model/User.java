package com.app.demo.user.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> roles;

//    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Token> tokens;

//    @OneToMany(cascade = CascadeType.ALL)
//    @JoinColumn(name = "owner_id")
//    private List<Car> cars = new ArrayList<>();

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastModDate;


    public void addRole(UserRole userRole) {
        if (roles == null) {
            roles = new HashSet<>();
        }

        roles.add(userRole);
        userRole.setUser(this);
    }

    public void removeRole(UserRole userRole) {
        roles.remove(userRole);
        userRole.setUser(null);
    }

    public void removeAllRoles() {
        for (Iterator<UserRole> iterator = roles.iterator(); iterator.hasNext(); ) {
            UserRole userRole = iterator.next();
            iterator.remove();
            userRole.setUser(null);
            roles.remove(userRole);
        }
    }

//    public void addCar(Car car) {
//        if (cars == null) {
//            cars = new ArrayList<>();
//        }
//        cars.add(car);
//    }
//
//    public void removeCar(Car car) {
//        cars.remove(car);
//    }
//
//    public void removeAllCars() {
//        cars.forEach(car -> {
//            cars.remove(car);
//        });
//    }

}