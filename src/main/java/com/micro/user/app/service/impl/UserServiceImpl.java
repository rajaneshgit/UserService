package com.micro.user.app.service.impl;

import com.micro.user.app.exception.ResourceNotFoundException;
import com.micro.user.app.external.services.HotelService;
import com.micro.user.app.external.services.RatingService;
import com.micro.user.app.model.Rating;
import com.micro.user.app.model.User;
import com.micro.user.app.repository.UserRepository;
import com.micro.user.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;
    @Autowired
    private RatingService ratingService;

    private Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        List<User> users = userRepository.findAll();
        users = users.stream().map(user -> {
            getRatingsWithHotels(user);
            return user;
        }).collect(Collectors.toList());
        return users;
    }

    @Override
    public User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found by given id in server : " + userId));
        getRatingsWithHotels(user);
        return user;
    }

    private void getRatingsWithHotels(User user) {
        List<Rating> ratings = ratingService.getRatings(user.getUserId());
        ratings = ratings.stream().map(rating -> {
            rating.setHotel(hotelService.getHotel(rating.getHotelId()));
            return rating;
        }).collect(Collectors.toList());
        user.setRatings(ratings);
    }
}
