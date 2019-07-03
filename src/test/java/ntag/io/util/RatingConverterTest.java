package ntag.io.util;

import ntag.Category;
import ntag.model.AudioFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag(Category.Unit)
class RatingConverterTest {

    @BeforeAll
    public static void setUpClass() {
        Integer[] convertionList = {13, 23, 54, 64, 118, 128, 186, 196, 242, 255};
        RatingConverter.setConversion(AudioFormat.MP3, Arrays.asList(convertionList));
    }

    @ParameterizedTest
    @CsvSource({"0,0", "100,4", "200,8", "255,10", "256, 10"})
    public void internalToHalfStars(int input, int expected) {
        // given
        // when
        int actual = RatingConverter.internalToHalfStars(AudioFormat.MP3, input);
        // then
        assertEquals(actual, expected);
    }

    @ParameterizedTest
    @CsvSource({"0,0", "4,64", "8,196", "10,255", "11,255"})
    public void halfStarsToInternal(int input, int expected) {
        // given
        // when
        int actual = RatingConverter.halfStarsToInternal(AudioFormat.MP3, input);
        // then
        assertEquals(actual, expected);
    }

    @Test()
    public void setConversionWithIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RatingConverter.setConversion(null, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RatingConverter.setConversion(AudioFormat.MP3, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RatingConverter.setConversion(AudioFormat.MP3, new ArrayList<>()));
    }
}