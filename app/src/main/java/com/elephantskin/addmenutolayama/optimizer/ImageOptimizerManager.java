package com.elephantskin.addmenutolayama.optimizer;

import java.awt.Image;
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
    private final Integer[] sizeToResizeList = {1680, 1440};
    
    private ImageOptimizerManager(String projectPath) {
        this.imagePath = projectPath + "jpg/base/";
    }
    
    public static void optimize(String projectPath) throws InterruptedException {
        System.out.println("> Iniciando otimização de imagens...");

        new ImageOptimizerManager(projectPath).processImageOptimization();
    }

    public void processImageOptimization() throws InterruptedException {
        final Integer chunkSize = 50;
        AtomicInteger counter = new AtomicInteger(0);
        
        createDirectoriesIfNecessary();

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
                        if (webpImage.getAbsolutePath().contains("1024")) continue;
                        
                        resize(webpImage);
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

    private void createDirectoriesIfNecessary() {
        for (Integer size : this.sizeToResizeList) {
            String path = this.imagePath + size;
            File dir = new File(path);
            if (dir.exists()) continue;
            
            System.out.println("> > Criando diretório " + path);
            dir.mkdirs();
        }
    }

    private List<File> listImages() {
        List<File> fileList = new ArrayList<File>();

        File directory1024 = new File(this.imagePath + "1024");
        File directory2048 = new File(this.imagePath + "2048");
        fileList.addAll(Arrays.asList(directory1024.listFiles((File dir, String name) -> !name.endsWith(".webp"))));
        fileList.addAll(Arrays.asList(directory2048.listFiles((File dir, String name) -> !name.endsWith(".webp"))));

        return fileList;
    }

    private File convertToWebp(File image) throws IOException {
        File webpImage = new File(image.getAbsolutePath().replace(".jpg", ".webp"));
        if (webpImage.exists()) {
            System.out.println("> > Imagem " + image.getAbsolutePath() + " já existe. Ignorando...");
            return webpImage;
        }
        
        System.out.println("> > Convertendo imagem " + image.getAbsolutePath() + " para webp...");
        BufferedImage bufferedImage = ImageIO.read(image);
        ImageIO.write(bufferedImage, "webp", webpImage);

        return webpImage;
    }

    private void resize(File image) throws IOException {
        for (Integer size : this.sizeToResizeList) {
            final String fullpath = this.imagePath + size + "/" + image.getName();
            File resizedImage = new File(fullpath);
            if (resizedImage.exists()) {
                System.out.println("> > Imagem " + image.getAbsolutePath() + " já redimensionada para " + size + "px");
                continue;
            }
            
            System.out.println("> > Redimensionando imagem " + image.getAbsolutePath() + " para " + size + "px");
            resizedImage.mkdir();
            BufferedImage output1to1 = resizeAspect1to1(image, size);
            ImageIO.write(output1to1, "webp", resizedImage);
        }
    }

    private BufferedImage resizeAspect1to1(File image, Integer size) throws IOException {
        BufferedImage originalImage = ImageIO.read(image);
        Image resultingImage = originalImage.getScaledInstance(size, size, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

        return outputImage;
    }
}
