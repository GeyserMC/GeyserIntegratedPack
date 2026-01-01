package org.geysermc.integratedpack.renderers.unicode;

import org.geysermc.integratedpack.ImageUtil;
import org.geysermc.integratedpack.JavaResources;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AttackIndicatorCharacterRenderer implements UnicodeCharacterRenderer {
    private static final String CROSSHAIR_ATTACK_INDICATOR_EMPTY = "assets/minecraft/textures/gui/sprites/hud/crosshair_attack_indicator_background.png";
    private static final String CROSSHAIR_ATTACK_INDICATOR_PROGRESS = "assets/minecraft/textures/gui/sprites/hud/crosshair_attack_indicator_progress.png";
    private static final String CROSSHAIR_ATTACK_INDICATOR_FULL = "assets/minecraft/textures/gui/sprites/hud/crosshair_attack_indicator_full.png";

    private static final String HOTBAR_ATTACK_INDICATOR_EMPTY = "assets/minecraft/textures/gui/sprites/hud/hotbar_attack_indicator_background.png";
    private static final String HOTBAR_ATTACK_INDICATOR_PROGRESS = "assets/minecraft/textures/gui/sprites/hud/hotbar_attack_indicator_progress.png";

    @Override
    public String getName() {
        return "Attack Indicator";
    }

    @Override
    public List<BufferedImage> getUnicodeImages() throws IOException {
        List<BufferedImage> images = new ArrayList<>();

        // Crosshair
        {
            BufferedImage emptyAttackIndicator = JavaResources.getAsImage(CROSSHAIR_ATTACK_INDICATOR_EMPTY);
            BufferedImage progressAttackIndicator = JavaResources.getAsImage(CROSSHAIR_ATTACK_INDICATOR_PROGRESS);

            for (int i = 0; i < progressAttackIndicator.getWidth() + 1; i++) {
                BufferedImage canvas = new BufferedImage(emptyAttackIndicator.getWidth(), emptyAttackIndicator.getWidth(), BufferedImage.TYPE_INT_ARGB); // Ensures square
                Graphics g = canvas.getGraphics();

                // Do not center, keeps in line with ATTACK_INDICATOR_FULL which puts it at the top
                g.drawImage(emptyAttackIndicator, 0, 0, null);
                if (i != 0) { // Errors when i = 0
                    g.drawImage(ImageUtil.crop(progressAttackIndicator, i, progressAttackIndicator.getHeight()), 0, 0, null);
                }
                images.add(canvas);
            }

            images.add(JavaResources.getAsImage(CROSSHAIR_ATTACK_INDICATOR_FULL));
        }

        // Hotbar
        {
            BufferedImage emptyAttackIndicator = JavaResources.getAsImage(HOTBAR_ATTACK_INDICATOR_EMPTY);
            BufferedImage progressAttackIndicator = JavaResources.getAsImage(HOTBAR_ATTACK_INDICATOR_PROGRESS);

            for (int i = progressAttackIndicator.getHeight(); i > 0; i--) {
                BufferedImage canvas = new BufferedImage(emptyAttackIndicator.getWidth(), emptyAttackIndicator.getHeight(), BufferedImage.TYPE_INT_ARGB); // Should already be square
                Graphics g = canvas.getGraphics();

                g.drawImage(emptyAttackIndicator, 0, 0, null);
                if (i != progressAttackIndicator.getHeight()) {
                    g.drawImage(ImageUtil.crop(
                            progressAttackIndicator, 0, i,
                            progressAttackIndicator.getWidth(), progressAttackIndicator.getHeight() - i
                    ), 0, i, null);
                }
                images.add(canvas);
            }
        }

        return images;
    }

    @Override
    public int order() {
        return 0;
    }
}
