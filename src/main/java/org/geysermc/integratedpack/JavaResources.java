/*
 * Copyright (c) 2025 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/GeyserIntegratedPack
 */

package org.geysermc.integratedpack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.zip.ZipFile;

public class JavaResources {
    private static ZipFile CLIENT_JAR;

    /**
     * This function copies the files from the jar to the pack and initializes the class for getting resources when needed in renderers.
     *
     * @param clientJar The java client jar
     */
    public static void extract(ZipFile clientJar) {
        CLIENT_JAR = clientJar;

        try {
            // Get the assets we need
            JsonObject requiredAssets = Resources.getAsJson("required_assets.json").getAsJsonObject();

            // Get the files we need to copy from the jar to the pack.
            JsonObject requiredJarFiles = requiredAssets.getAsJsonObject("files");
            for (Map.Entry<String, JsonElement> entry : requiredJarFiles.entrySet()) {
                String jarAssetPath = entry.getKey();
                String destinationPath = entry.getValue().getAsString();
                InputStream asset = getAsStream(jarAssetPath);

                IntegratedPack.log("Copying " + jarAssetPath + " to " + destinationPath + "...");

                String assetFileName = Path.of(jarAssetPath).toFile().getName();
                Path destination = IntegratedPack.WORKING_PATH.resolve(destinationPath).resolve(assetFileName);

                File destinationFolder = IntegratedPack.WORKING_PATH.resolve(destinationPath).toFile();
                if (!destinationFolder.exists()) {
                    if (!destinationFolder.mkdirs()) {
                        IntegratedPack.log("Could not make directories for copying " + jarAssetPath + " to " + destinationPath + "!");
                        continue;
                    }
                }

                Files.copy(asset, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            // Get other assets which are not included in the jar file
            JsonObject requiredRemoteAssets = requiredAssets.getAsJsonObject("assets");
            for (Map.Entry<String, JsonElement> entry : requiredRemoteAssets.entrySet()) {
                String remoteAssetPath = entry.getKey();
                String destinationPath = entry.getValue().getAsString();

                LauncherMetaWrapper.Asset asset = LauncherMetaWrapper.ASSETS.objects().get(remoteAssetPath);
                if (asset == null) {
                    IntegratedPack.log("WARNING: Unable to find %s in the asset index.".formatted(remoteAssetPath));
                    continue;
                }

                String bytes = asset.hash().substring(0, 2);

                InputStream assetStream = WebUtils.request("https://resources.download.minecraft.net/%s/%s"
                        .formatted(bytes, asset.hash()));

                IntegratedPack.log("Downloading " + remoteAssetPath + " to " + destinationPath + "...");

                String assetFileName = Path.of(remoteAssetPath).toFile().getName();
                Path destination = IntegratedPack.WORKING_PATH.resolve(destinationPath).resolve(assetFileName);

                File destinationFolder = IntegratedPack.WORKING_PATH.resolve(destinationPath).toFile();
                if (!destinationFolder.exists()) {
                    if (!destinationFolder.mkdirs()) {
                        IntegratedPack.log("Could not make directories for downloading " + remoteAssetPath + " to " + destinationPath + "!");
                        continue;
                    }
                }

                Files.copy(assetStream, destination, StandardCopyOption.REPLACE_EXISTING);
                try {
                    assetStream.close();
                } catch (IOException e) {
                    IntegratedPack.log("Failed to close input stream, see stacktrace below, continuing...");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a resource as an InputStream.
     *
     * @param resourcePath The path to the resource in the Minecraft JAR file.
     * @return The resource as a BufferedImage.
     */
    public static InputStream getAsStream(String resourcePath) {
        try {
            return CLIENT_JAR.getInputStream(CLIENT_JAR.getEntry(resourcePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a resource as a String using the default charset (UTF-8).
     *
     * @param resourcePath The path to the resource in the Minecraft JAR file.
     * @return The resource as a String.
     */
    public static String getAsText(String resourcePath) throws IOException {
        return getAsText(resourcePath, Charset.defaultCharset());
    }

    /**
     * Returns a resource as a String.
     *
     * @param resourcePath The path to the resource in the Minecraft JAR file.
     * @param charset The charset to use for decoding the resource.
     * @return The resource as a String.
     */
    public static String getAsText(String resourcePath, Charset charset) throws IOException {
        InputStream is = getAsStream(resourcePath);
        String text = new String(is.readAllBytes(), charset);
        is.close();
        return text;
    }

    /**
     * Returns a resource as a BufferedImage.
     *
     * @param resourcePath The path to the resource in the Minecraft JAR file.
     * @return The resource as a BufferedImage.
     */
    public static BufferedImage getAsImage(String resourcePath) throws IOException {
        InputStream is = getAsStream(resourcePath);
        BufferedImage image = ImageIO.read(is);
        is.close();
        return image;
    }
}
