package com.zs.service;

import com.zs.dto.FeedItemResponse;
import com.zs.dto.PagedResponse;
import com.zs.entity.*;
import com.zs.repository.FeedRepository;
import com.zs.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FollowRepository followRepository;
    private final FeedRepository feedRepository;

    @Transactional
    public void generateFeeds(Book book, Chapter chapter) {
        List<Follow> follows = followRepository.findByBookId(book.getId());
        for (Follow follow : follows) {
            Feed existing = feedRepository.findByUserIdAndBookIdAndDismissedFalse(
                    follow.getUser().getId(), book.getId());
            if (existing != null) {
                existing.setChapter(chapter);
                existing.setCreatedAt(java.time.LocalDateTime.now());
                feedRepository.save(existing);
            } else {
                Feed feed = Feed.builder()
                        .user(follow.getUser())
                        .type(FeedType.CHAPTER_UPDATE)
                        .book(book)
                        .chapter(chapter)
                        .build();
                feedRepository.save(feed);
            }
        }
    }

    public PagedResponse<FeedItemResponse> getFeeds(User user, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Feed> feedPage = feedRepository.findByUserIdAndDismissedFalseOrderByCreatedAtDesc(
                user.getId(), pageable);

        List<FeedItemResponse> items = feedPage.getContent().stream()
                .map(f -> new FeedItemResponse(
                        f.getId(),
                        f.getBook().getId(),
                        f.getBook().getTitle(),
                        f.getBook().getAuthor(),
                        f.getBook().getCoverUrl(),
                        f.getChapter() != null ? f.getChapter().getId() : null,
                        f.getChapter() != null ? f.getChapter().getTitle() : null,
                        f.getCreatedAt()
                ))
                .toList();

        return new PagedResponse<>(
                items,
                feedPage.getNumber(),
                feedPage.getSize(),
                feedPage.getTotalElements(),
                feedPage.getTotalPages(),
                feedPage.isLast()
        );
    }

    public void dismissFeed(User user, Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        if (!feed.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权操作");
        }
        feed.setDismissed(true);
        feedRepository.save(feed);
    }
}
