//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.mod.gui.font;

import java.awt.Font;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CustomFont extends CFont {
   protected final CFont.CharData[] boldChars = new CFont.CharData[256];
   protected final CFont.CharData[] italicChars = new CFont.CharData[256];
   protected final CFont.CharData[] boldItalicChars = new CFont.CharData[256];
   private final int[] colorCode = new int[32];
   protected DynamicTexture texBold;
   protected DynamicTexture texItalic;
   protected DynamicTexture texItalicBold;

   public CustomFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
      super(font, antiAlias, fractionalMetrics);
      this.setupMinecraftColorcodes();
      this.setupBoldItalicIDs();
   }

   public CustomFont(CFont.CustomFont font, boolean antiAlias, boolean fractionalMetrics) {
      super(font, antiAlias, fractionalMetrics);
      this.setupMinecraftColorcodes();
      this.setupBoldItalicIDs();
   }

   public float drawStringWithShadow(String text, double x, double y, int color) {
      float shadowWidth = this.drawString(text, x + 1.0, y + 1.0, color, true);
      return Math.max(shadowWidth, this.drawString(text, x, y, color, false));
   }

   public float drawString(String text, float x, float y, int color) {
      return this.drawString(text, (double)x, (double)y, color, false);
   }

   public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
      return this.drawStringWithShadow(text, (double)x - (double)this.getStringWidth(text) / 2.0, (double)y, color);
   }

   public float drawCenteredString(String text, float x, float y, int color) {
      return this.drawString(text, x - (float)this.getStringWidth(text) / 2.0F, y, color);
   }

   public float drawString(String text, double x, double y, int color, boolean shadow) {
      --x;
      y -= 2.0;
      if (text == null) {
         return 0.0F;
      } else {
         if (color == 553648127) {
            color = 16777215;
         }

         if ((color & -67108864) == 0) {
            color |= -16777216;
         }

         if (shadow) {
            color = (color & 16579836) >> 2 | color & 0xFF000000;
         }

         CFont.CharData[] currentData = this.charData;
         float alpha = (float)(color >> 24 & 0xFF) / 255.0F;
         boolean bold = false;
         boolean italic = false;
         boolean strikethrough = false;
         boolean underline = false;
         x *= 2.0;
         y *= 2.0;
         GL11.glPushMatrix();
         GlStateManager.scale(0.5, 0.5, 0.5);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color((float)(color >> 16 & 0xFF) / 255.0F, (float)(color >> 8 & 0xFF) / 255.0F, (float)(color & 0xFF) / 255.0F, alpha);
         int size = text.length();
         GlStateManager.enableTexture2D();
         GlStateManager.bindTexture(this.tex.getGlTextureId());
         GL11.glBindTexture(3553, this.tex.getGlTextureId());

         for(int i = 0; i < size; ++i) {
            char character = text.charAt(i);
            if (character == 167) {
               int colorIndex = 21;

               try {
                  colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
               } catch (Exception var19) {
               }

               if (colorIndex < 16) {
                  bold = false;
                  italic = false;
                  underline = false;
                  strikethrough = false;
                  GlStateManager.bindTexture(this.tex.getGlTextureId());
                  currentData = this.charData;
                  if (colorIndex < 0) {
                     colorIndex = 15;
                  }

                  if (shadow) {
                     colorIndex += 16;
                  }

                  int colorcode = this.colorCode[colorIndex];
                  GlStateManager.color(
                     (float)(colorcode >> 16 & 0xFF) / 255.0F, (float)(colorcode >> 8 & 0xFF) / 255.0F, (float)(colorcode & 0xFF) / 255.0F, alpha
                  );
               } else if (colorIndex == 17) {
                  bold = true;
                  if (italic) {
                     GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                     currentData = this.boldItalicChars;
                  } else {
                     GlStateManager.bindTexture(this.texBold.getGlTextureId());
                     currentData = this.boldChars;
                  }
               } else if (colorIndex == 18) {
                  strikethrough = true;
               } else if (colorIndex == 19) {
                  underline = true;
               } else if (colorIndex == 20) {
                  italic = true;
                  if (bold) {
                     GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                     currentData = this.boldItalicChars;
                  } else {
                     GlStateManager.bindTexture(this.texItalic.getGlTextureId());
                     currentData = this.italicChars;
                  }
               } else if (colorIndex == 21) {
                  bold = false;
                  italic = false;
                  underline = false;
                  strikethrough = false;
                  GlStateManager.color(
                     (float)(color >> 16 & 0xFF) / 255.0F, (float)(color >> 8 & 0xFF) / 255.0F, (float)(color & 0xFF) / 255.0F, alpha
                  );
                  GlStateManager.bindTexture(this.tex.getGlTextureId());
                  currentData = this.charData;
               }

               ++i;
            } else if (character < currentData.length) {
               GL11.glBegin(4);
               this.drawChar(currentData, character, (float)x, (float)y);
               GL11.glEnd();
               if (strikethrough) {
                  this.drawLine(
                     x,
                     y + (double)currentData[character].height / 2.0,
                     x + (double)currentData[character].width - 8.0,
                     y + (double)currentData[character].height / 2.0
                  );
               }

               if (underline) {
                  this.drawLine(
                     x,
                     y + (double)currentData[character].height - 2.0,
                     x + (double)currentData[character].width - 8.0,
                     y + (double)currentData[character].height - 2.0
                  );
               }

               x += (double)(currentData[character].width - 8 + this.charOffset);
            }
         }

         GL11.glHint(3155, 4352);
         GL11.glPopMatrix();
         return (float)x / 2.0F;
      }
   }

   @Override
   public int getStringWidth(String text) {
      if (text == null) {
         return 0;
      } else {
         int width = 0;
         CFont.CharData[] currentData = this.charData;
         int size = text.length();

         for(int i = 0; i < size; ++i) {
            char character = text.charAt(i);
            if (character == 167) {
               ++i;
            } else if (character < currentData.length) {
               width += currentData[character].width - 8 + this.charOffset;
            }
         }

         return width / 2;
      }
   }

   @Override
   public void setFont(Font font) {
      super.setFont(font);
      this.setupBoldItalicIDs();
   }

   @Override
   public void setAntiAlias(boolean antiAlias) {
      super.setAntiAlias(antiAlias);
      this.setupBoldItalicIDs();
   }

   @Override
   public void setFractionalMetrics(boolean fractionalMetrics) {
      super.setFractionalMetrics(fractionalMetrics);
      this.setupBoldItalicIDs();
   }

   private void setupBoldItalicIDs() {
      this.texBold = this.setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
      this.texItalic = this.setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
      this.texItalicBold = this.setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
   }

   private void drawLine(double x, double y, double x1, double y1) {
      GL11.glDisable(3553);
      GL11.glLineWidth(1.0F);
      GL11.glBegin(1);
      GL11.glVertex2d(x, y);
      GL11.glVertex2d(x1, y1);
      GL11.glEnd();
      GL11.glEnable(3553);
   }

   private void setupMinecraftColorcodes() {
      for(int index = 0; index < 32; ++index) {
         int noClue = (index >> 3 & 1) * 85;
         int red = (index >> 2 & 1) * 170 + noClue;
         int green = (index >> 1 & 1) * 170 + noClue;
         int blue = (index & 1) * 170 + noClue;
         if (index == 6) {
            red += 85;
         }

         if (index >= 16) {
            red /= 4;
            green /= 4;
            blue /= 4;
         }

         this.colorCode[index] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
      }
   }
}
