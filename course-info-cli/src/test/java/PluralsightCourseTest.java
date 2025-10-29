import com.pluralsight.courseinfo.cli.service.PluralsightCourse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PluralsightCourseTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            01:08:54.9613330, 68
            00:00:00.0000000, 0
            00:00:59.0000000, 0
            00:01:00.0000000, 1
            02:30:00.0000000, 150
            """)
    void durationInMunite(String duration, long expectedMinutes) {
        PluralsightCourse course = new PluralsightCourse("id", "title", duration, "url", false);
        assertEquals(expectedMinutes, course.durationInMunite());
    }
}
