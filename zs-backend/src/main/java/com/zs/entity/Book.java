package com.zs.entity;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String title;

    @Column(length = 128, nullable = false)
    private String author;

    @Column(name = "cover_url", length = 512)
    private String coverUrl;

    @Column(name = "book_url", length = 1024)
    private String bookUrl;

    @Column(name = "latest_update_url", length = 1024)
    private String latestUpdateUrl;

    @Type(ListArrayType.class)
    @Column(columnDefinition = "text[]")
    private List<String> tags;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "world_background", columnDefinition = "TEXT")
    private String worldBackground;

    @Column(columnDefinition = "TEXT")
    private String highlights;

    @Enumerated(EnumType.STRING)
    @Column(name = "serial_status", length = 16, nullable = false)
    @Builder.Default
    private SerialStatus serialStatus = SerialStatus.ONGOING;

    @Column(name = "total_words")
    private Long totalWords;

    @Column(name = "rating")
    @Builder.Default
    private Integer rating = 0;

    @Column(name = "count_of_review")
    @Builder.Default
    private Integer countOfReview = 0;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Chapter> chapters = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Follow> follows = new ArrayList<>();
}
