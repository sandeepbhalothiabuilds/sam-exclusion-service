/**
 * Copyright (C) 2015, GIAYBAC
 * <p>
 * Released under the MIT license
 */
package com.nyc.properties.service;

import com.nyc.properties.entity.Table;
import com.nyc.properties.entity.TableCell;
import com.nyc.properties.entity.TableRow;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.*;
import java.util.*;


public class PDFTableExtractor {
    String regex = ".*[a-zA-Z].*";

    //contains pages that will be extracted table content.
    //If this variable doesn't contain any page, all pages will be extracted
    private final List<Integer> extractedPages = new ArrayList<>();
    private final List<Integer> exceptedPages = new ArrayList<>();
    //contains avoided line idx-s for each page,
    //if this multimap contains only one element and key of this element equals -1
    //then all lines in extracted pages contains in multi-map value will be avoided
    private final Multimap<Integer, Integer> pageNExceptedLinesMap = HashMultimap.create();

    private InputStream inputStream;
    private PDDocument document;
    private String password;

    public PDFTableExtractor setSource(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public PDFTableExtractor setSource(File file) {
        try {
            return this.setSource(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Invalid pdf file", ex);
        }
    }


    public PDFTableExtractor exceptLine(int pageIdx, int[] lineIdxes) {
        for (int lineIdx : lineIdxes) {
            pageNExceptedLinesMap.put(pageIdx, lineIdx);
        }
        return this;
    }

    public PDFTableExtractor exceptLine(int[] lineIdxes) {
        this.exceptLine(-1, lineIdxes);
        return this;
    }

    public List<Table> extract() {
        List<Table> retVal = new ArrayList<>();
        Multimap<Integer, Range<Integer>> pageIdNLineRangesMap = LinkedListMultimap.create();
        Multimap<Integer, TextPosition> pageIdNTextsMap = LinkedListMultimap.create();
        try {
            this.document = PDDocument.load(inputStream);
            for (int pageId = 0; pageId < document.getNumberOfPages(); pageId++) {
                List<TextPosition> texts = extractTextPositions(pageId);//sorted by .getY() ASC
                //extract line ranges
                List<Range<Integer>> lineRanges = getLineRanges(pageId, texts);
                //extract column ranges
                List<TextPosition> textsByLineRanges = getTextsByLineRanges(lineRanges, texts);

                pageIdNLineRangesMap.putAll(pageId, lineRanges);
                pageIdNTextsMap.putAll(pageId, textsByLineRanges);
            }

            //Calculate columnRanges
            List<Range<Integer>> columnRanges = getColumnRanges(pageIdNTextsMap.values());
            for (int pageId : pageIdNTextsMap.keySet()) {
                Table table = buildTable(pageId, (List) pageIdNTextsMap.get(pageId), (List) pageIdNLineRangesMap.get(pageId), columnRanges);
                retVal.add(table);
                //debug
                System.out.println("Found " + table.getRows().size() + " row(s) and " + columnRanges.size()
                        + " column(s) of a table in page " + pageId);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Parse pdf file fail", ex);
        } finally {
            if (this.document != null) {
                try {
                    this.document.close();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
        //return
        return retVal;
    }


    private Table buildTable(int pageIdx, List<TextPosition> tableContent,
                             List<Range<Integer>> rowTrapRanges, List<Range<Integer>> columnTrapRanges) {
        Table retVal = new Table(pageIdx, columnTrapRanges.size());
        int idx = 0;
        int rowIdx = 0;
        List<TextPosition> rowContent = new ArrayList<>();
        while (idx < tableContent.size()) {
            TextPosition textPosition = tableContent.get(idx);
            Range<Integer> rowTrapRange = rowTrapRanges.get(rowIdx);
            Range<Integer> textRange = Range.closed((int) textPosition.getY(),
                    (int) (textPosition.getY() + textPosition.getHeight()));
            if (rowTrapRange.encloses(textRange)) {
                rowContent.add(textPosition);
                idx++;
            } else {
                TableRow row = buildRow(rowIdx, rowContent, columnTrapRanges);
                if (!retVal.getRows().isEmpty()) {
                    cleanUpRowContent(row);
                }
                retVal.getRows().add(row);
                //next row: clear rowContent
                rowContent.clear();
                rowIdx++;
            }
        }
        //last row
        if (!rowContent.isEmpty() && rowIdx < rowTrapRanges.size()) {
            TableRow row = buildRow(rowIdx, rowContent, columnTrapRanges);
            cleanUpRowContent(row);
            retVal.getRows().add(row);
        }
        //return
        return retVal;
    }

    private void cleanUpRowContent(TableRow row) {
        int lastIndex = row.getCells().size() - 1;
        if (lastIndex == 13 && row.getCells().get(lastIndex - 1).getContent().matches(regex)) {

            TableCell blockCell = row.getCells().get(lastIndex - 1);
            TableCell status3Cell = row.getCells().get(lastIndex - 2);
            char[] ch = blockCell.getContent().toCharArray();

            StringBuilder status3Str = new StringBuilder("");
            StringBuilder blockStr = new StringBuilder("");

            for (int i = 0; i < blockCell.getContent().length(); i++) {
                if ((ch[i] >= 65 && ch[i] <= 90) || (ch[i] >= 97 && ch[i] <= 122)) {
                    status3Str.append(ch[i]);
                } else {
                    blockStr.append(ch[i]);
                }
            }
            TableCell newBlockCell = new TableCell(lastIndex - 1, blockStr.toString());
            TableCell newStatus3Cell = new TableCell(lastIndex - 2, status3Cell.getContent().concat(status3Str.toString()));

            System.out.println("Status3 has been changed from: " + row.getCells().get(lastIndex - 2).getContent() + " to " + newStatus3Cell.getContent() + " and Block has been changed from " + row.getCells().get(lastIndex - 1).getContent() + " to " + newBlockCell.getContent());

            row.getCells().set(lastIndex - 1, newBlockCell);
            row.getCells().set(lastIndex - 2, newStatus3Cell);
        }
    }


    private TableRow buildRow(int rowIdx, List<TextPosition> rowContent, List<Range<Integer>> columnTrapRanges) {
        TableRow retVal = new TableRow(rowIdx);
        //Sort rowContent
        Collections.sort(rowContent, new Comparator<TextPosition>() {
            @Override
            public int compare(TextPosition o1, TextPosition o2) {
                int retVal = 0;
                if (o1.getX() < o2.getX()) {
                    retVal = -1;
                } else if (o1.getX() > o2.getX()) {
                    retVal = 1;
                }
                return retVal;
            }
        });
        try {
            int idx = 0;
            int columnIdx = 0;
            List<TextPosition> cellContent = new ArrayList<>();
            while (idx < rowContent.size()) {
                try {
                    TextPosition textPosition = rowContent.get(idx);
                    Range<Integer> columnTrapRange = columnTrapRanges.get(columnIdx);
                    Range<Integer> textRange = Range.closed((int) textPosition.getX(),
                            (int) (textPosition.getX() + textPosition.getWidth()));
                    if (columnTrapRange.encloses(textRange)) {
                        cellContent.add(textPosition);
                        idx++;
                    } else if (textRange.lowerEndpoint() <= columnTrapRange.upperEndpoint() && textRange.upperEndpoint() >= columnTrapRange.upperEndpoint()) {
                        cellContent.add(textPosition);
                        idx++;
                        System.out.println("Some text is overlapping so it will be skipped for row: " + rowContent);
                    } else {
                        TableCell cell = buildCell(columnIdx, cellContent);
                        retVal.getCells().add(cell);
                        cellContent.clear();
                        columnIdx++;
                    }
                } catch (Exception ex) {
                    System.out.println("retVal11: " + retVal + " Error: " + ex);

                }
            }
            if (!cellContent.isEmpty() && columnIdx < columnTrapRanges.size()) {
                TableCell cell = buildCell(columnIdx, cellContent);
                retVal.getCells().add(cell);
            }
        } catch (Exception e) {
            System.out.println("retVal: " + retVal + " Error: " + e);
        }
        //return
        return retVal;
    }

    private TableCell buildCell(int columnIdx, List<TextPosition> cellContent) {
        Collections.sort(cellContent, new Comparator<TextPosition>() {
            @Override
            public int compare(TextPosition o1, TextPosition o2) {
                int retVal = 0;
                if (o1.getX() < o2.getX()) {
                    retVal = -1;
                } else if (o1.getX() > o2.getX()) {
                    retVal = 1;
                }
                return retVal;
            }
        });
        //String cellContentString = Joiner.on("").join(cellContent.stream().map(e -> e.getCharacter()).iterator());
        StringBuilder cellContentBuilder = new StringBuilder();
        for (TextPosition textPosition : cellContent) {
            cellContentBuilder.append(textPosition.getUnicode());
        }
        String cellContentString = cellContentBuilder.toString();
        return new TableCell(columnIdx, cellContentString);
    }

    private List<TextPosition> extractTextPositions(int pageId) throws IOException {
        TextPositionExtractor extractor = new TextPositionExtractor(document, pageId);
        return extractor.extract();
    }

    private boolean isExceptedLine(int pageIdx, int lineIdx) {
        boolean retVal = this.pageNExceptedLinesMap.containsEntry(pageIdx, lineIdx)
                || this.pageNExceptedLinesMap.containsEntry(-1, lineIdx);
        return retVal;
    }

    private List<TextPosition> getTextsByLineRanges(List<Range<Integer>> lineRanges, List<TextPosition> textPositions) {
        List<TextPosition> retVal = new ArrayList<>();
        int idx = 0;
        int lineIdx = 0;
        while (idx < textPositions.size() && lineIdx < lineRanges.size()) {
            TextPosition textPosition = textPositions.get(idx);
            Range<Integer> textRange = Range.closed((int) textPosition.getY(),
                    (int) (textPosition.getY() + textPosition.getHeight()));
            Range<Integer> lineRange = lineRanges.get(lineIdx);
            if (lineRange.encloses(textRange)) {
                retVal.add(textPosition);
                idx++;
            } else if (lineRange.upperEndpoint() < textRange.lowerEndpoint()) {
                lineIdx++;
            } else {
                idx++;
            }
        }
        //return
        return retVal;
    }


    private List<Range<Integer>> getColumnRanges(Collection<TextPosition> texts) {
        TrapRangeBuilder rangesBuilder = new TrapRangeBuilder();
        String headerNames = "";
        for (TextPosition text : texts) {
            Range<Integer> range = Range.closed((int) Math.floor(text.getX()), (int) Math.ceil(text.getX() + text.getWidth()));
            //Range<Integer> range = Range.closed((int)text.getX(), (int)(text.getX() + text.getWidth()));
            rangesBuilder.addRange(range);
            headerNames = headerNames.concat(text.toString());
            if (headerNames.contains("LOT")) {
                break;
            }

        }
        return updateRanges(rangesBuilder.build());
    }

    private List<Range<Integer>> updateRanges(List<Range<Integer>> ranges) {
        TrapRangeBuilder rangesBuilder = new TrapRangeBuilder();
        for (int i = 0; i < ranges.size(); i++) {
            if (i < ranges.size() - 1) {
                Range<Integer> range = Range.closed(ranges.get(i).lowerEndpoint(), ranges.get(i + 1).lowerEndpoint() - 1);
                rangesBuilder.addRange(range);
            } else {
                Range<Integer> range = Range.closed(ranges.get(i).lowerEndpoint(), ranges.get(i).upperEndpoint() + 10);
                rangesBuilder.addRange(range);
            }
        }
        return rangesBuilder.build();
    }

    private List<Range<Integer>> getLineRanges(int pageId, List<TextPosition> pageContent) {
        TrapRangeBuilder lineTrapRangeBuilder = new TrapRangeBuilder();
        for (TextPosition textPosition : pageContent) {
            Range<Integer> lineRange = Range.closed((int) textPosition.getY(),
                    (int) (textPosition.getY() + textPosition.getHeight()));
            //add to builder
            lineTrapRangeBuilder.addRange(lineRange);
        }
        List<Range<Integer>> lineTrapRanges = lineTrapRangeBuilder.build();
        List<Range<Integer>> retVal = removeExceptedLines(pageId, lineTrapRanges);
        return retVal;
    }

    private List<Range<Integer>> removeExceptedLines(int pageIdx, List<Range<Integer>> lineTrapRanges) {
        List<Range<Integer>> retVal = new ArrayList<>();
        for (int lineIdx = 0; lineIdx < lineTrapRanges.size(); lineIdx++) {
            boolean isExceptedLine = isExceptedLine(pageIdx, lineIdx)
                    || isExceptedLine(pageIdx, lineIdx - lineTrapRanges.size());
            if (!isExceptedLine) {
                retVal.add(lineTrapRanges.get(lineIdx));
            }
        }
        //return
        return retVal;
    }

    private static class TextPositionExtractor extends PDFTextStripper {

        private final List<TextPosition> textPositions = new ArrayList<>();
        private final int pageId;

        private TextPositionExtractor(PDDocument document, int pageId) throws IOException {
            super();
            super.setSortByPosition(true);
            super.document = document;
            this.pageId = pageId;
        }

        public void stripPage(int pageId) throws IOException {
            this.setStartPage(pageId + 1);
            this.setEndPage(pageId + 1);
            try (Writer writer = new OutputStreamWriter(new ByteArrayOutputStream())) {
                writeText(document, writer);
            }
        }

        @Override
        protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
            this.textPositions.addAll(textPositions);
        }


        private List<TextPosition> extract() throws IOException {
            this.stripPage(pageId);
            //sort
            Collections.sort(textPositions, new Comparator<TextPosition>() {
                @Override
                public int compare(TextPosition o1, TextPosition o2) {
                    int retVal = 0;
                    if (o1.getY() < o2.getY()) {
                        retVal = -1;
                    } else if (o1.getY() > o2.getY()) {
                        retVal = 1;
                    }
                    return retVal;

                }
            });
            return this.textPositions;
        }
    }
}
