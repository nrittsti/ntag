package ntag.fx.scene.control;

public enum NTagThemeEnum {
  ModenaLight("ntag.css"), ModenaDark("ntag_dark.css");

  NTagThemeEnum(String css) {
    this.css = css;
  }

  private String css;

  public String getCSS() {
    return css;
  }
}