package com.chatai.adr.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.chatai.adr.R
import com.chatai.adr.model.ChatMessage
import com.chatai.adr.util.CodeFormatterUtil

class ChatAdapter(
    private val messages: MutableList<ChatMessage>,
    private val listener: OnMessageActionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnMessageActionListener {
        fun onSpeakText(text: String)
        fun onRegenerate(message: ChatMessage)
    }

    override fun getItemViewType(position: Int): Int {
        val msg = messages[position]
        return if (msg.isUser) VIEW_TYPE_USER else VIEW_TYPE_ASSISTANT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_USER) {
            val view = inflater.inflate(R.layout.item_message_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_message_ai, parent, false)
            AssistantViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserViewHolder) {
            holder.bind(message)
        } else if (holder is AssistantViewHolder) {
            holder.bind(message, listener)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun setMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun updateLastMessage(content: String, isStreaming: Boolean, modelName: String? = null) {
        if (messages.isNotEmpty()) {
            val lastIdx = messages.size - 1
            val last = messages[lastIdx]
            last.content = content
            last.isStreaming = isStreaming
            if (modelName != null) last.modelName = modelName
            notifyItemChanged(lastIdx)
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvContent: TextView = itemView.findViewById(R.id.tvUserContent)
        private val tvTime: TextView = itemView.findViewById(R.id.tvUserTime)

        fun bind(message: ChatMessage) {
            tvContent.text = message.content
            tvTime.text = message.formattedTime
        }
    }

    class AssistantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvContent: TextView = itemView.findViewById(R.id.tvAiContent)
        private val layoutCodeBlocks: LinearLayout = itemView.findViewById(R.id.layoutCodeBlocks)
        private val tvTime: TextView = itemView.findViewById(R.id.tvAiTime)
        private val btnCopy: ImageButton = itemView.findViewById(R.id.btnCopyAiMessage)
        private val btnSpeak: ImageButton = itemView.findViewById(R.id.btnSpeakAiMessage)
        private val btnRegenerate: ImageButton = itemView.findViewById(R.id.btnRegenerateAiMessage)

        fun bind(message: ChatMessage, listener: OnMessageActionListener) {
            val context = itemView.context
            tvTime.text = message.formattedTime

            val segments = CodeFormatterUtil.parseSegments(message.content)
            val hasCode = segments.any { it.type == CodeFormatterUtil.SegmentType.CODE }

            if (!hasCode) {
                tvContent.visibility = View.VISIBLE
                tvContent.text = CodeFormatterUtil.formatMarkdown(context, message.content)
                layoutCodeBlocks.visibility = View.GONE
            } else {
                tvContent.visibility = View.GONE
                layoutCodeBlocks.visibility = View.VISIBLE
                layoutCodeBlocks.removeAllViews()

                for (segment in segments) {
                    if (segment.type == CodeFormatterUtil.SegmentType.CODE) {
                        val codeView = createOpenCodeView(context, segment.language, segment.content)
                        layoutCodeBlocks.addView(codeView)
                    } else {
                        val textView = TextView(context).apply {
                            textSize = 14.5f
                            setTextColor(Color.parseColor("#1E293B"))
                            setLineSpacing(4f, 1.15f)
                            setTextIsSelectable(true)
                            text = CodeFormatterUtil.formatMarkdown(context, segment.content)
                            setPadding(0, 4, 0, 4)
                        }
                        layoutCodeBlocks.addView(textView)
                    }
                }
            }

            btnCopy.setOnClickListener {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("ChatAiMessage", message.content)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Đã sao chép tin nhắn!", Toast.LENGTH_SHORT).show()
            }

            btnSpeak.setOnClickListener {
                listener.onSpeakText(message.content)
            }

            btnRegenerate.setOnClickListener {
                listener.onRegenerate(message)
            }
        }

        private fun createOpenCodeView(context: Context, language: String, code: String): View {
            val card = CardView(context).apply {
                radius = 16f
                cardElevation = 4f
                setCardBackgroundColor(Color.parseColor("#0F172A"))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 10, 0, 10)
                }
                layoutParams = params
            }

            val rootLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Header bar
            val headerBar = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.parseColor("#1E293B"))
                setPadding(16, 10, 16, 10)
            }

            val tvLang = TextView(context).apply {
                text = language
                setTextColor(Color.parseColor("#94A3B8"))
                textSize = 12f
                typeface = Typeface.MONOSPACE
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvCopy = TextView(context).apply {
                text = "Sao chép"
                setTextColor(Color.parseColor("#38BDF8"))
                textSize = 12f
                setPadding(12, 4, 12, 4)
                setBackgroundResource(android.R.drawable.list_selector_background)
                setOnClickListener {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Code", code)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Đã chép đoạn mã!", Toast.LENGTH_SHORT).show()
                }
            }

            headerBar.addView(tvLang)
            headerBar.addView(tvCopy)
            rootLayout.addView(headerBar)

            // Code content with horizontal scroll
            val hScroll = HorizontalScrollView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tvCode = TextView(context).apply {
                text = code
                setTextColor(Color.parseColor("#E2E8F0"))
                textSize = 13f
                typeface = Typeface.MONOSPACE
                setTextIsSelectable(true)
                setPadding(20, 16, 20, 20)
            }

            hScroll.addView(tvCode)
            rootLayout.addView(hScroll)
            card.addView(rootLayout)

            return card
        }
    }

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_ASSISTANT = 2
    }
}
