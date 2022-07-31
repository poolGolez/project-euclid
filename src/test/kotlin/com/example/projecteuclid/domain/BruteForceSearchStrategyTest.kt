package com.example.projecteuclid.domain

import com.example.projecteuclid.ProjectEuclidApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Stream

@SpringBootTest(
    classes = arrayOf(ProjectEuclidApplication::class),
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class BruteForceSearchStrategyTest {

    @Autowired
    lateinit var strategy: BruteForceSearchStrategy

    @ParameterizedTest
    @MethodSource("closestPoints")
    fun search(target: GeoPosition, expected: GeoPosition) {
        val actual = strategy.search(target)!!

        assertThat(actual.id).isEqualTo(expected.id)
        assertThat(actual.latitude).isEqualTo(expected.latitude)
        assertThat(actual.longitude).isEqualTo(expected.longitude)
    }

    companion object {
        @JvmStatic
        fun closestPoints(): Stream<Arguments?>? {
            return Stream.of(
                Arguments.of(GeoPosition(0.0, 0.0), GeoPosition(2L, 0.0, 8.0)),
                Arguments.of(GeoPosition(14.0, 14.0), GeoPosition(3L, 16.0, 8.0)),
                Arguments.of(GeoPosition(9.0, 7.0), GeoPosition(7L, 7.0, 9.0)),
                Arguments.of(GeoPosition(0.0, 180.0), GeoPosition(4L, 4.0, 16.0)),
                Arguments.of(GeoPosition(90.0, 0.0), GeoPosition(3L, 16.0, 8.0)),
                Arguments.of(GeoPosition(12.0, 4.0), GeoPosition(5L, 12.0, 4.0)),
            )
        }
    }
}