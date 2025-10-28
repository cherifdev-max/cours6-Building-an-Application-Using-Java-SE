package com.pluralsight.courseinfo.cli;

import com.pluralsight.courseinfo.cli.service.CourseReetrievalService;
import com.pluralsight.courseinfo.cli.service.PluralsightCourse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;

public class CourseReetriever {
    private static final Logger LOG = LoggerFactory.getLogger(CourseReetriever.class);

    public static void main(String... args){
       LOG.info("Bienvenue dans le cours Pluralsight!");

        if(args.length == 0){
            LOG.warn("donne moi le nom de l auteure svp");
            return;
        }
        try {
            retrieveCourses(args[0]);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private static void retrieveCourses(String authorId) {
        LOG.info("Voici les cours de l'auteur '{}'" , authorId);
        CourseReetrievalService courseReetrievalService =   new CourseReetrievalService();

        List<PluralsightCourse> coursesToStore =courseReetrievalService.getCoursesFor(authorId)
                .stream()
                .filter(Predicate.not(PluralsightCourse::isRetired))
                .toList();
        LOG.info("voici le cours de cette auteur {} duration {} courses {} ", coursesToStore.size(), coursesToStore.getFirst().duration(), coursesToStore );
    }

}
