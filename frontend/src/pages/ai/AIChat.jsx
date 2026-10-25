
import { useEffect, useRef, useState } from "react";
import aiApi from "../../api/aiApi";
import "./AIChat.css";

const suggestions = [
    "How can I save more money?",
    "How should I manage my monthly budget?",
    "How can I reduce unnecessary expenses?",
    "Explain the 50/30/20 budgeting rule.",
];

function AIChat() {
    const [messages, setMessages] = useState([
        {
            role: "assistant",
            content:
                "Hi! I'm your FinTrack AI Finance Assistant. I can help you understand budgeting, saving, expenses and personal finance. What would you like to know?",
        },
    ]);

    const [question, setQuestion] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const messagesEndRef = useRef(null);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({
            behavior: "smooth",
        });
    }, [messages, loading]);

    const sendMessage = async (text = question) => {
        const userQuestion = text.trim();

        if (!userQuestion || loading) return;

        setError("");
        setQuestion("");

        setMessages((previous) => [
            ...previous,
            {
                role: "user",
                content: userQuestion,
            },
        ]);

        setLoading(true);

        try {
            const response = await aiApi.askQuestion(userQuestion);

            setMessages((previous) => [
                ...previous,
                {
                    role: "assistant",
                    content: response.answer,
                },
            ]);
        } catch (err) {
            const errorMessage =
                err.response?.data?.message ||
                "Unable to get a response. Please try again.";

            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        sendMessage();
    };

    return (
        <div className="ai-chat-page">
            <div className="ai-chat-header">
                <div>
                    <div className="ai-heading">
                        <div className="ai-icon">✦</div>
                        <div>
                            <h1>FinTrack AI</h1>
                            <p>Your personal finance assistant</p>
                        </div>
                    </div>
                </div>

                <span className="ai-status">
                    <span className="status-dot"></span>
                    AI Assistant
                </span>
            </div>

            <div className="ai-chat-container">
                <div className="ai-messages">
                    {messages.length === 1 && (
                        <div className="ai-welcome">
                            <h2>How can I help you today?</h2>
                            <p>
                                Ask me anything about budgeting, saving and
                                managing your money.
                            </p>

                            <div className="ai-suggestions">
                                {suggestions.map((suggestion) => (
                                    <button
                                        key={suggestion}
                                        type="button"
                                        onClick={() => sendMessage(suggestion)}
                                        disabled={loading}
                                    >
                                        {suggestion}
                                        <span>↗</span>
                                    </button>
                                ))}
                            </div>
                        </div>
                    )}

                    {messages.map((message, index) => (
                        <div
                            key={index}
                            className={`ai-message ${message.role === "user"
                                    ? "user-message"
                                    : "assistant-message"
                                }`}
                        >
                            <div className="message-avatar">
                                {message.role === "user" ? "You" : "✦"}
                            </div>

                            <div className="message-content">
                                <span className="message-role">
                                    {message.role === "user"
                                        ? "You"
                                        : "FinTrack AI"}
                                </span>

                                <p>{message.content}</p>
                            </div>
                        </div>
                    ))}

                    {loading && (
                        <div className="ai-message assistant-message">
                            <div className="message-avatar">✦</div>
                            <div className="message-content">
                                <span className="message-role">FinTrack AI</span>
                                <p className="typing-indicator">
                                    Thinking...
                                </p>
                            </div>
                        </div>
                    )}

                    {error && (
                        <div className="ai-error" role="alert">
                            {error}
                        </div>
                    )}

                    <div ref={messagesEndRef} />
                </div>

                <form className="ai-input-area" onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Ask FinTrack AI anything..."
                        value={question}
                        onChange={(event) => setQuestion(event.target.value)}
                        maxLength={2000}
                        disabled={loading}
                    />

                    <button
                        type="submit"
                        disabled={loading || !question.trim()}
                    >
                        {loading ? "..." : "Send ↑"}
                    </button>
                </form>

                <p className="ai-disclaimer">
                    AI responses may not always be accurate. Verify important
                    financial decisions independently.
                </p>
            </div>
        </div>
    );
}

export default AIChat;
