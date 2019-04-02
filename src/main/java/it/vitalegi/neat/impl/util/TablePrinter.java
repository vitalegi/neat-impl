package it.vitalegi.neat.impl.util;

import java.util.ArrayList;
import java.util.List;

public class TablePrinter {

	private Row headers;
	private List<Row> rows;

	public static TablePrinter newPrinter() {
		TablePrinter printer = new TablePrinter();
		printer.rows = new ArrayList<>();
		return printer;
	}

	public void setHeaders(List<String> headers) {
		this.headers = new Row(headers);
	}

	public void addRow(List<String> row) {
		rows.add(new Row(row));
	}

	public StringBuilder print() {
		StringBuilder sb = new StringBuilder();
		int[] widths = getWidths();
		sb.append(printHorizontalBar(widths)).append("\n");
		sb.append(print(headers, widths)).append("\n");
		sb.append(printHorizontalBar(widths)).append("\n");
		rows.forEach(r -> sb.append(print(r, widths)).append("\n"));
		sb.append(printHorizontalBar(widths)).append("\n");
		return sb;
	}

	private int[] getWidths() {
		int[] widths = new int[headers.values.size()];
		computeWidths(widths, headers);
		rows.forEach(r -> computeWidths(widths, r));
		return widths;
	}

	private void computeWidths(int[] widths, Row row) {
		for (int i = 0; i < widths.length; i++) {
			if (widths[i] < row.values.get(i).length()) {
				widths[i] = row.values.get(i).length();
			}
		}
	}

	private String print(Row row, int[] widths) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < widths.length; i++) {
			sb.append("| ");
			sb.append(StringUtil.leftPadding(row.values.get(i), widths[i]));
			sb.append(" ");
		}
		sb.append("|");
		return sb.toString();
	}

	private String printHorizontalBar(int[] widths) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < widths.length; i++) {
			sb.append("+-");
			sb.append(StringUtil.leftPadding("-", widths[i], '-'));
			sb.append("-");
		}
		sb.append("+");
		return sb.toString();
	}

	private class Row {

		List<String> values;

		public Row(List<String> values) {
			this.values = values;
		}
	}
}
