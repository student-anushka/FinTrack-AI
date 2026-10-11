import { useCallback, useEffect, useState } from "react";
import goalApi from "../../api/goalApi";

const initialForm = {
    name: "",
    targetAmount: "",
    targetDate: "",
    description: "",
};

const initialContribution = {
    amount: "",
    contributionDate: new Date().toISOString().slice(0, 10),
    note: "",
};

const styles = {
    page: {
        padding: "28px",
        maxWidth: "1400px",
        margin: "0 auto",
        color: "var(--text-primary, #172033)",
    },
    header: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        flexWrap: "wrap",
        gap: "16px",
        marginBottom: "24px",
    },
    title: { margin: 0, fontSize: "28px", fontWeight: 750 },
    muted: { color: "#64748b", fontSize: "14px" },
    button: {
        border: "none",
        borderRadius: "9px",
        padding: "10px 15px",
        fontWeight: 650,
        cursor: "pointer",
        background: "#4f46e5",
        color: "#fff",
    },
    secondaryButton: {
        border: "1px solid #dbe2ea",
        borderRadius: "8px",
        padding: "8px 12px",
        background: "#fff",
        color: "#334155",
        cursor: "pointer",
        fontWeight: 600,
    },
    dangerButton: {
        border: "1px solid #fecaca",
        borderRadius: "8px",
        padding: "8px 12px",
        background: "#fff",
        color: "#b91c1c",
        cursor: "pointer",
        fontWeight: 600,
    },
    stats: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(190px, 1fr))",
        gap: "16px",
        marginBottom: "24px",
    },
    statCard: {
        background: "#fff",
        border: "1px solid #e8edf4",
        borderRadius: "14px",
        padding: "20px",
        boxShadow: "0 3px 12px rgba(15, 23, 42, 0.035)",
    },
    grid: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))",
        gap: "18px",
    },
    goalCard: {
        background: "#fff",
        border: "1px solid #e8edf4",
        borderRadius: "14px",
        padding: "20px",
        boxShadow: "0 3px 12px rgba(15, 23, 42, 0.035)",
        minWidth: 0,
    },
    input: {
        width: "100%",
        boxSizing: "border-box",
        padding: "10px 12px",
        border: "1px solid #cbd5e1",
        borderRadius: "8px",
        font: "inherit",
        background: "#fff",
        color: "#172033",
    },
    label: {
        display: "block",
        fontWeight: 600,
        fontSize: "13px",
        marginBottom: "6px",
    },
    field: { marginBottom: "14px" },
    overlay: {
        position: "fixed",
        inset: 0,
        background: "rgba(15, 23, 42, 0.52)",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "18px",
        zIndex: 1000,
        overflowY: "auto",
    },
    modal: {
        width: "100%",
        maxWidth: "560px",
        maxHeight: "90vh",
        overflowY: "auto",
        background: "#fff",
        borderRadius: "16px",
        padding: "24px",
        boxSizing: "border-box",
    },
    alert: {
        padding: "12px 14px",
        background: "#fff1f2",
        color: "#9f1239",
        border: "1px solid #fecdd3",
        borderRadius: "9px",
        marginBottom: "16px",
        fontSize: "14px",
    },
    success: {
        padding: "12px 14px",
        background: "#ecfdf5",
        color: "#047857",
        border: "1px solid #a7f3d0",
        borderRadius: "9px",
        marginBottom: "16px",
        fontSize: "14px",
    },
};

function formatCurrency(value) {
    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2,
    }).format(Number(value) || 0);
}

function formatDate(value) {
    if (!value) return "No deadline";
    return new Date(`${value}T00:00:00`).toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
    });
}

function getErrorMessage(error) {
    const data = error?.response?.data;

    if (typeof data === "string" && data.trim()) return data;
    if (data?.message) return data.message;
    if (data?.error) return data.error;

    if (error?.response?.status === 401) {
        return "Your session may have expired. Please log in again.";
    }
    if (error?.response?.status === 403) {
        return "You don't have permission to perform this action.";
    }
    if (!error?.response) {
        return "Cannot reach the backend. Check that Spring Boot is running.";
    }

    return "Something went wrong. Please try again.";
}

function getProgress(goal) {
    const supplied = Number(goal.percentageCompleted);
    if (Number.isFinite(supplied)) {
        return Math.min(100, Math.max(0, supplied));
    }

    const target = Number(goal.targetAmount);
    const current = Number(goal.currentAmount);
    if (target <= 0) return 0;

    return Math.min(100, Math.max(0, (current / target) * 100));
}

function getStatus(goal) {
    const progress = getProgress(goal);

    if (progress >= 100) return { label: "Completed", color: "#047857", bg: "#d1fae5" };

    if (goal.targetDate) {
        const deadline = new Date(`${goal.targetDate}T23:59:59`);
        if (deadline < new Date()) {
            return { label: "Overdue", color: "#b91c1c", bg: "#fee2e2" };
        }
    }

    return { label: "In progress", color: "#4338ca", bg: "#e0e7ff" };
}

function GoalForm({ initialValues, onSubmit, onCancel, busy }) {
    const [form, setForm] = useState(initialValues);
    const [validationError, setValidationError] = useState("");

    function changeField(event) {
        const { name, value } = event.target;
        setForm((previous) => ({ ...previous, [name]: value }));
    }

    function handleSubmit(event) {
        event.preventDefault();
        setValidationError("");

        if (!form.name.trim()) {
            setValidationError("Please enter a goal name.");
            return;
        }
        if (!form.targetAmount || Number(form.targetAmount) <= 0) {
            setValidationError("Target amount must be greater than zero.");
            return;
        }

        onSubmit({
            name: form.name.trim(),
            targetAmount: Number(form.targetAmount),
            targetDate: form.targetDate || null,
            description: form.description.trim(),
        });
    }

    return (
        <form onSubmit={handleSubmit}>
            {validationError && <div style={styles.alert}>{validationError}</div>}

            <div style={styles.field}>
                <label style={styles.label} htmlFor="goal-name">Goal name *</label>
                <input
                    id="goal-name"
                    name="name"
                    value={form.name}
                    onChange={changeField}
                    placeholder="e.g. Emergency fund"
                    maxLength={100}
                    required
                    style={styles.input}
                />
            </div>

            <div style={styles.field}>
                <label style={styles.label} htmlFor="goal-target">Target amount (₹) *</label>
                <input
                    id="goal-target"
                    name="targetAmount"
                    type="number"
                    min="0.01"
                    step="0.01"
                    value={form.targetAmount}
                    onChange={changeField}
                    placeholder="50000"
                    required
                    style={styles.input}
                />
            </div>

            <div style={styles.field}>
                <label style={styles.label} htmlFor="goal-date">Target date</label>
                <input
                    id="goal-date"
                    name="targetDate"
                    type="date"
                    value={form.targetDate || ""}
                    onChange={changeField}
                    style={styles.input}
                />
            </div>

            <div style={styles.field}>
                <label style={styles.label} htmlFor="goal-description">Description</label>
                <textarea
                    id="goal-description"
                    name="description"
                    value={form.description}
                    onChange={changeField}
                    placeholder="Why is this goal important to you?"
                    rows={3}
                    maxLength={500}
                    style={{ ...styles.input, resize: "vertical" }}
                />
            </div>

            <div style={{ display: "flex", gap: "10px", justifyContent: "flex-end" }}>
                <button type="button" onClick={onCancel} style={styles.secondaryButton}>
                    Cancel
                </button>
                <button type="submit" disabled={busy} style={styles.button}>
                    {busy ? "Saving..." : "Save goal"}
                </button>
            </div>
        </form>
    );
}

function ContributionForm({ onSubmit, onCancel, busy, goal }) {
    const [form, setForm] = useState(initialContribution);
    const [validationError, setValidationError] = useState("");

    function handleSubmit(event) {
        event.preventDefault();
        setValidationError("");

        if (!form.amount || Number(form.amount) <= 0) {
            setValidationError("Contribution must be greater than zero.");
            return;
        }

        const remaining =
            Number(goal.targetAmount || 0) - Number(goal.currentAmount || 0);

        if (Number(form.amount) > remaining) {
            setValidationError(
                `You can contribute up to ${formatCurrency(remaining)} for this goal.`
            );
            return;
        }

        if (!form.contributionDate) {
            setValidationError("Please select a contribution date.");
            return;
        }

        if (form.contributionDate > new Date().toISOString().slice(0, 10)) {
            setValidationError("Contribution date cannot be in the future.");
            return;
        }

        onSubmit({
            amount: Number(form.amount),
            contributionDate: form.contributionDate,
            note: form.note.trim(),
        });
    }

    return (
        <form onSubmit={handleSubmit}>
            <p style={styles.muted}>
                Remaining to target:{" "}
                <strong>
                    {formatCurrency(
                        Number(goal.targetAmount || 0) - Number(goal.currentAmount || 0)
                    )}
                </strong>
            </p>

            {validationError && <div style={styles.alert}>{validationError}</div>}

            <div style={styles.field}>
                <label style={styles.label} htmlFor="contribution-amount">Amount (₹) *</label>
                <input
                    id="contribution-amount"
                    type="number"
                    min="0.01"
                    max={Math.max(
                        0,
                        Number(goal.targetAmount || 0) - Number(goal.currentAmount || 0)
                    )}
                    step="0.01"
                    value={form.amount}
                    onChange={(event) =>
                        setForm((previous) => ({ ...previous, amount: event.target.value }))
                    }
                    required
                    style={styles.input}
                />
            </div>

            <div style={styles.field}>
                <label style={styles.label} htmlFor="contribution-date">Contribution date *</label>
                <input
                    id="contribution-date"
                    type="date"
                    max={new Date().toISOString().slice(0, 10)}
                    value={form.contributionDate}
                    onChange={(event) =>
                        setForm((previous) => ({
                            ...previous,
                            contributionDate: event.target.value,
                        }))
                    }
                    required
                    style={styles.input}
                />
            </div>

            <div style={styles.field}>
                <label style={styles.label} htmlFor="contribution-note">Note</label>
                <input
                    id="contribution-note"
                    value={form.note}
                    maxLength={250}
                    onChange={(event) =>
                        setForm((previous) => ({ ...previous, note: event.target.value }))
                    }
                    placeholder="e.g. Monthly savings"
                    style={styles.input}
                />
            </div>

            <div style={{ display: "flex", gap: "10px", justifyContent: "flex-end" }}>
                <button type="button" onClick={onCancel} style={styles.secondaryButton}>
                    Cancel
                </button>
                <button type="submit" disabled={busy} style={styles.button}>
                    {busy ? "Adding..." : "Add contribution"}
                </button>
            </div>
        </form>
    );
}

function ContributionHistory({ goal, onClose }) {
    const [page, setPage] = useState(0);
    const [history, setHistory] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const loadHistory = useCallback(async (signal) => {
        setLoading(true);
        setError("");

        try {
            const response = await goalApi.getContributions(
                goal.id,
                { page, size: 5, sortBy: "contributionDate", direction: "desc" },
                { signal }
            );
            setHistory(response);
        } catch (requestError) {
            if (requestError.code !== "ERR_CANCELED") {
                setError(getErrorMessage(requestError));
            }
        } finally {
            if (!signal?.aborted) setLoading(false);
        }
    }, [goal.id, page]);

    useEffect(() => {
        const controller = new AbortController();

        async function fetchHistory() {
            await loadHistory(controller.signal);
        }

        fetchHistory();

        return () => controller.abort();
    }, [loadHistory]);

    const contributions = history?.contributions ?? [];
    const totalPages = Number(history?.totalPages ?? 0);

    return (
        <div style={styles.overlay} role="presentation">
            <section
                style={styles.modal}
                role="dialog"
                aria-modal="true"
                aria-labelledby="history-title"
            >
                <div style={{ display: "flex", justifyContent: "space-between", gap: "12px" }}>
                    <div>
                        <h2 id="history-title" style={{ margin: "0 0 6px" }}>Contribution history</h2>
                        <p style={styles.muted}>{goal.name}</p>
                    </div>
                    <button type="button" onClick={onClose} style={styles.secondaryButton}>
                        Close
                    </button>
                </div>

                {error && <div style={styles.alert}>{error}</div>}
                {loading && <p>Loading contribution history...</p>}

                {!loading && !error && contributions.length === 0 && (
                    <p style={styles.muted}>No contributions recorded yet.</p>
                )}

                {!loading && contributions.map((item) => (
                    <div
                        key={item.id}
                        style={{
                            padding: "14px 0",
                            borderBottom: "1px solid #e8edf4",
                            display: "flex",
                            justifyContent: "space-between",
                            gap: "12px",
                        }}
                    >
                        <div>
                            <strong>{item.note || "Savings contribution"}</strong>
                            <div style={styles.muted}>{formatDate(item.contributionDate)}</div>
                        </div>
                        <strong style={{ color: "#047857", whiteSpace: "nowrap" }}>
                            +{formatCurrency(item.amount)}
                        </strong>
                    </div>
                ))}

                {!loading && totalPages > 1 && (
                    <div style={{ display: "flex", justifyContent: "space-between", marginTop: "16px" }}>
                        <button
                            type="button"
                            disabled={page === 0}
                            onClick={() => setPage((previous) => Math.max(0, previous - 1))}
                            style={styles.secondaryButton}
                        >
                            Previous
                        </button>
                        <span style={styles.muted}>
                            Page {page + 1} of {totalPages}
                        </span>
                        <button
                            type="button"
                            disabled={page + 1 >= totalPages}
                            onClick={() => setPage((previous) => previous + 1)}
                            style={styles.secondaryButton}
                        >
                            Next
                        </button>
                    </div>
                )}
            </section>
        </div>
    );
}

export default function Goals() {
    const [goals, setGoals] = useState([]);
    const [loading, setLoading] = useState(true);
    const [pageError, setPageError] = useState("");
    const [notice, setNotice] = useState("");
    const [modal, setModal] = useState(null);
    const [busy, setBusy] = useState(false);

    const loadGoals = useCallback(async (signal) => {
        setLoading(true);
        setPageError("");

        try {
            const response = await goalApi.getGoals({ signal });
            setGoals(Array.isArray(response) ? response : []);
        } catch (error) {
            if (error.code !== "ERR_CANCELED") {
                setPageError(getErrorMessage(error));
            }
        } finally {
            if (!signal?.aborted) setLoading(false);
        }
    }, []);

    useEffect(() => {
        const controller = new AbortController();

        async function fetchGoals() {
            await loadGoals(controller.signal);
        }

        fetchGoals();

        return () => controller.abort();
    }, [loadGoals]);

    const totalTarget = goals.reduce(
        (sum, goal) => sum + Number(goal.targetAmount || 0),
        0
    );
    const totalSaved = goals.reduce(
        (sum, goal) => sum + Number(goal.currentAmount || 0),
        0
    );
    const completedCount = goals.filter((goal) => getProgress(goal) >= 100).length;
    const remaining = Math.max(0, totalTarget - totalSaved);

    function openCreate() {
        setNotice("");
        setModal({ type: "create" });
    }

    function openEdit(goal) {
        setNotice("");
        setModal({
            type: "edit",
            goal,
            initialValues: {
                name: goal.name ?? "",
                targetAmount: String(goal.targetAmount ?? ""),
                targetDate: goal.targetDate ?? "",
                description: goal.description ?? "",
            },
        });
    }

    async function handleSaveGoal(formData) {
        setBusy(true);
        setNotice("");
        setPageError("");

        try {
            if (modal.type === "edit") {
                await goalApi.updateGoal(modal.goal.id, formData);
                setNotice("Goal updated successfully.");
            } else {
                await goalApi.createGoal(formData);
                setNotice("Goal created successfully.");
            }

            setModal(null);
            await loadGoals();
        } catch (error) {
            setPageError(getErrorMessage(error));
        } finally {
            setBusy(false);
        }
    }

    async function handleDelete(goal) {
        const confirmed = window.confirm(
            `Delete "${goal.name}"? Its contribution history may also be deleted.`
        );
        if (!confirmed) return;

        setNotice("");
        setPageError("");

        try {
            await goalApi.deleteGoal(goal.id);
            setGoals((previous) => previous.filter((item) => item.id !== goal.id));
            setNotice("Goal deleted successfully.");
        } catch (error) {
            setPageError(getErrorMessage(error));
        }
    }

    async function handleAddContribution(formData) {
        setBusy(true);
        setNotice("");
        setPageError("");

        try {
            const updatedGoal = await goalApi.addContribution(modal.goal.id, formData);

            setGoals((previous) =>
                previous.map((goal) =>
                    goal.id === modal.goal.id
                        ? { ...goal, ...updatedGoal }
                        : goal
                )
            );
            setModal(null);
            setNotice("Contribution added successfully.");
            await loadGoals();
        } catch (error) {
            setPageError(getErrorMessage(error));
        } finally {
            setBusy(false);
        }
    }

    return (
        <main style={styles.page}>
            <header style={styles.header}>
                <div>
                    <h1 style={styles.title}>Financial Goals</h1>
                    <p style={styles.muted}>
                        Turn your savings plans into achievable milestones.
                    </p>
                </div>
                <button type="button" onClick={openCreate} style={styles.button}>
                    + Create goal
                </button>
            </header>

            {pageError && (
                <div role="alert" style={styles.alert}>
                    {pageError}
                    <button
                        type="button"
                        onClick={() => loadGoals()}
                        style={{ ...styles.secondaryButton, marginLeft: "12px" }}
                    >
                        Retry
                    </button>
                </div>
            )}

            {notice && (
                <div role="status" style={styles.success}>
                    {notice}
                    <button
                        type="button"
                        onClick={() => setNotice("")}
                        style={{ ...styles.secondaryButton, marginLeft: "12px" }}
                    >
                        Dismiss
                    </button>
                </div>
            )}

            <section style={styles.stats} aria-label="Goal summary">
                <div style={styles.statCard}>
                    <div style={styles.muted}>Total target</div>
                    <h2 style={{ margin: "10px 0 0", fontSize: "23px" }}>
                        {formatCurrency(totalTarget)}
                    </h2>
                </div>
                <div style={styles.statCard}>
                    <div style={styles.muted}>Total saved</div>
                    <h2 style={{ margin: "10px 0 0", fontSize: "23px", color: "#047857" }}>
                        {formatCurrency(totalSaved)}
                    </h2>
                </div>
                <div style={styles.statCard}>
                    <div style={styles.muted}>Remaining across goals</div>
                    <h2 style={{ margin: "10px 0 0", fontSize: "23px" }}>
                        {formatCurrency(remaining)}
                    </h2>
                </div>
                <div style={styles.statCard}>
                    <div style={styles.muted}>Goals completed</div>
                    <h2 style={{ margin: "10px 0 0", fontSize: "23px" }}>
                        {completedCount} / {goals.length}
                    </h2>
                </div>
            </section>

            {loading ? (
                <p style={styles.muted}>Loading your goals...</p>
            ) : goals.length === 0 && !pageError ? (
                <section style={{ ...styles.goalCard, textAlign: "center", padding: "48px 20px" }}>
                    <div style={{ fontSize: "38px" }}>🎯</div>
                    <h2 style={{ marginBottom: "8px" }}>Your first goal starts here</h2>
                    <p style={styles.muted}>
                        Set a target, record contributions, and track your progress.
                    </p>
                    <button type="button" onClick={openCreate} style={styles.button}>
                        Create your first goal
                    </button>
                </section>
            ) : (
                <section style={styles.grid} aria-label="Your financial goals">
                    {goals.map((goal) => {
                        const progress = getProgress(goal);
                        const current = Number(goal.currentAmount || 0);
                        const target = Number(goal.targetAmount || 0);
                        const status = getStatus(goal);
                        const goalRemaining = Math.max(0, target - current);

                        return (
                            <article key={goal.id} style={styles.goalCard}>
                                <div style={{ display: "flex", justifyContent: "space-between", gap: "12px" }}>
                                    <div style={{ minWidth: 0 }}>
                                        <h2 style={{ margin: "0 0 7px", fontSize: "19px", overflowWrap: "anywhere" }}>
                                            {goal.name}
                                        </h2>
                                        <p style={{ ...styles.muted, margin: 0 }}>
                                            Target date: {formatDate(goal.targetDate)}
                                        </p>
                                    </div>
                                    <span
                                        style={{
                                            flexShrink: 0,
                                            alignSelf: "flex-start",
                                            borderRadius: "20px",
                                            padding: "5px 9px",
                                            fontSize: "12px",
                                            fontWeight: 700,
                                            color: status.color,
                                            background: status.bg,
                                        }}
                                    >
                                        {status.label}
                                    </span>
                                </div>

                                {goal.description && (
                                    <p style={{ color: "#475569", fontSize: "14px", lineHeight: 1.5 }}>
                                        {goal.description}
                                    </p>
                                )}

                                <div style={{ display: "flex", justifyContent: "space-between", gap: "12px", marginTop: "22px" }}>
                                    <div>
                                        <div style={styles.muted}>Saved so far</div>
                                        <strong style={{ fontSize: "20px", color: "#047857" }}>
                                            {formatCurrency(current)}
                                        </strong>
                                    </div>
                                    <div style={{ textAlign: "right" }}>
                                        <div style={styles.muted}>Target</div>
                                        <strong style={{ fontSize: "20px" }}>
                                            {formatCurrency(target)}
                                        </strong>
                                    </div>
                                </div>

                                <div
                                    style={{
                                        height: "9px",
                                        background: "#e2e8f0",
                                        borderRadius: "99px",
                                        overflow: "hidden",
                                        marginTop: "16px",
                                    }}
                                    role="progressbar"
                                    aria-label={`${goal.name} completion`}
                                    aria-valuenow={Math.round(progress)}
                                    aria-valuemin={0}
                                    aria-valuemax={100}
                                >
                                    <div
                                        style={{
                                            height: "100%",
                                            width: `${progress}%`,
                                            background: progress >= 100 ? "#059669" : "#4f46e5",
                                            borderRadius: "99px",
                                            transition: "width 250ms ease",
                                        }}
                                    />
                                </div>

                                <div style={{ display: "flex", justifyContent: "space-between", marginTop: "8px", fontSize: "13px" }}>
                                    <span style={styles.muted}>{progress.toFixed(1)}% complete</span>
                                    <span style={styles.muted}>{formatCurrency(goalRemaining)} to go</span>
                                </div>

                                <div style={{ display: "flex", gap: "8px", flexWrap: "wrap", marginTop: "20px" }}>
                                    {goalRemaining > 0 && (
                                        <button
                                            type="button"
                                            onClick={() => {
                                                setNotice("");
                                                setModal({ type: "contribution", goal });
                                            }}
                                            style={styles.button}
                                        >
                                            + Add savings
                                        </button>
                                    )}
                                    <button
                                        type="button"
                                        onClick={() => setModal({ type: "history", goal })}
                                        style={styles.secondaryButton}
                                    >
                                        History
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() => openEdit(goal)}
                                        style={styles.secondaryButton}
                                    >
                                        Edit
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() => handleDelete(goal)}
                                        style={styles.dangerButton}
                                    >
                                        Delete
                                    </button>
                                </div>
                            </article>
                        );
                    })}
                </section>
            )}

            {modal && (modal.type === "create" || modal.type === "edit") && (
                <div style={styles.overlay} role="presentation">
                    <section
                        style={styles.modal}
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="goal-form-title"
                    >
                        <h2 id="goal-form-title" style={{ marginTop: 0 }}>
                            {modal.type === "edit" ? "Edit goal" : "Create a financial goal"}
                        </h2>
                        <p style={styles.muted}>
                            Choose a realistic target and a date that works for you.
                        </p>
                        <GoalForm
                            key={`${modal.type}-${modal.goal?.id ?? "new"}`}
                            initialValues={modal.initialValues ?? initialForm}
                            onSubmit={handleSaveGoal}
                            onCancel={() => setModal(null)}
                            busy={busy}
                        />
                    </section>
                </div>
            )}

            {modal?.type === "contribution" && (
                <div style={styles.overlay} role="presentation">
                    <section
                        style={styles.modal}
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="contribution-title"
                    >
                        <h2 id="contribution-title" style={{ marginTop: 0 }}>
                            Add savings
                        </h2>
                        <p style={styles.muted}>Goal: {modal.goal.name}</p>
                        <ContributionForm
                            key={modal.goal.id}
                            goal={modal.goal}
                            onSubmit={handleAddContribution}
                            onCancel={() => setModal(null)}
                            busy={busy}
                        />
                    </section>
                </div>
            )}

            {modal?.type === "history" && (
                <ContributionHistory
                    goal={modal.goal}
                    onClose={() => setModal(null)}
                />
            )}
        </main>
    );
}