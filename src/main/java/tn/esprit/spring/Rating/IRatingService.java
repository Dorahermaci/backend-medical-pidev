package tn.esprit.spring.Rating;

public interface IRatingService {


    Rating createRating(Rating rating, Long courseId);

    void calculateAndSetAverageRating(Long courseId);



    boolean hasBadWord(String comment);
}
