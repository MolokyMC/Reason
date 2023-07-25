package net.ccbluex.liquidbounce.ui.CFont.font;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

/* loaded from: LiquidBounce-b73.jar:net/ccbluex/liquidbounce/FontLoaders.class */
public class FontLoaders {
    public static CFontRenderer F14 = new CFontRenderer(getFont2(14), true, true);
    public static CFontRenderer F16 = new CFontRenderer(getFont2(16), true, true);
    public static CFontRenderer F18 = new CFontRenderer(getFont2(18), true, true);
    public static CFontRenderer F20 = new CFontRenderer(getFont2(20), true, true);
    public static CFontRenderer F22 = new CFontRenderer(getFont2(22), true, true);
    public static CFontRenderer F23 = new CFontRenderer(getFont2(23), true, true);
    public static CFontRenderer F24 = new CFontRenderer(getFont2(24), true, true);
    public static CFontRenderer F30 = new CFontRenderer(getFont2(30), true, true);
    public static CFontRenderer F40 = new CFontRenderer(getFont2(40), true, true);
    public static CFontRenderer F50 = new CFontRenderer(getFont2(50), true, true);
    public static CFontRenderer C12 = new CFontRenderer(getFont2(12), true, true);
    public static CFontRenderer C14 = new CFontRenderer(getFont2(14), true, true);
    public static CFontRenderer C16 = new CFontRenderer(getFont2(16), true, true);
    public static CFontRenderer C18 = new CFontRenderer(getFont2(18), true, true);
    public static CFontRenderer C20 = new CFontRenderer(getFont2(20), true, true);
    public static CFontRenderer C22 = new CFontRenderer(getFont2(22), true, true);
    public static CFontRenderer Logo = new CFontRenderer(getFont2(40), true, true);
    public static ArrayList<CFontRenderer> fonts = new ArrayList<>();

    public static CFontRenderer getFontRender(int size) {
        return fonts.get(size - 10);
    }

    private static Font getFont( final int size) {
        try {
            final InputStream inputStream = Files.newInputStream(new File(LiquidBounce.fileManager.fontsDir, "misans.ttf").toPath());
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();

            return new Font("default", Font.PLAIN, size);
        }
    }
    private static Font getFont2( int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce+/font/misans.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

}
