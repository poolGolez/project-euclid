package com.example.projecteuclid.domain

import com.example.projecteuclid.ProjectEuclidApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.stream.Stream

@SpringBootTest(
    classes = arrayOf(ProjectEuclidApplication::class),
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class ExhaustiveSearchStrategyTest {

    @Autowired
    lateinit var bruteForceSearchStrategy: BruteForceSearchStrategy

    @Autowired
    lateinit var kdTreeSearchStrategy: KdTreeSearchStrategy

    @ParameterizedTest
    @MethodSource("closestPoints")
    fun search(target: GeoPosition) {
        val kdResult = kdTreeSearchStrategy.search(target)!!
        val bruteForceResult = bruteForceSearchStrategy.search(target)!!

        val kdResultDistance = kdResult.distanceSquaredFrom(target)
        val bruteForceDistance = bruteForceResult.distanceSquaredFrom(target)
        if (!kdResultDistance.equals(bruteForceResult)) {
            println("(${kdResult.latitude}, ${kdResult.longitude}) : (${bruteForceResult.latitude},${bruteForceResult.longitude})")
        }
        // NOTE: Comparing results as there could be equidistant points
        assertThat(kdResultDistance).isEqualTo(bruteForceDistance)
    }

    companion object {
        @JvmStatic
        fun closestPoints(): Stream<Arguments?> {
            return IntRange(0, 16)
                .map { latitude ->
                    IntRange(0, 16)
                        .map { longitude ->
                            Arguments.of(GeoPosition(BigDecimal(latitude), BigDecimal(longitude)))
                        }
                }
                .flatten<Arguments?>()
                .toList()
                .stream()
        }
    }

}