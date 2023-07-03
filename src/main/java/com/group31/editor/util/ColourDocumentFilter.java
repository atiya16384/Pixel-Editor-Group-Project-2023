package com.group31.editor.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColourDocumentFilter extends DocumentFilter {

  Pattern allowedPattern = Pattern.compile("#[0-9a-f]{6}");

  @Override
  public void insertString(FilterBypass fb, int offset, String string,
                           AttributeSet attrs) throws BadLocationException {
    if (allowedPattern.matcher(string).matches()) {
      super.insertString(fb, offset, string, attrs);
    }
  }

  @Override
  public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
    if (allowedPattern.matcher(string).matches()) {
      super.replace(fb, offset, length, string, attrs);
    }
  }
}
