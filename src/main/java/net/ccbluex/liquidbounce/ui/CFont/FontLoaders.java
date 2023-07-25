//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "D:\����2\MCP 1.8.9 (1)\MCP 1.8.9\mcp918"!

//Decompiled by Procyon!

package net.ccbluex.liquidbounce.ui.CFont;


import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class FontLoaders
{
    public static CFontRenderer SFREGULAR12;
    public static CFontRenderer SFREGULAR14;
    public static CFontRenderer SFREGULAR16;
    public static CFontRenderer SFREGULAR18;
    public static CFontRenderer productsans16;
    public static CFontRenderer productsans18;
    public static CFontRenderer BoldFont12;
    public static CFontRenderer BoldFont8;
    public static CFontRenderer poppins14;
    public static CFontRenderer poppins15;
    public static CFontRenderer poppins16;
    public static CFontRenderer poppins18;
    public static CFontRenderer BoldFont14;
    public static CFontRenderer BoldFont16;
    public static CFontRenderer BoldFont10;
    public static CFontRenderer BoldFont18;
    public static CFontRenderer BoldFont20;
    public static CFontRenderer BoldFont30;
    public static CFontRenderer SFREGULAR25;
    public static CFontRenderer Sans25;
    public static CFontRenderer Sans35;
    public static CFontRenderer ETB20;
    public static CFontRenderer NovIcon20;
    public static CFontRenderer FluxIcon14;
    public static CFontRenderer FluxIcon16;
    public static CFontRenderer FluxIcon18;
    public static CFontRenderer FluxIcon20;
    public static CFontRenderer FluxIcon30;
    public static CFontRenderer FluxIcon40;
    public static CFontRenderer FluxIcon50;
    public static CFontRenderer Icon16;
    public static CFontRenderer JelloTitle20;
    public static CFontRenderer JelloTitle18;
    public static CFontRenderer JelloList16;
    public static CFontRenderer JelloList40;

    public static CFontRenderer NOTIFICATIONS20;
    public static CFontRenderer NOTIFICATIONS18;
    public static CFontRenderer NOTIFICATIONS16;
    public static CFontRenderer NOTIFICATIONS30;
    public static CFontRenderer siyuan20;
    public static CFontRenderer siyuan18;
    public static CFontRenderer siyuan16;
    public static CFontRenderer siyuan30;
    public static CFontRenderer tenacitybold14;
    public static CFontRenderer tenacitybold16;
    public static CFontRenderer tenacitybold18;
    public static CFontRenderer tenacitybold20;
    public static CFontRenderer tenacitybold22;
    public static CFontRenderer tenacity14;
    public static CFontRenderer tenacity16;
    public static CFontRenderer tenacity18;
    public static CFontRenderer tenacity20;
    public static CFontRenderer tenacity22;
    public static CFontRenderer C16;
    public static CFontRenderer  C18;
    public static CFontRenderer C24;
    public static CFontRenderer Chinese18;
    public static CFontRenderer Chinese35;
    public static CFontRenderer JelloMedium_28;
    public static CFontRenderer JelloMedium_22;
    public static CFontRenderer JelloLight_24;
    public static CFontRenderer JelloLight_30;

    public static CFontRenderer Tenatiy_24;
    public static CFontRenderer JelloM20;
    public static CFontRenderer JelloM15;
    public static CFontRenderer NovICON64;
    public static ArrayList<CFontRenderer> fonts;

    public static CFontRenderer Chinese16;

    public static CFontRenderer getFontRender(final int size) {
        return FontLoaders.fonts.get(size - 10);
    }


    private static Font getFont(final String fontName, final int size) {
        try {
            final InputStream inputStream = Files.newInputStream(new File(LiquidBounce.fileManager.fontsDir, fontName + ".ttf").toPath());
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();

            return new Font("default", Font.PLAIN, size);
        }
    }
    private static Font getFont2(String name, int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce+/font/"+name+".ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    
    static {
        FontLoaders.Chinese16 =  new CFontRenderer(FontLoaders.getFont2("Chinese",16),true,true);

        FontLoaders.NovICON64 = new CFontRenderer(getFont2("NovICON",64), true, true);
        FontLoaders.Chinese18 = new CFontRenderer(getFont2("Chinese",18),true,true);
        FontLoaders.Chinese35 = new CFontRenderer(getFont2("Chinese",35),true,true);
        FontLoaders.SFREGULAR12 = new CFontRenderer(getFont2("SFREGULAR", 12), true, true);
        FontLoaders.SFREGULAR14 = new CFontRenderer(getFont2("SFREGULAR", 14), true, true);
        FontLoaders.SFREGULAR16 = new CFontRenderer(getFont2("SFREGULAR", 16), true, true);
        FontLoaders.SFREGULAR18 = new CFontRenderer(getFont2("SFREGULAR", 18), true, true);
        FontLoaders.productsans16 = new CFontRenderer(getFont2("productsans", 16), true, true);
        FontLoaders.productsans18 = new CFontRenderer(getFont2("productsans", 18), true, true);
        FontLoaders.BoldFont12 = new CFontRenderer(getFont2("BoldFont", 14), true, true);
        FontLoaders.BoldFont8 = new CFontRenderer(getFont2("BoldFont", 8), true, true);
        FontLoaders.poppins14 = new CFontRenderer(getFont2("PoppinsRegular", 14), true, true);
        FontLoaders.poppins15 = new CFontRenderer(getFont2("PoppinsRegular", 15), true, true);
        FontLoaders.poppins16 = new CFontRenderer(getFont2("PoppinsRegular", 16), true, true);
        FontLoaders.poppins18 = new CFontRenderer(getFont2("PoppinsRegular", 18), true, true);
        FontLoaders.BoldFont14 = new CFontRenderer(getFont2("BoldFont", 14), true, true);
        FontLoaders.BoldFont16 = new CFontRenderer(getFont2("BoldFont", 16), true, true);
        FontLoaders.BoldFont10 = new CFontRenderer(getFont2("BoldFont", 10), true, true);
        FontLoaders.BoldFont18 = new CFontRenderer(getFont2("BoldFont", 18), true, true);
        FontLoaders.BoldFont20 = new CFontRenderer(getFont2("BoldFont", 20), true, true);
        FontLoaders.BoldFont30 = new CFontRenderer(getFont2("BoldFont", 30), true, true);
        FontLoaders.SFREGULAR25 = new CFontRenderer(getFont2("SFREGULAR", 25), true, true);
        FontLoaders.Sans25 = new CFontRenderer(getFont2("GoogleSans", 25), true, true);
        FontLoaders.Sans35 = new CFontRenderer(getFont2("GoogleSans", 35), true, true);
        FontLoaders.JelloMedium_28 = new CFontRenderer(getFont2("jellomedium", 28), true, true);
        FontLoaders.JelloMedium_22 = new CFontRenderer(getFont2("jellomedium", 22), true, true);
        FontLoaders.JelloLight_24 = new CFontRenderer(getFont2("jellolight", 24), true, true);
        FontLoaders.JelloLight_30 = new CFontRenderer(getFont2("jellolight", 30), true, true);
        FontLoaders.Tenatiy_24 = new CFontRenderer(getFont2("tenacitybold", 24), true, true);
        FontLoaders.ETB20 = new CFontRenderer(getFont2("ETB", 20), true, true);
        FontLoaders.NovIcon20 = new CFontRenderer(getFont2("NovIcon", 20), true, true);
        FontLoaders.FluxIcon14 = new CFontRenderer(getFont2("fluxicon", 16), true, true);
        FontLoaders.FluxIcon16 = new CFontRenderer(getFont2("fluxicon", 16), true, true);
        FontLoaders.FluxIcon18 = new CFontRenderer(getFont2("fluxicon", 18), true, true);
        FontLoaders.FluxIcon20 = new CFontRenderer(getFont2("fluxicon", 25), true, true);
        FontLoaders.FluxIcon30 = new CFontRenderer(getFont2("fluxicon", 41), true, true);
        FontLoaders.FluxIcon40 = new CFontRenderer(getFont2("fluxicon", 40), true, true);
        FontLoaders.FluxIcon50 = new CFontRenderer(getFont2("fluxicon", 50), true, true);
        FontLoaders.Icon16 = new CFontRenderer(getFont2("icon", 16), true, true);
        FontLoaders.JelloTitle20 = new CFontRenderer(getFont2("jellolight", 20), true, true);
        FontLoaders.JelloTitle18 = new CFontRenderer(getFont2("jellolight", 18), true, true);
        FontLoaders.JelloList16 = new CFontRenderer(getFont2("jelloregular", 16), true, true);
        FontLoaders.JelloList40 = new CFontRenderer(getFont2("jelloregular", 21), true, true);
        FontLoaders.C16 = new CFontRenderer(getFont2("misans",16),true,true);
        FontLoaders.C18 = new CFontRenderer(getFont2("misans",18),true,true);
        FontLoaders.C24 = new CFontRenderer(getFont2("misans",24),true,true);
        FontLoaders.NOTIFICATIONS20 = new CFontRenderer(getFont2("NOTIFICATIONS", 20), true, true);
        FontLoaders.NOTIFICATIONS18 = new CFontRenderer(getFont2("NOTIFICATIONS", 18), true, true);
        FontLoaders.NOTIFICATIONS16 = new CFontRenderer(getFont2("NOTIFICATIONS", 16), true, true);
        FontLoaders.NOTIFICATIONS30 = new CFontRenderer(getFont2("NOTIFICATIONS", 30), true, true);
        FontLoaders.siyuan20 = new CFontRenderer(getFont2("siyuan", 20), true, true);
        FontLoaders.siyuan18 = new CFontRenderer(getFont2("siyuan", 18), true, true);
        FontLoaders.siyuan16 = new CFontRenderer(getFont2("siyuan", 16), true, true);
        FontLoaders.siyuan30 = new CFontRenderer(getFont2("siyuan", 30), true, true);
        FontLoaders.tenacitybold14 = new CFontRenderer(getFont2("tenacitybold", 14), true, true);
        FontLoaders.tenacitybold16 = new CFontRenderer(getFont2("tenacitybold", 16), true, true);
        FontLoaders.tenacitybold18 = new CFontRenderer(getFont2("tenacitybold", 18), true, true);
        FontLoaders.tenacitybold20 = new CFontRenderer(getFont2("tenacitybold", 20), true, true);
        FontLoaders.tenacitybold22 = new CFontRenderer(getFont2("tenacitybold", 22), true, true);
        FontLoaders.tenacity14 = new CFontRenderer(getFont2("tenacity", 14), true, true);
        FontLoaders.tenacity16 = new CFontRenderer(getFont2("tenacity", 16), true, true);
        FontLoaders.tenacity18 = new CFontRenderer(getFont2("tenacity", 18), true, true);
        FontLoaders.tenacity20 = new CFontRenderer(getFont2("tenacity", 20), true, true);
        FontLoaders.tenacity22 = new CFontRenderer(getFont2("tenacity", 22), true, true);

        FontLoaders.JelloM20 = new CFontRenderer(getFont2("jellomedium", 20), true, true);
        FontLoaders.JelloM15 = new CFontRenderer(getFont2("jellomedium", 15), true, true);

        FontLoaders.fonts = new ArrayList<CFontRenderer>();
    }
}
