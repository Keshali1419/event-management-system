package com.faculty.eventmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private String eventType;
    private int maxAttendees;
    private int currentAttendees;

    private String imageUrl;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;


    @Enumerated(EnumType.STRING)
    private EventStatus status;
}