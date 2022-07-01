package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.yandex.practicum.filmorate.TestUtil.*;


@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReviewControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ReviewService reviewService;


    @Test
    public void shouldCheckValidate() {
        this.restTemplate.postForObject("http://localhost:" + port + "/reviews", invalidReview1, String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/reviews", invalidReview2, String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/reviews", invalidReview3, String.class);

        final List<Review> reviews = this.restTemplate.getForObject(("http://localhost:" + port + "/reviews"),
                ArrayList.class);
        assertNotNull(reviews, "Отзывы на возвращаются.");
        assertEquals(3, reviews.size(), "Неверное количество отзывов.");
    }

    @Test
    public void shouldAddAndDeleteLikeOrDislike() {
        reviewService.likeOrDislike(1, 2, true);
        reviewService.likeOrDislike(1, 1, true);
        reviewService.likeOrDislike(1, 3, false);
        reviewService.likeOrDislike(3, 2, false);
        Review reviewFromStorage1 = reviewService.findById(1);
        Review reviewFromStorage2 = reviewService.findById(2);
        Review reviewFromStorage3 = reviewService.findById(3);

        assertEquals(1, reviewFromStorage1.getUseful(), "Неверное количество лайков.");
        assertEquals(0, reviewFromStorage2.getUseful(), "Неверное количество лайков.");
        assertEquals(-1, reviewFromStorage3.getUseful(), "Неверное количество лайков.");

        reviewService.deleteLikeOrDislike(3, 2, false);
        reviewService.deleteLikeOrDislike(1, 1, true);
        Review reviewFromStorage4 = reviewService.findById(3);
        Review reviewFromStorage5 = reviewService.findById(1);
        assertEquals(0, reviewFromStorage4.getUseful(), "Неверное количество лайков, " +
                "после удаления дизлайка.");
        assertEquals(0, reviewFromStorage5.getUseful(), "Неверное количество лайков, " +
                "после удаления лайка.");
    }

    @Test
    public void shouldGetReviewsByFilmsOrExactQuantity() {
        final Collection<Review> reviewsByFilm = reviewService.findAllByIdFilm(1L, 10);
        final Collection<Review> reviews1 = reviewService.findAllByIdFilm(null, 1);

        assertNotNull(reviewsByFilm, "Отзывы на возвращаются.");
        assertNotNull(reviews1, "Отзыв нe возвращается.");
        assertEquals(2, reviewsByFilm.size(), "Неверное количество отзывовов у фильма.");
        assertEquals(1, reviews1.size(), "Вернулось не то количество отзывово, что запрашивал " +
                "пользователь.");
        //delete review
        reviewService.delete(1L);
        final Collection<Review> reviewsByFilm1 = reviewService.findAllByIdFilm(1L, 10);

        assertEquals(1, reviewsByFilm1.size(), "Неверное количество отзывовов у фильма.");
    }

}


