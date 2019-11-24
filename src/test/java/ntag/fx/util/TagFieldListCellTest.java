package ntag.fx.util;

import javafx.embed.swing.JFXPanel;
import ntag.Category;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.Unit)
class TagFieldListCellTest {

  @BeforeAll
  static void initPlatform() {
    new JFXPanel();
  }

  @Test
  void updateItem() {
    // given
    ID3v23Frame tagField = new ID3v23Frame("TYER");
    // when
    TagFieldListCell tagFieldListCell = new TagFieldListCell();
    tagFieldListCell.updateItem(tagField, false);
    String result = tagFieldListCell.getText();
    // then
    assertThat(result).isEqualTo("TYER (Year)");
  }

  @Test
  void createDescription() {
    // given
    ID3v23Frame tagField = new ID3v23Frame("TYER");
    // when
    String result = TagFieldListCell.createDescription(tagField);
    // then
    assertThat(result).isEqualTo("TYER (Year)");
  }
}