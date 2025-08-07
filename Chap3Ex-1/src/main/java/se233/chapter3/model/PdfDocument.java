package se233.chapter3.model;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.LinkedHashMap;

public class PdfDocument {
    private String name;
    private String filePath;
    private PDDocument document;
    private LinkedHashMap<String, List<FileFreq>> uniqueSets;

    public PdfDocument(String filePath) throws IOException {
        this.name = Paths.get(filePath).getFileName().toString();
        this.filePath = filePath;
        File pdfFile = new File(filePath);
        this.document = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfFile));
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }

    public PDDocument getDocument() {
        return document;
    }

    public LinkedHashMap<String, List<FileFreq>> getUniqueSets() {
        return uniqueSets;
    }

    public void setUniqueSets(LinkedHashMap<String, List<FileFreq>> uniqueSets) {
        this.uniqueSets = uniqueSets;
    }
}