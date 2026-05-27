package com.zs.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "chapter")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Book book;

    @Column(nullable = false)
    private Integer seq;

    @Column(length = 512, nullable = false)
    private String title;

    @Column(name = "source_url", length = 1024)
    private String sourceUrl;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}
