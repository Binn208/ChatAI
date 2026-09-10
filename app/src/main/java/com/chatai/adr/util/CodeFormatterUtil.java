package com.chatai.adr.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chatai.adr.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OpenCode / Code Block parser and renderer for rich code display in Chat AI.
 */
public class CodeFormatterUtil {

    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```([a-zA-Z0-9_-]*)\\s*\\n?([\\s\\S]*?)```");
    private static final Pattern INLINE_CODE_PATTERN = Pattern.compile("`([^`]+)`");
    private static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*([^*]+)\\*\\*");

    public static class FormattedSection {
        public final boolean isCodeBlock;
        public final String language;
        public final String content;

        public FormattedSection(boolean isCodeBlock, String language, String content) {
            this.isCodeBlock = isCodeBlock;
            this.language = (language != null && !language.trim().isEmpty()) ? language.toUpperCase().trim() : "CODE";
            this.content = content;
        }
    }

    /**
     * Parses raw markdown text into sections of plain markdown text and code blocks.
     */
    public static List<FormattedSection> parseContent(String rawText) {
        List<FormattedSection> sections = new ArrayList<>();
        if (rawText == null || rawText.isEmpty()) {
            return sections;
        }

        Matcher matcher = CODE_BLOCK_PATTERN.matcher(rawText);
        int lastIndex = 0;

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String textBefore = rawText.substring(lastIndex, matcher.start());
                if (!textBefore.trim().isEmpty()) {
                    sections.add(new FormattedSection(false, null, textBefore));
                }
            }

            String lang = matcher.group(1);
            String code = matcher.group(2);
            if (code != null) {
                // Trim trailing newlines
                sections.add(new FormattedSection(true, lang, code.replaceAll("^\\n+|\\n+$", "")));
            }

            lastIndex = matcher.end();
        }

        if (lastIndex < rawText.length()) {
            String trailingText = rawText.substring(lastIndex);
            if (!trailingText.trim().isEmpty()) {
                sections.add(new FormattedSection(false, null, trailingText));
            }
        }

        if (sections.isEmpty()) {
            sections.add(new FormattedSection(false, null, rawText));
        }

        return sections;
    }

    /**
     * Formats basic markdown (bold, inline code) into SpannableStringBuilder.
     */
    public static CharSequence formatInlineMarkdown(String text) {
        if (text == null) return "";
        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        // 1. Process Bold **text**
        Matcher boldMatcher = BOLD_PATTERN.matcher(builder);
        while (boldMatcher.find()) {
            int start = boldMatcher.start();
            int end = boldMatcher.end();
            String inner = boldMatcher.group(1);
            builder.replace(start, end, inner);
            builder.setSpan(new StyleSpan(Typeface.BOLD), start, start + inner.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            boldMatcher = BOLD_PATTERN.matcher(builder); // reset after replace
        }

        // 2. Process Inline code `code`
        Matcher inlineMatcher = INLINE_CODE_PATTERN.matcher(builder);
        while (inlineMatcher.find()) {
            int start = inlineMatcher.start();
            int end = inlineMatcher.end();
            String inner = inlineMatcher.group(1);
            builder.replace(start, end, inner);
            int newEnd = start + inner.length();
            builder.setSpan(new TypefaceSpan("monospace"), start, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new BackgroundColorSpan(Color.parseColor("#E2E8F0")), start, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#BE185D")), start, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new RelativeSizeSpan(0.92f), start, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            inlineMatcher = INLINE_CODE_PATTERN.matcher(builder);
        }

        return builder;
    }

    /**
     * Creates a customized OpenCode styled block view.
     */
    public static View createCodeBlockView(Context context, String language, String codeContent) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 10);
        container.setLayoutParams(params);

        // Styling: Dark background rounded container
        container.setBackgroundResource(R.drawable.bg_input_box);
        container.setClipToOutline(true);

        // Header Bar (Language Badge + Copy Button)
        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(Color.parseColor("#1E293B")); // Slate dark
        header.setPadding(24, 12, 16, 12);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        header.setLayoutParams(headerParams);

        TextView tvLang = new TextView(context);
        tvLang.setText("💻 " + language);
        tvLang.setTextColor(Color.parseColor("#38BDF8")); // Sky Blue
        tvLang.setTextSize(11f);
        tvLang.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        LinearLayout.LayoutParams langParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        tvLang.setLayoutParams(langParams);
        header.addView(tvLang);

        TextView btnCopy = new TextView(context);
        btnCopy.setText("📋 SAO CHÉP");
        btnCopy.setTextColor(Color.parseColor("#94A3B8"));
        btnCopy.setTextSize(10.5f);
        btnCopy.setTypeface(Typeface.DEFAULT_BOLD);
        btnCopy.setPadding(16, 4, 16, 4);
        btnCopy.setClickable(true);
        btnCopy.setFocusable(true);
        btnCopy.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("OpenCode Snippet", codeContent);
            if (cm != null) {
                cm.setPrimaryClip(clip);
                btnCopy.setText("✓ ĐÃ CHÉP");
                btnCopy.setTextColor(Color.parseColor("#10B981"));
                Toast.makeText(context, "Đã sao chép khối mã " + language + "!", Toast.LENGTH_SHORT).show();
                btnCopy.postDelayed(() -> {
                    btnCopy.setText("📋 SAO CHÉP");
                    btnCopy.setTextColor(Color.parseColor("#94A3B8"));
                }, 2000);
            }
        });
        header.addView(btnCopy);

        container.addView(header);

        // Code Body
        TextView tvCode = new TextView(context);
        tvCode.setText(codeContent);
        tvCode.setTextSize(13f);
        tvCode.setTypeface(Typeface.MONOSPACE);
        tvCode.setTextColor(Color.parseColor("#E2E8F0")); // Light slate
        tvCode.setBackgroundColor(Color.parseColor("#0F172A")); // Deep Slate Editor
        tvCode.setPadding(24, 20, 24, 20);
        tvCode.setTextIsSelectable(true);
        tvCode.setHorizontallyScrolling(true);
        LinearLayout.LayoutParams codeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tvCode.setLayoutParams(codeParams);
        container.addView(tvCode);

        return container;
    }
}
