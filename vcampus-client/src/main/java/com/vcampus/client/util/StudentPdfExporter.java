package com.vcampus.client.util;

import com.vcampus.common.dto.Student;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class StudentPdfExporter {

    // 表格样式
    private static final float TABLE_LEFT_MARGIN = 50;
    private static final float TABLE_RIGHT_MARGIN = 50;
    private static final float HEADER_HEIGHT = 20;  // 调小组标题高度
    private static final float CELL_HEIGHT = 20;    // 调小单元格高度
    private static final float COL_WIDTH = 270;
    private static final float BOTTOM_MARGIN = 50;

    // 颜色
    private static final PDColor TITLE_BG_COLOR = new PDColor(new float[]{0.2f, 0.4f, 0.8f}, PDDeviceRGB.INSTANCE);
    private static final PDColor HEADER_TEXT_COLOR = new PDColor(new float[]{1f, 1f, 1f}, PDDeviceRGB.INSTANCE);
    private static final PDColor GROUP_HEADER_BG_COLOR = new PDColor(new float[]{0.7f, 0.7f, 0.7f}, PDDeviceRGB.INSTANCE);
    private static final PDColor ROW_BG_COLOR = new PDColor(new float[]{1f, 1f, 1f}, PDDeviceRGB.INSTANCE);
    private static final PDColor BORDER_COLOR = new PDColor(new float[]{0.8f, 0.8f, 0.8f}, PDDeviceRGB.INSTANCE);
    private static final PDColor LABEL_TEXT_COLOR = new PDColor(new float[]{0f, 0f, 0f}, PDDeviceRGB.INSTANCE);
    private static final PDColor CONTENT_TEXT_COLOR = new PDColor(new float[]{0.1f, 0.1f, 0.1f}, PDDeviceRGB.INSTANCE);

    public static void exportStudentData(Student student) {
        if (student == null) {
            System.err.println("学生信息为空，无法导出PDF。");
            return;
        }

        String safeStudentId = student.getStudentId() != null ?
                student.getStudentId().replaceAll("[^a-zA-Z0-9_-]", "") : "Unknown";
        String fileName = "Student_" + safeStudentId + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PDType0Font font = loadChineseFont(document);

            float titleFontSize = 22;
            float headerFontSize = 14;
            float contentFontSize = 12;

            // 顶部标题
            float titleY = 750;
            writeTextWithBackground(contentStream, font, "学生信息表", titleFontSize,
                    0, titleY, 615, 40, TITLE_BG_COLOR, HEADER_TEXT_COLOR);

            float currentY = titleY - 60;

            // 学生信息分组
            String[][][] studentDataGroups = {
                    {{"用户ID", student.getUserId()}, {"学号", student.getStudentId()},
                            {"姓名", student.getName()}, {"性别", student.getGender()},
                            {"出生日期", student.getBirth_date()}, {"籍贯", student.getNative_place()}},

                    {{"年级", String.valueOf(student.getGrade())}, {"专业", student.getMajor()},
                            {"学院", student.getCollege()}, {"政治面貌", student.getPolitics_status()},
                            {"学籍状态", student.getStudent_status()}},

                    {{"手机号", student.getPhone()}, {"邮箱", student.getEmail()},
                            {"宿舍地址", student.getDormAddress()}},

                    {{"父亲姓名", student.getFatherName()}, {"父亲电话", student.getFatherPhone()},
                            {"父亲政治面貌", student.getFatherPoliticsStatus()},
                            {"父亲工作单位", student.getFatherWorkUnit()}},

                    {{"母亲姓名", student.getMotherName()}, {"母亲电话", student.getMotherPhone()},
                            {"母亲政治面貌", student.getMotherPoliticsStatus()},
                            {"母亲工作单位", student.getMotherWorkUnit()}}
            };
            String[] groupTitles = {"基本信息", "学业信息", "联系信息", "父亲信息", "母亲信息"};

            for (int g = 0; g < studentDataGroups.length; g++) {
                String[][] groupData = studentDataGroups[g];

                currentY = drawGroupHeader(contentStream, font, currentY, groupTitles[g], headerFontSize);
                for (String[] row : groupData) {
                    String fieldName = cleanText(row[0]);
                    String fieldValue = cleanText(row[1]);
                    int valueLines = countLines(fieldValue, font, contentFontSize, COL_WIDTH);
                    float rowHeight = Math.max(CELL_HEIGHT, valueLines * CELL_HEIGHT);
                    currentY = drawSingleRow(contentStream, font, currentY, rowHeight,
                            fieldName, fieldValue, contentFontSize, ROW_BG_COLOR);
                }
            }

            // 签名区域
            currentY = drawSignatureArea(contentStream, font, currentY, contentFontSize);

            contentStream.close();
            document.save(new File(fileName));
            System.out.println("PDF 导出成功: " + new File(fileName).getAbsolutePath());

        } catch (IOException e) {
            System.err.println("PDF导出失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static PDType0Font loadChineseFont(PDDocument document) throws IOException {
        try (InputStream fontStream = StudentPdfExporter.class.getResourceAsStream("/fonts/simhei.ttf")) {
            if (fontStream == null) {
                throw new RuntimeException("未找到字体文件 simhei.ttf，请确保放在 resources/fonts/ 目录下");
            }
            return PDType0Font.load(document, fontStream);
        }
    }

    private static float drawGroupHeader(PDPageContentStream cs, PDType0Font font,
                                         float currentY, String groupTitle, float fontSize) throws IOException {
        drawCellBackground(cs, TABLE_LEFT_MARGIN, currentY - HEADER_HEIGHT,
                COL_WIDTH * 2, HEADER_HEIGHT, GROUP_HEADER_BG_COLOR);
        writeText(cs, font, TABLE_LEFT_MARGIN + 5, currentY - HEADER_HEIGHT / 2 - fontSize / 2 + 5,
                groupTitle, fontSize, HEADER_TEXT_COLOR);
        drawCellBorder(cs, TABLE_LEFT_MARGIN, currentY - HEADER_HEIGHT, COL_WIDTH * 2, HEADER_HEIGHT);
        return currentY - HEADER_HEIGHT;
    }

    private static float drawSingleRow(PDPageContentStream cs, PDType0Font font, float currentY,
                                       float rowHeight, String fieldName, String fieldValue,
                                       float fontSize, PDColor bgColor) throws IOException {
        float rowBottomY = currentY - rowHeight;
        drawCellBackground(cs, TABLE_LEFT_MARGIN, rowBottomY, COL_WIDTH * 2, rowHeight, bgColor);

        writeText(cs, font, TABLE_LEFT_MARGIN + 5, currentY - fontSize - 5, fieldName + "：", fontSize, LABEL_TEXT_COLOR);
        writeWrappedText(cs, font, TABLE_LEFT_MARGIN + COL_WIDTH + 5, currentY - fontSize - 5,
                fieldValue, fontSize, CELL_HEIGHT, COL_WIDTH - 10, CONTENT_TEXT_COLOR);

        drawCellBorder(cs, TABLE_LEFT_MARGIN, rowBottomY, COL_WIDTH * 2, rowHeight);
        cs.setStrokingColor(BORDER_COLOR);
        cs.setLineWidth(0.5f);
        cs.moveTo(TABLE_LEFT_MARGIN + COL_WIDTH, rowBottomY);
        cs.lineTo(TABLE_LEFT_MARGIN + COL_WIDTH, rowBottomY + rowHeight);
        cs.stroke();

        return rowBottomY;
    }

    private static void drawCellBackground(PDPageContentStream cs, float x, float y,
                                           float width, float height, PDColor color) throws IOException {
        cs.setNonStrokingColor(color);
        cs.addRect(x, y, width, height);
        cs.fill();
    }

    private static void drawCellBorder(PDPageContentStream cs, float x, float y,
                                       float width, float height) throws IOException {
        cs.setStrokingColor(BORDER_COLOR);
        cs.setLineWidth(0.5f);
        cs.addRect(x, y, width, height);
        cs.stroke();
    }

    private static void writeText(PDPageContentStream cs, PDType0Font font, float x, float y,
                                  String text, float fontSize, PDColor textColor) throws IOException {
        if (text.isEmpty()) return;
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.setNonStrokingColor(textColor);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private static void writeWrappedText(PDPageContentStream cs, PDType0Font font, float x, float y,
                                         String text, float fontSize, float lineHeight,
                                         float maxWidth, PDColor textColor) throws IOException {
        if (text.isEmpty()) return;
        float currentY = y;
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        float currentLineWidth = 0;
        for (String word : words) {
            String wordWithSpace = word + " ";
            float wordWidth = font.getStringWidth(wordWithSpace) / 1000 * fontSize;
            if (currentLineWidth + wordWidth <= maxWidth) {
                currentLine.append(wordWithSpace);
                currentLineWidth += wordWidth;
            } else {
                writeText(cs, font, x, currentY, currentLine.toString().trim(), fontSize, textColor);
                currentY -= lineHeight;
                currentLine = new StringBuilder(wordWithSpace);
                currentLineWidth = wordWidth;
            }
        }
        if (currentLine.length() > 0) {
            writeText(cs, font, x, currentY, currentLine.toString().trim(), fontSize, textColor);
        }
    }

    private static String cleanText(String text) {
        if (text == null) return "";
        return text.replaceAll("[\r\0]", "").replaceAll("　", " ").trim();
    }

    private static int countLines(String text, PDType0Font font, float fontSize, float maxWidth) throws IOException {
        if (text.isEmpty()) return 1;
        float textTotalWidth = font.getStringWidth(text) / 1000 * fontSize;
        if (textTotalWidth <= maxWidth) return 1;

        String[] words = text.split(" ");
        int lines = 1;
        float currentLineWidth = 0;
        for (String word : words) {
            float wordWidth = font.getStringWidth(word + " ") / 1000 * fontSize;
            if (currentLineWidth + wordWidth > maxWidth) {
                lines++;
                currentLineWidth = wordWidth;
            } else {
                currentLineWidth += wordWidth;
            }
        }
        return lines;
    }

    private static void writeTextWithBackground(PDPageContentStream cs, PDType0Font font, String text,
                                                float fontSize, float x, float y,
                                                float width, float height, PDColor bgColor, PDColor textColor) throws IOException {
        drawCellBackground(cs, x, y, width, height, bgColor);
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float textX = x + (width - textWidth) / 2;
        float textY = y + (height - fontSize) / 2 + 5;
        writeText(cs, font, textX, textY, text, fontSize, textColor);
    }

    private static float drawSignatureArea(PDPageContentStream cs, PDType0Font font,
                                           float currentY, float fontSize) throws IOException {
        currentY -= 40;
        writeText(cs, font, TABLE_LEFT_MARGIN, currentY, "学生签名：", fontSize, CONTENT_TEXT_COLOR);
        cs.setStrokingColor(BORDER_COLOR);
        cs.setLineWidth(0.5f);
        cs.moveTo(TABLE_LEFT_MARGIN + 80, currentY - 5);
        cs.lineTo(TABLE_LEFT_MARGIN + 250, currentY - 5);
        cs.stroke();

        writeText(cs, font, TABLE_LEFT_MARGIN + 300, currentY, "日期：", fontSize, CONTENT_TEXT_COLOR);
        cs.moveTo(TABLE_LEFT_MARGIN + 350, currentY - 5);
        cs.lineTo(TABLE_LEFT_MARGIN + 500, currentY - 5);
        cs.stroke();

        return currentY - 30;
    }
}
