package ntag.commons;

import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.Unit)
class ComparableListTest {

  ComparableList<String> comparableList;

  @BeforeEach
  protected void setUp() {
    comparableList = new ComparableList<>();
    comparableList.add("a");
    comparableList.add("b");
    comparableList.add("c");
  }

  @Test
  @DisplayName("both list are equals")
  void compareTo1() {
    // given
    ComparableList<String> given = new ComparableList<>(List.of("a", "b", "c"));
    int expected = 0;
    // when
    int actual = comparableList.compareTo(given);
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @DisplayName("right list is smaller")
  void compareTo2() {
    // given
    ComparableList<String> given = new ComparableList<>(List.of("a", "b"));
    int expected = 1;
    // when
    int actual = comparableList.compareTo(given);
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @DisplayName("right list is larger")
  void compareTo3() {
    // given
    ComparableList<String> given = new ComparableList<>(List.of("a", "b", "c", "d"));
    int expected = -1;
    // when
    int actual = comparableList.compareTo(given);
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @DisplayName("different list elements")
  void compareTo4() {
    // given
    ComparableList<String> given = new ComparableList<>(List.of("a", "y", "c"));
    int unexpected = 0;
    // when
    int actual = comparableList.compareTo(given);
    // then
    assertThat(actual).isNotEqualTo(unexpected);
  }
}
