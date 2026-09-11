package com.chatai.adr.util

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import java.util.regex.Pattern

object CodeFormatterUtil {

    enum class SegmentType {
        TEXT,
        CODE
    }

    data class ContentSegment(
        val type: SegmentType,
        val language: String,
        val content: String
    )

    private val CODE_BLOCK_PATTERN = Pattern.compile("```([a-zA-Z0-9_+-]*)\\n?([\\s\\S]*?)```")

    fun parseSegments(text: String): List<ContentSegment> {
        val segments = mutableListOf<ContentSegment>()
        if (text.isEmpty()) return segments

        val matcher = CODE_BLOCK_PATTERN.matcher(text)
        var lastEnd = 0

        while (matcher.find()) {
            val textBefore = text.substring(lastEnd, matcher.start()).trim()
            if (textBefore.isNotEmpty()) {
                segments.add(ContentSegment(SegmentType.TEXT, "", textBefore))
            }

            var lang = matcher.group(1)?.trim() ?: ""
            if (lang.isEmpty()) lang = "Code"

            val code = matcher.group(2)?.trimEnd('\r', '\n') ?: ""
            segments.add(ContentSegment(SegmentType.CODE, lang.replaceFirstChar { it.uppercase() }, code))

            lastEnd = matcher.end()
        }

        if (lastEnd < text.length) {
            val remain = text.substring(lastEnd).trim()
            if (remain.isNotEmpty()) {
                segments.add(ContentSegment(SegmentType.TEXT, "", remain))
            }
        }

        if (segments.isEmpty()) {
            segments.add(ContentSegment(SegmentType.TEXT, "", text))
        }

        return segments
    }

    fun formatMarkdown(context: Context, text: String): CharSequence {
        val ssb = SpannableStringBuilder(text)

        // 1. Highlight `inline code`
        val inlineCodePattern = Pattern.compile("`([^`]+)`")
        val inlineMatcher = inlineCodePattern.matcher(ssb)
        while (inlineMatcher.find()) {
            val start = inlineMatcher.start()
            val end = inlineMatcher.end()
            ssb.setSpan(BackgroundColorSpan(Color.parseColor("#1E293B")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.setSpan(ForegroundColorSpan(Color.parseColor("#38BDF8")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.setSpan(TypefaceSpan("monospace"), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // 2. Highlight **bold**
        val boldPattern = Pattern.compile("\\*\\*([^*]+)\\*\\*")
        val boldMatcher = boldPattern.matcher(ssb)
        while (boldMatcher.find()) {
            val start = boldMatcher.start()
            val end = boldMatcher.end()
            ssb.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return ssb
    }
}
