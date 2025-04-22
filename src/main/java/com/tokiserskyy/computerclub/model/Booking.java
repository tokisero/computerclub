package com.tokiserskyy.computerclub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "computer_id")
    private Computer computer;

    @ManyToOne(fetch = FetchType.LAZY, cascade =  { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "user_id")
    private Person person;

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", computer='" + computer.toString() + '\'' +
                ", person='" + person.toString() + '\'' +
                '}';
    }
}