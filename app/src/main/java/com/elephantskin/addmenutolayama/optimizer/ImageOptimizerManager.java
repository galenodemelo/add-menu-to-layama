package com.elephantskin.addmenutolayama.optimizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class ImageOptimizerManager {

    private final String imagePath;
    
    private ImageOptimizerManager(String projectPath) {
        this.imagePath = projectPath + "jpg/base/";
    }
    
    public static void optimize(String projectPath) throws InterruptedException {
        new ImageOptimizerManager(projectPath).optimize();
    }

    private void optimize() throws InterruptedException {
        System.out.println("> Iniciando otimização de imagens...");
        processImageConversion();
    }

    private void processImageConversion() throws InterruptedException {
        final Integer chunkSize = 50;
        AtomicInteger counter = new AtomicInteger(0);
        
        List<File> fileList = listImages();
        final Collection<List<File>> chunkedList = fileList
            .stream()
            .collect(Collectors.groupingBy(i -> counter.getAndIncrement() / chunkSize))
            .values();

        for (List<File> chunk : chunkedList) {
            Thread thread = new Thread(() -> {
                for (File file : chunk) {
                    try {
                        File webpImage = convertToWebp(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Erro ao converter imagem: " + file.getName());
                    }
                }
            });
            thread.start();
            thread.join();
        }
    }

    private List<File> listImages() {
        List<File> fileList = new ArrayList<File>();

        File directory1024 = new File(this.imagePath + "1024");
        File directory2048 = new File(this.imagePath + "2048");
        fileList.addAll(Arrays.asList(directory1024.listFiles((File dir, String name) -> name.endsWith(".jpg"))));
        fileList.addAll(Arrays.asList(directory2048.listFiles((File dir, String name) -> name.endsWith(".jpg"))));

        return fileList;
    }

    private File convertToWebp(File image) throws IOException {
        File webpImage = new File(image.getAbsolutePath().replace(".jpg", ".webp"));
        if (webpImage.exists()) return webpImage;
        
        System.out.println("> > Convertendo imagem " + image.getAbsolutePath() + " para webp...");
        BufferedImage bufferedImage = ImageIO.read(image);
        ImageIO.write(bufferedImage, "webp", webpImage);

        return webpImage;
    }

        return webpImage;
    }
}
