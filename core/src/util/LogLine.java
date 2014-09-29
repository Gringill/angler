package util;

import java.text.DateFormat;
import java.util.Date;

public class LogLine {
	final Date date;
	final String text;
	final String tag;
	final StackTraceElement[] stack;

	public LogLine(Date date, String text, String tag, StackTraceElement[] stack) {
		this.date = date;
		this.text = text;
		this.tag = tag;
		this.stack = stack;
	}

	@Override
	public String toString() {
		return "[" + DateFormat.getTimeInstance().format(date) + "] " + text + " " + stack[2];
	}
}
