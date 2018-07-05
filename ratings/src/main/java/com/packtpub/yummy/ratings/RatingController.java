package com.packtpub.yummy.ratings;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/votes/{type}/{entityId}")
@AllArgsConstructor
@Slf4j
public class RatingController {
    private RatingRepository ratingRepository;

    @PostMapping
    public void addRating(@RequestBody @Valid RatingVote ratingVote,
                          @PathVariable String type,
                          @PathVariable String entityId) {
        log.info("Got rating {} for {} with entity {}", ratingVote, type, entityId);
        ratingRepository.persist(type, entityId, ratingVote.getRating());
    }

    @GetMapping
    public RatingResult getRating(@PathVariable String type,
                                  @PathVariable String entityId) {
        return ratingRepository.calculateRating(type, entityId);
    }
}
