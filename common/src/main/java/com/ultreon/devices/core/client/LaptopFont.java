package com.ultreon.devices.core.client;

import com.ultreon.devices.Devices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

/**
 * @author MrCrayfish
 */
public class LaptopFont{//} extends Font {
    private boolean debug = false;

//    public LaptopFont(Minecraft mc) {
//        super(res -> new LaptopFontSet(mc.getTextureManager(), Devices.res("laptop")));
//    }

    // Todo: Port to 1.18.2 where possible.
//    @Override
//    public int getCharWidth(char c)
//    {
//        switch(c)
//        {
//            case '\n': return 0;
//            case '\t': return 20;
//        }
//        return super.getCharWidth(c);
//    }
//
//    @Override
//    protected float renderUnicodeChar(char c, boolean italic)
//    {
//        if(debug && (c == '\n' || c == '\t'))
//        {
//            super.renderUnicodeChar(c, italic);
//        }
//        switch(c)
//        {
//            case '\n': return 0f;
//            case '\t': return d+f;
//        }
//        return super.renderUnicodeChar(c, italic);
//    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }
}
