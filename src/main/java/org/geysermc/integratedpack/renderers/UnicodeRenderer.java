package org.geysermc.integratedpack.renderers;

import org.geysermc.integratedpack.ImageUtil;
import org.geysermc.integratedpack.JavaResources;
import org.geysermc.integratedpack.MathUtils;
import org.geysermc.integratedpack.renderers.unicode.UnicodeCharacterRenderer;
import org.reflections.Reflections;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UnicodeRenderer implements Renderer {
    private static final UnicodeCharacterRenderer[] RENDERERS;

    static {
        Reflections reflections = new Reflections("org.geysermc.integratedpack.renderers.unicode");
        Set<Class<? extends UnicodeCharacterRenderer>> renderers = reflections.getSubTypesOf(UnicodeCharacterRenderer.class);

        RENDERERS = renderers.stream().map(rendererClass -> {
            try {
                return rendererClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).sorted().toArray(UnicodeCharacterRenderer[]::new);
    }

    @Override
    public String getName() {
        return "Unicode";
    }

    @Override
    public String getDestination() {
        return "font/glyph_EF.png";
    }

    @Override
    public void render() throws IOException {
        List<BufferedImage> images = new ArrayList<>();

        for (UnicodeCharacterRenderer renderer : RENDERERS) {
            log("- Rendering unicodes for " + renderer.getName() + "...");
            for (BufferedImage image : renderer.getUnicodeImages()) {
                if (image.getHeight() != image.getWidth()) {
                    log("Unicode from %s has a mismatching width and height, width must equal the height. Skipping character.");
                    continue;
                }

                images.add(image);
            }
        }

        if (images.size() > 256) throw new IllegalStateException("Too many unicodes registered! Max 256, but got %d.".formatted(images.size()));

        // Prevents some whacky scaling issues, does mean big image sizes tho :(
        int charSize = MathUtils.lcm(images.stream().mapToInt(BufferedImage::getWidth).toArray());

        BufferedImage fontImage = new BufferedImage(charSize * 16, charSize * 16, BufferedImage.TYPE_INT_ARGB);
        Graphics g = fontImage.getGraphics();

        int x = 0;
        int y = 0;

        for (BufferedImage image : images) {
            if (image.getWidth() != charSize) {
                float scale = (float) charSize / image.getWidth();
                image = ImageUtil.scale(image, scale);
            }

            g.drawImage(image, x * charSize, y * charSize, null);

            x++;

            if (x == 16) {
                y++;
                x = 0;
            }
        }

        ImageIO.write(fontImage, "PNG", getDestinationPath().toFile());
    }
}
