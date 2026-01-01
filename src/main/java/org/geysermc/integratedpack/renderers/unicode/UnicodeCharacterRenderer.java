package org.geysermc.integratedpack.renderers.unicode;

import org.geysermc.integratedpack.IntegratedPack;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public interface UnicodeCharacterRenderer extends Comparable<UnicodeCharacterRenderer> {
    /**
     * Gets the name of the unicode renderer for logging in the console.
     *
     * @return The name of the unicode renderer.
     */
    String getName();

    /**
     * Returns a list of images to add to the unicode sheet
     *
     * @throws IOException If an error occurs during rendering.
     */
    List<BufferedImage> getUnicodeImages() throws IOException;

    /**
     * Gets the order position of this renderer, 0 being the first one to process.
     *
     * @return The order position of this renderer.
     */
    int order();

    @Override
    default int compareTo(UnicodeCharacterRenderer other) {
        return Integer.compare(this.order(), other.order());
    }

    default void log(String message) {
        IntegratedPack.log(message);
    }
}
