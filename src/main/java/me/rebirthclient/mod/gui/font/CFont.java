package me.rebirthclient.mod.gui.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CFont {
   protected final CFont.CharData[] charData = new CFont.CharData[256];
   private final float imgSize = 512.0F;
   protected Font font;
   protected boolean antiAlias;
   protected boolean fractionalMetrics;
   protected int fontHeight = -1;
   protected int charOffset;
   protected DynamicTexture tex;

   public CFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
      this.font = font;
      this.antiAlias = antiAlias;
      this.fractionalMetrics = fractionalMetrics;
      this.tex = this.setupTexture(font, antiAlias, fractionalMetrics, this.charData);
   }

   public CFont(CFont.CustomFont font, boolean antiAlias, boolean fractionalMetrics) {
      try {
         Font inputFont = Font.createFont(0, CFont.class.getResourceAsStream(font.getFile())).deriveFont(font.getSize()).deriveFont(font.getType());
         this.font = inputFont;
         this.antiAlias = antiAlias;
         this.fractionalMetrics = fractionalMetrics;
         this.tex = this.setupTexture(inputFont, antiAlias, fractionalMetrics, this.charData);
      } catch (FontFormatException | IOException var5) {
      }
   }

   protected DynamicTexture setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CFont.CharData[] chars) {
      BufferedImage img = this.generateFontImage(font, antiAlias, fractionalMetrics, chars);

      try {
         return new DynamicTexture(img);
      } catch (Exception var7) {
         var7.printStackTrace();
         return null;
      }
   }

   protected BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics, CFont.CharData[] chars) {
      int imgSize = 512;
      BufferedImage bufferedImage = new BufferedImage(imgSize, imgSize, 2);
      Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
      g.setFont(font);
      g.setColor(new Color(255, 255, 255, 0));
      g.fillRect(0, 0, imgSize, imgSize);
      g.setColor(Color.WHITE);
      g.setRenderingHint(
         RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF
      );
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
      FontMetrics fontMetrics = g.getFontMetrics();
      int charHeight = 0;
      int positionX = 0;
      int positionY = 1;

      for(int i = 0; i < chars.length; ++i) {
         char ch = (char)i;
         CFont.CharData charData = new CFont.CharData();
         Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(ch), g);
         charData.width = dimensions.getBounds().width + 8;
         charData.height = dimensions.getBounds().height;
         if (positionX + charData.width >= imgSize) {
            positionX = 0;
            positionY += charHeight;
            charHeight = 0;
         }

         if (charData.height > charHeight) {
            charHeight = charData.height;
         }

         charData.storedX = positionX;
         charData.storedY = positionY;
         if (charData.height > this.fontHeight) {
            this.fontHeight = charData.height;
         }

         chars[i] = charData;
         g.drawString(String.valueOf(ch), positionX + 2, positionY + fontMetrics.getAscent());
         positionX += charData.width;
      }

      return bufferedImage;
   }

   public void drawChar(CFont.CharData[] chars, char c, float x, float y) throws ArrayIndexOutOfBoundsException {
      try {
         this.drawQuad(
            x,
            y,
            (float)chars[c].width,
            (float)chars[c].height,
            (float)chars[c].storedX,
            (float)chars[c].storedY,
            (float)chars[c].width,
            (float)chars[c].height
         );
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   protected void drawQuad(float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {
      float renderSRCX = srcX / 512.0F;
      float renderSRCY = srcY / 512.0F;
      float renderSRCWidth = srcWidth / 512.0F;
      float renderSRCHeight = srcHeight / 512.0F;
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
      GL11.glVertex2d((double)(x + width), (double)y);
      GL11.glTexCoord2f(renderSRCX, renderSRCY);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)x, (double)(y + height));
      GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)x, (double)(y + height));
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)(x + width), (double)(y + height));
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
      GL11.glVertex2d((double)(x + width), (double)y);
   }

   public int getStringHeight(String text) {
      return this.getHeight();
   }

   public int getHeight() {
      return (this.fontHeight - 8) / 2;
   }

   public int getStringWidth(String text) {
      int width = 0;

      for(char c : text.toCharArray()) {
         if (c < this.charData.length) {
            width += this.charData[c].width - 8 + this.charOffset;
         }
      }

      return width / 2;
   }

   public boolean isAntiAlias() {
      return this.antiAlias;
   }

   public void setAntiAlias(boolean antiAlias) {
      if (this.antiAlias != antiAlias) {
         this.antiAlias = antiAlias;
         this.tex = this.setupTexture(this.font, antiAlias, this.fractionalMetrics, this.charData);
      }
   }

   public boolean isFractionalMetrics() {
      return this.fractionalMetrics;
   }

   public void setFractionalMetrics(boolean fractionalMetrics) {
      if (this.fractionalMetrics != fractionalMetrics) {
         this.fractionalMetrics = fractionalMetrics;
         this.tex = this.setupTexture(this.font, this.antiAlias, fractionalMetrics, this.charData);
      }
   }

   public Font getFont() {
      return this.font;
   }

   public void setFont(Font font) {
      this.font = font;
      this.tex = this.setupTexture(font, this.antiAlias, this.fractionalMetrics, this.charData);
   }

   protected static class CharData {
      public int width;
      public int height;
      public int storedX;
      public int storedY;
   }

   public static class CustomFont {
      final float size;
      final String file;
      final int style;

      public CustomFont(String file, float size, int style) {
         this.file = file;
         this.size = size;
         this.style = style;
      }

      public float getSize() {
         return this.size;
      }

      public String getFile() {
         return this.file;
      }

      public int getType() {
         return this.style > 3 ? 3 : Math.max(this.style, 0);
      }
   }
}
