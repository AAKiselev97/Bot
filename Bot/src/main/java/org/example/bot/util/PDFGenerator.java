package org.example.bot.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.List;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.provider.impl.JSONProviderImpl;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFGenerator {
    private static final Logger log = LogManager.getLogger(JSONProviderImpl.class);
    private static final String FILE_PATH = System.getProperty("user.home") + "/file/";
    private static final String FILE_TYPE = ".pdf";
    private static String fileName;

    public static void init(String name) {
        fileName = name + FILE_TYPE;
    }

    public static void generatePdfFromResultSet(java.util.List<String> strings) {
        try {
            File file = new File(FILE_PATH + fileName);
            file.createNewFile();
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath()));
            Font font = new Font(Font.HELVETICA, 12, Font.BOLDITALIC, Color.BLACK);
            document.open();
            document.add(new Paragraph(fileName + " history", font));
            List list = new List(List.UNORDERED, 14);
            for (String string : strings) {
                list.add(new ListItem(string, font));
            }
            document.add(list);
            document.close();
            pdfWriter.close();
            log.debug("pdf create");
        } catch (IOException | DocumentException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }
}
