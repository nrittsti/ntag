package ntag.io.util;

import ntag.Category;
import ntag.model.AudioFormat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag(Category.Unit)
class RatingConverterTest {

    @BeforeAll
    public static void setUpClass() {
        Integer[] convertionList = {13, 23, 54, 64, 118, 128, 186, 196, 242, 255};
        RatingConverter.setConversion(AudioFormat.MP3, List.of(convertionList));
    }

    @ParameterizedTest
    @CsvSource({"0,0", "100,4", "200,8", "255,10", "256, 10"})
    public void internalToHalfStars(int input, int expected) {
        // given
        // when
        int actual = RatingConverter.internalToHalfStars(AudioFormat.MP3, input);
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"0,0", "4,64", "8,196", "10,255", "11,255"})
    public void halfStarsToInternal(int input, int expected) {
        // given
        // when
        int actual = RatingConverter.halfStarsToInternal(AudioFormat.MP3, input);
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test()
    public void setConversionWithIllegalArgumentException() {
        assertThatThrownBy(() -> RatingConverter.setConversion(null, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> RatingConverter.setConversion(AudioFormat.MP3, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> RatingConverter.setConversion(AudioFormat.MP3, List.of())).isInstanceOf(IllegalArgumentException.class);
    }
}
