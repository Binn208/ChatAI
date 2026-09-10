package com.chatai.adr.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatai.adr.R;
import com.chatai.adr.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> messages = new ArrayList<>();
    private final Context context;

    public ChatAdapter(Context context) {
        this.context = context;
    }

    public interface OnMessageActionListener {
        void onSpeak(ChatMessage message);
        void onRegenerate(ChatMessage message, int position);
        void onFeedback(ChatMessage message, boolean isLiked);
    }

    private OnMessageActionListener actionListener;

    public void setOnMessageActionListener(OnMessageActionListener listener) {
        this.actionListener = listener;
    }

    public void setMessages(List<ChatMessage> newMessages) {
        messages.clear();
        if (newMessages != null) {
            messages.addAll(newMessages);
        }
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void updateMessageContent(int position, String newContent) {
        if (position >= 0 && position < messages.size()) {
            messages.get(position).setContent(newContent);
            notifyItemChanged(position);
        }
    }

    public void clearMessages() {
        int count = messages.size();
        messages.clear();
        notifyItemRangeRemoved(0, count);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ChatMessage.TYPE_USER) {
            View view = inflater.inflate(R.layout.item_message_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_ai, parent, false);
            return new AiViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else if (holder instanceof AiViewHolder) {
            ((AiViewHolder) holder).bind(message, position, context, actionListener);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContent;
        private final TextView tvTime;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvUserContent);
            tvTime = itemView.findViewById(R.id.tvUserTime);
        }

        void bind(ChatMessage message) {
            tvContent.setText(message.getContent());
            tvTime.setText(message.getFormattedTime());
        }
    }

    static class AiViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContent;
        private final TextView tvTime;
        private final ImageButton btnCopy;
        private final ImageButton btnSpeak;
        private final ImageButton btnRegenerate;
        private final ImageButton btnLike;
        private final ImageButton btnDislike;
        private final LinearLayout layoutCodeBlocks;

        AiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvAiContent);
            tvTime = itemView.findViewById(R.id.tvAiTime);
            btnCopy = itemView.findViewById(R.id.btnCopyAiMessage);
            btnSpeak = itemView.findViewById(R.id.btnSpeakAiMessage);
            btnRegenerate = itemView.findViewById(R.id.btnRegenerateAiMessage);
            btnLike = itemView.findViewById(R.id.btnLikeAiMessage);
            btnDislike = itemView.findViewById(R.id.btnDislikeAiMessage);
            layoutCodeBlocks = itemView.findViewById(R.id.layoutCodeBlocks);
        }

        void bind(ChatMessage message, int position, Context context, OnMessageActionListener listener) {
            String raw = message.getContent() != null ? message.getContent() : "";

            // OpenCode parsing: Check if there are markdown code blocks
            List<com.chatai.adr.util.CodeFormatterUtil.FormattedSection> sections = 
                    com.chatai.adr.util.CodeFormatterUtil.parseContent(raw);

            boolean hasCodeBlock = false;
            for (com.chatai.adr.util.CodeFormatterUtil.FormattedSection sec : sections) {
                if (sec.isCodeBlock) {
                    hasCodeBlock = true;
                    break;
                }
            }

            if (!hasCodeBlock) {
                if (layoutCodeBlocks != null) {
                    layoutCodeBlocks.removeAllViews();
                    layoutCodeBlocks.setVisibility(View.GONE);
                }
                tvContent.setText(com.chatai.adr.util.CodeFormatterUtil.formatInlineMarkdown(raw));
                tvContent.setVisibility(View.VISIBLE);
            } else {
                if (layoutCodeBlocks != null) {
                    layoutCodeBlocks.removeAllViews();
                    layoutCodeBlocks.setVisibility(View.VISIBLE);

                    for (com.chatai.adr.util.CodeFormatterUtil.FormattedSection sec : sections) {
                        if (sec.isCodeBlock) {
                            View codeBlockView = com.chatai.adr.util.CodeFormatterUtil.createCodeBlockView(
                                    context, sec.language, sec.content);
                            layoutCodeBlocks.addView(codeBlockView);
                        } else if (!sec.content.trim().isEmpty()) {
                            TextView inlineTv = new TextView(context);
                            inlineTv.setText(com.chatai.adr.util.CodeFormatterUtil.formatInlineMarkdown(sec.content));
                            inlineTv.setTextColor(context.getResources().getColor(R.color.ai_bubble_text));
                            inlineTv.setTextSize(14.5f);
                            inlineTv.setTextIsSelectable(true);
                            inlineTv.setPadding(0, 8, 0, 8);
                            layoutCodeBlocks.addView(inlineTv);
                        }
                    }
                    tvContent.setVisibility(View.GONE);
                } else {
                    tvContent.setText(com.chatai.adr.util.CodeFormatterUtil.formatInlineMarkdown(raw));
                    tvContent.setVisibility(View.VISIBLE);
                }
            }

            tvTime.setText(message.getFormattedTime());

            // Copy full message
            btnCopy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("AI Message", message.getContent());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
                }
            });

            // Speak (TTS)
            if (btnSpeak != null) {
                btnSpeak.setOnClickListener(v -> {
                    if (listener != null) listener.onSpeak(message);
                });
            }

            // Regenerate
            if (btnRegenerate != null) {
                btnRegenerate.setOnClickListener(v -> {
                    if (listener != null) listener.onRegenerate(message, position);
                });
            }

            // Like
            if (btnLike != null) {
                btnLike.setOnClickListener(v -> {
                    btnLike.setColorFilter(android.graphics.Color.parseColor("#10B981")); // Green
                    if (btnDislike != null) btnDislike.clearColorFilter();
                    Toast.makeText(context, R.string.feedback_liked, Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onFeedback(message, true);
                });
            }

            // Dislike
            if (btnDislike != null) {
                btnDislike.setOnClickListener(v -> {
                    btnDislike.setColorFilter(android.graphics.Color.parseColor("#EF4444")); // Red
                    if (btnLike != null) btnLike.clearColorFilter();
                    Toast.makeText(context, R.string.feedback_disliked, Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onFeedback(message, false);
                });
            }
        }
    }
}
