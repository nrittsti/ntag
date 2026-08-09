/*
 *   This file is part of NTag (audio file tag editor).
 *
 *   NTag is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   NTag is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Copyright 2026, Nico Rittstieg
 *
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Dependency-free generator for the NTag application icon.
 *
 * <p>Design: modern rounded-square tile with a blue vertical gradient
 * (#3FA9F5 -&gt; #0B62A6) and a white price-tag silhouette holding a carved
 * eighth-note. The note and the string-hole show the tile gradient through
 * the tag, creating a cut-out effect.
 *
 * <p>Usage (from the repository root):
 * <pre>
 *   javac -d tools/icon/build tools/icon/IconGenerator.java
 *   java -cp tools/icon/build IconGenerator tools/icon/ntag.iconset
 *   iconutil -c icns tools/icon/ntag.iconset -o etc/NTag.icns
 * </pre>
 */
public final class IconGenerator {

  private static final int MASTER = 1024;

  private static final Color GRADIENT_TOP = new Color(0x3FA9F5);
  private static final Color GRADIENT_BOTTOM = new Color(0x0B62A6);

  private IconGenerator() {
  }

  public static void main(String[] args) throws Exception {
    Path iconsetDir = args.length > 0 ? Path.of(args[0]) : Path.of("tools/icon/ntag.iconset");
    Path repoRoot = Path.of("").toAbsolutePath();

    BufferedImage master = renderMaster();

    writePng(master, repoRoot.resolve("tools/icon/build/ntag-1024.png"));

    // JavaFX window icon + Linux desktop icon (512)
    writePng(scale(master, 512), repoRoot.resolve("src/main/resources/icons/ntag.png"));
    writePng(scale(master, 512), repoRoot.resolve("etc/ntag.png"));

    // Windows multi-size ICO (16,24,32,48,64,128,256)
    writeIco(master, repoRoot.resolve("etc/ntag.ico"), new int[]{16, 24, 32, 48, 64, 128, 256});

    // macOS iconset (10 files, consumed by iconutil)
    Files.createDirectories(iconsetDir);
    writePng(scale(master, 16), iconsetDir.resolve("icon_16x16.png"));
    writePng(scale(master, 32), iconsetDir.resolve("icon_16x16@2x.png"));
    writePng(scale(master, 32), iconsetDir.resolve("icon_32x32.png"));
    writePng(scale(master, 64), iconsetDir.resolve("icon_32x32@2x.png"));
    writePng(scale(master, 128), iconsetDir.resolve("icon_128x128.png"));
    writePng(scale(master, 256), iconsetDir.resolve("icon_128x128@2x.png"));
    writePng(scale(master, 256), iconsetDir.resolve("icon_256x256.png"));
    writePng(scale(master, 512), iconsetDir.resolve("icon_256x256@2x.png"));
    writePng(scale(master, 512), iconsetDir.resolve("icon_512x512.png"));
    writePng(scale(master, MASTER), iconsetDir.resolve("icon_512x512@2x.png"));

    // SVG vector source
    Files.writeString(repoRoot.resolve("etc/ntag.svg"), renderSvg());

    System.out.println("Generated PNG/ICO/SVG. Now run:");
    System.out.println("  iconutil -c icns tools/icon/ntag.iconset -o etc/NTag.icns");
  }

  private static BufferedImage renderMaster() {
    BufferedImage image = new BufferedImage(MASTER, MASTER, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    try {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

      RoundRectangle2D tile = new RoundRectangle2D.Double(48, 48, 928, 928, 190, 190);
      g.setPaint(new LinearGradientPaint(0, 48, 0, 976,
          new float[]{0f, 1f},
          new Color[]{GRADIENT_TOP, GRADIENT_BOTTOM}));
      g.fill(tile);

      g.clip(tile);

      // soft top-left shine
      g.setPaint(new LinearGradientPaint(0, 48, 0, 340,
          new float[]{0f, 1f},
          new Color[]{new Color(255, 255, 255, 90), new Color(255, 255, 255, 0)}));
      g.fill(new RoundRectangle2D.Double(48, 48, 928, 340, 190, 190));

      // soft bottom inner shadow
      g.setPaint(new LinearGradientPaint(0, 820, 0, 976,
          new float[]{0f, 1f},
          new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 70)}));
      g.fill(new RoundRectangle2D.Double(48, 820, 928, 200, 190, 190));

      // white price tag (body + right tip)
      g.setPaint(Color.WHITE);
      g.fill(new RoundRectangle2D.Double(262, 302, 500, 420, 70, 70));
      Path2D tip = new Path2D.Double();
      tip.moveTo(762, 402);
      tip.lineTo(932, 512);
      tip.lineTo(762, 622);
      tip.closePath();
      g.fill(tip);

      // string hole - carved with the tile gradient
      g.setPaint(new LinearGradientPaint(0, 48, 0, 976,
          new float[]{0f, 1f},
          new Color[]{GRADIENT_TOP, GRADIENT_BOTTOM}));
      g.fill(new Ellipse2D.Double(312, 367, 104, 104));

      // eighth-note - carved with the tile gradient
      Path2D stem = new Path2D.Double();
      stem.moveTo(490, 377);
      stem.lineTo(534, 377);
      stem.lineTo(534, 677);
      stem.lineTo(490, 677);
      stem.closePath();
      g.fill(stem);

      Path2D flag = new Path2D.Double();
      flag.moveTo(512, 389);
      flag.quadTo(672, 367, 667, 537);
      flag.quadTo(612, 577, 597, 517);
      flag.quadTo(572, 457, 512, 389);
      flag.closePath();
      g.fill(flag);

      Graphics2D g2 = (Graphics2D) g.create();
      try {
        g2.rotate(Math.toRadians(-20), 432, 642);
        g2.fill(new Ellipse2D.Double(357, 587, 150, 110));
      } finally {
        g2.dispose();
      }
    } finally {
      g.dispose();
    }
    return image;
  }

  private static BufferedImage scale(BufferedImage source, int size) {
    BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = scaled.createGraphics();
    try {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.drawImage(source, 0, 0, size, size, null);
    } finally {
      g.dispose();
    }
    return scaled;
  }

  private static void writePng(BufferedImage image, Path path) throws IOException {
    Files.createDirectories(path.getParent());
    if (!ImageIO.write(image, "png", path.toFile())) {
      throw new IOException("No PNG writer for " + path);
    }
    System.out.println("Wrote " + path);
  }

  /**
   * Writes a Vista-compatible multi-size ICO (each entry is an embedded PNG).
   */
  private static void writeIco(BufferedImage master, Path path, int[] sizes) throws IOException {
    Map<Integer, byte[]> blobs = new LinkedHashMap<>();
    for (int size : sizes) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImageIO.write(scale(master, size), "png", out);
      blobs.put(size, out.toByteArray());
    }

    ByteArrayOutputStream ico = new ByteArrayOutputStream();
    ico.write(0);
    ico.write(0);
    ico.write(1);
    ico.write(0);
    int count = blobs.size();
    ico.write(count & 0xFF);
    ico.write((count >> 8) & 0xFF);

    int offset = 6 + 16 * count;
    for (byte[] blob : blobs.values()) {
      // width / height byte is 0 for 256
      ico.write(0);
      ico.write(0);
      ico.write(0);
      ico.write(0);
      writeLE16(ico, 1); // color planes
      writeLE16(ico, 32); // bits per pixel
      writeLE32(ico, blob.length);
      writeLE32(ico, offset);
      offset += blob.length;
    }
    for (byte[] blob : blobs.values()) {
      ico.write(blob, 0, blob.length);
    }

    Files.createDirectories(path.getParent());
    Files.write(path, ico.toByteArray());
    System.out.println("Wrote " + path);
  }

  private static void writeLE16(ByteArrayOutputStream out, int value) {
    out.write(value & 0xFF);
    out.write((value >> 8) & 0xFF);
  }

  private static void writeLE32(ByteArrayOutputStream out, int value) {
    out.write(value & 0xFF);
    out.write((value >> 8) & 0xFF);
    out.write((value >> 16) & 0xFF);
    out.write((value >> 24) & 0xFF);
  }

  private static String renderSvg() {
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <svg xmlns="http://www.w3.org/2000/svg" width="1024" height="1024" viewBox="0 0 1024 1024">
          <defs>
            <linearGradient id="tile" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0" stop-color="#3FA9F5"/>
              <stop offset="1" stop-color="#0B62A6"/>
            </linearGradient>
          </defs>
          <rect x="48" y="48" width="928" height="928" rx="190" fill="url(#tile)"/>
          <g fill="#FFFFFF">
            <rect x="262" y="302" width="500" height="420" rx="70"/>
            <path d="M762 402 L932 512 L762 622 Z"/>
          </g>
          <g fill="url(#tile)">
            <circle cx="364" cy="419" r="52"/>
            <rect x="490" y="377" width="44" height="300"/>
            <path d="M512 389 Q672 367 667 537 Q612 577 597 517 Q572 457 512 389 Z"/>
            <ellipse cx="432" cy="642" rx="75" ry="55" transform="rotate(-20 432 642)"/>
          </g>
        </svg>
        """;
  }
}
