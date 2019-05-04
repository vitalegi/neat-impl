package it.vitalegi.neat.impl.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

public class TablePrinter {

	private class Row {

		List<String> values;

		public Row(List<String> values) {
			this.values = values;
		}
	}

	public static final int LEFT = -1;

	public static final int RIGHT = 1;

	public static TablePrinter newPrinter() {
		TablePrinter printer = new TablePrinter();
		printer.rows = new ArrayList<>();
		printer.alignments = new ArrayList<>();
		return printer;
	}

	private List<Integer> alignments;

	private Row headers;

	private List<Row> rows;

	public void addRow(List<String> row) {
		rows.add(new Row(row));
	}

	private void computeWidths(int[] widths, Row row) {
		for (int i = 0; i < widths.length; i++) {
			if (widths[i] < row.values.get(i).length()) {
				widths[i] = row.values.get(i).length();
			}
		}
	}

	private int[] getAlignments() {
		return alignments.stream().mapToInt(i -> i).toArray();
	}

	private String getHorizontalBar(int[] widths) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < widths.length; i++) {
			sb.append("+-");
			sb.append(StringUtil.leftPadding("-", widths[i], '-'));
			sb.append("-");
		}
		sb.append("+");
		return sb.toString();
	}

	private String getRow(Row row, int[] widths, int[] alignments) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < widths.length; i++) {
			sb.append("| ");
			String v = row.values.get(i);
			int w = widths[i];
			int align = LEFT;
			if (i < alignments.length) {
				align = alignments[i];
			}
			if (TablePrinter.LEFT == align) {
				sb.append(StringUtil.rightPadding(v, w));
			} else {
				sb.append(StringUtil.leftPadding(v, w));
			}
			sb.append(" ");
		}
		sb.append("|");
		return sb.toString();
	}

	private int[] getWidths() {
		int[] widths = new int[headers.values.size()];
		computeWidths(widths, headers);
		rows.forEach(r -> computeWidths(widths, r));
		return widths;
	}

	public void log(Logger log) {
		if (log.isDebugEnabled()) {
			int[] widths = getWidths();
			log.debug(getHorizontalBar(widths));
			log.debug(getRow(headers, widths, getAlignments()));
			log.debug(getHorizontalBar(widths));
			rows.forEach(r -> log.debug(getRow(r, widths, getAlignments())));
			log.debug(getHorizontalBar(widths));
		}
	}

	public void setAlignments(List<Integer> alignments) {
		this.alignments = alignments;
	}

	public void setHeaders(List<String> headers) {
		this.headers = new Row(headers);
	}
}
