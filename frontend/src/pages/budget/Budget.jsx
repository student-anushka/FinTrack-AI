
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import budgetApi from "../../api/budgetApi";
import categoryApi from "../../api/categoryApi";

const PAGE_SIZE = 10;

function todayLocal() {
    const date = new Date();
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
    return date.toISOString().slice(0, 10);
}

const emptyForm = () => ({
    name: "",
    amount: "",
    startDate: todayLocal(),
    endDate: todayLocal(),
    categoryId: "",
});

const initialFilters = {
    search: "",
    categoryId: "",
    startDate: "",
    endDate: "",
};

function formatCurrency(value) {
    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2,
    }).format(Number(value) || 0);
}

function errorMessage(error) {
    return (
        error?.response?.data?.message ||
        error?.response?.data?.error ||
        "Something went wrong. Please try again."
    );
}

function unwrapData(data) {
    return data?.data ?? data;
}

function normalizePage(data) {
    const result = unwrapData(data);

    return {
        budgets: Array.isArray(result?.budgets) ? result.budgets : [],
        currentPage: Number(result?.currentPage ?? 0),
        totalPages: Number(result?.totalPages ?? 0),
        totalElements: Number(result?.totalElements ?? 0),
    };
}

function normalizeCategories(data) {
    const result = unwrapData(data);

    if (Array.isArray(result)) return result;
    if (Array.isArray(result?.categories)) return result.categories;

    return [];
}

const styles = {
    page: { maxWidth: 1280, margin: "0 auto", padding: 8, color: "#17211e" },
    header: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        flexWrap: "wrap",
        gap: 16,
        marginBottom: 24,
    },
    card: {
        background: "#fff",
        border: "1px solid #dde5e2",
        borderRadius: 16,
        padding: 22,
        marginBottom: 20,
        boxShadow: "0 8px 30px rgba(15,35,31,.04)",
    },
    grid: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit,minmax(190px,1fr))",
        gap: 16,
    },
    field: { display: "grid", gap: 7, marginBottom: 14, color: "#46554f" },
    input: {
        width: "100%",
        boxSizing: "border-box",
        padding: "11px 12px",
        border: "1px solid #cbd5e1",
        borderRadius: 8,
        background: "#fff",
        color: "#17211e",
        minHeight: 42,
    },
    button: {
        padding: "10px 15px",
        border: "1px solid #cbd5e1",
        borderRadius: 8,
        background: "#fff",
        color: "#17211e",
        cursor: "pointer",
    },
    primary: {
        padding: "11px 16px",
        border: 0,
        borderRadius: 8,
        background: "#176f62",
        color: "#fff",
        cursor: "pointer",
        fontWeight: 700,
    },
    tableWrap: { width: "100%", overflowX: "auto" },
    table: { width: "100%", borderCollapse: "collapse", textAlign: "left" },
    cell: {
        padding: "14px 12px",
        borderBottom: "1px solid #edf1ef",
        whiteSpace: "nowrap",
        verticalAlign: "top",
    },
    error: {
        padding: 13,
        borderRadius: 8,
        background: "#fff1f2",
        color: "#991b1b",
        marginBottom: 16,
    },
    notice: {
        padding: 13,
        borderRadius: 8,
        background: "#ecfdf5",
        color: "#065f46",
        marginBottom: 16,
    },
};

function statusColor(status) {
    switch (status) {
        case "EXCEEDED":
            return { background: "#fee2e2", color: "#991b1b" };
        case "ON_TRACK":
            return { background: "#dcfce7", color: "#166534" };
        case "UPCOMING":
            return { background: "#dbeafe", color: "#1d4ed8" };
        case "COMPLETED":
            return { background: "#f1f5f9", color: "#475569" };
        default:
            return { background: "#f3f4f6", color: "#374151" };
    }
}

export default function Budget() {
    const navigate = useNavigate();

    const [budgets, setBudgets] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [categoriesLoading, setCategoriesLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [notice, setNotice] = useState("");
    const [formError, setFormError] = useState("");

    const [formOpen, setFormOpen] = useState(false);
    const [editingBudget, setEditingBudget] = useState(null);
    const [form, setForm] = useState(emptyForm);

    const [filters, setFilters] = useState(initialFilters);
    const [appliedFilters, setAppliedFilters] = useState({});
    const [page, setPage] = useState(0);
    const [pageInfo, setPageInfo] = useState({
        currentPage: 0,
        totalPages: 0,
        totalElements: 0,
    });
    const [refreshKey, setRefreshKey] = useState(0);

    useEffect(() => {
        const controller = new AbortController();
        let active = true;

        async function fetchCategories() {
            try {
                const data = await categoryApi.getCategories({
                    signal: controller.signal,
                });

                if (active) setCategories(normalizeCategories(data));
            } catch (err) {
                if (
                    !active ||
                    err?.name === "CanceledError" ||
                    err?.name === "AbortError" ||
                    err?.code === "ERR_CANCELED"
                ) return;

                if ([401, 403].includes(err?.response?.status)) {
                    localStorage.removeItem("jwt_token");
                    navigate("/login", { replace: true });
                    return;
                }

                setError(`Unable to load categories: ${errorMessage(err)}`);
            } finally {
                if (active) setCategoriesLoading(false);
            }
        }

        void fetchCategories();

        return () => {
            active = false;
            controller.abort();
        };
    }, [navigate]);

    useEffect(() => {
        const controller = new AbortController();
        let active = true;

        async function fetchBudgets() {
            try {
                const data = await budgetApi.getBudgets(
                    {
                        page,
                        size: PAGE_SIZE,
                        sortBy: "startDate",
                        direction: "desc",
                        ...appliedFilters,
                    },
                    { signal: controller.signal },
                );

                if (!active) return;

                const result = normalizePage(data);
                setBudgets(result.budgets);
                setPageInfo({
                    currentPage: result.currentPage,
                    totalPages: result.totalPages,
                    totalElements: result.totalElements,
                });
                setError("");
            } catch (err) {
                if (
                    !active ||
                    err?.name === "CanceledError" ||
                    err?.name === "AbortError" ||
                    err?.code === "ERR_CANCELED"
                ) return;

                if ([401, 403].includes(err?.response?.status)) {
                    localStorage.removeItem("jwt_token");
                    navigate("/login", { replace: true });
                    return;
                }

                setError(errorMessage(err));
            } finally {
                if (active) setLoading(false);
            }
        }

        void fetchBudgets();

        return () => {
            active = false;
            controller.abort();
        };
    }, [page, appliedFilters, refreshKey, navigate]);

    function handleFilterChange(event) {
        const { name, value } = event.target;
        setFilters((current) => ({ ...current, [name]: value }));
    }

    function applyFilters(event) {
        event.preventDefault();

        if (
            filters.startDate &&
            filters.endDate &&
            filters.startDate > filters.endDate
        ) {
            setError("Start date cannot be after end date.");
            return;
        }

        const next = {};
        Object.entries(filters).forEach(([key, value]) => {
            if (value !== "") next[key] = value;
        });

        setError("");
        setNotice("");
        setLoading(true);
        setPage(0);
        setAppliedFilters(next);
    }

    function resetFilters() {
        setFilters(initialFilters);
        setAppliedFilters({});
        setPage(0);
        setLoading(true);
        setError("");
        setNotice("");
    }

    function openCreateForm() {
        setEditingBudget(null);
        setForm(emptyForm());
        setFormError("");
        setFormOpen(true);
        setNotice("");
    }

    function openEditForm(budget) {
        setEditingBudget(budget);
        setForm({
            name: budget.name ?? "",
            amount: String(budget.amount ?? ""),
            startDate: budget.startDate ?? "",
            endDate: budget.endDate ?? "",
            categoryId: String(budget.categoryId ?? ""),
        });
        setFormError("");
        setFormOpen(true);
        setNotice("");
    }

    function handleFormChange(event) {
        const { name, value } = event.target;
        setForm((current) => ({ ...current, [name]: value }));
    }

    async function submitBudget(event) {
        event.preventDefault();
        setFormError("");
        setError("");
        setNotice("");

        if (!form.name.trim()) {
            setFormError("Budget name is required.");
            return;
        }

        if (!Number.isFinite(Number(form.amount)) || Number(form.amount) <= 0) {
            setFormError("Budget amount must be greater than zero.");
            return;
        }

        if (!form.startDate || !form.endDate) {
            setFormError("Please select both start and end dates.");
            return;
        }

        if (form.startDate > form.endDate) {
            setFormError("Start date cannot be after end date.");
            return;
        }

        if (!form.categoryId) {
            setFormError("Please select a category.");
            return;
        }

        const payload = {
            name: form.name.trim(),
            amount: Number(form.amount),
            startDate: form.startDate,
            endDate: form.endDate,
            categoryId: Number(form.categoryId),
        };

        setSaving(true);

        try {
            if (editingBudget) {
                await budgetApi.updateBudget(editingBudget.id, payload);
                setNotice("Budget updated successfully.");
            } else {
                await budgetApi.createBudget(payload);
                setNotice("Budget created successfully.");
            }

            setFormOpen(false);
            setEditingBudget(null);
            setForm(emptyForm());
            setPage(0);
            setLoading(true);
            setRefreshKey((current) => current + 1);
        } catch (err) {
            if ([401, 403].includes(err?.response?.status)) {
                localStorage.removeItem("jwt_token");
                navigate("/login", { replace: true });
                return;
            }

            setFormError(errorMessage(err));
        } finally {
            setSaving(false);
        }
    }

    async function deleteBudget(budget) {
        const confirmed = window.confirm(
            `Delete budget "${budget.name}"? This cannot be undone.`,
        );

        if (!confirmed) return;

        setError("");
        setNotice("");

        try {
            await budgetApi.deleteBudget(budget.id);
            setNotice("Budget deleted successfully.");
            setLoading(true);

            if (budgets.length === 1 && page > 0) {
                setPage((current) => current - 1);
            } else {
                setRefreshKey((current) => current + 1);
            }
        } catch (err) {
            if ([401, 403].includes(err?.response?.status)) {
                localStorage.removeItem("jwt_token");
                navigate("/login", { replace: true });
                return;
            }

            setError(errorMessage(err));
        }
    }

    function changePage(nextPage) {
        if (
            nextPage < 0 ||
            (pageInfo.totalPages > 0 && nextPage >= pageInfo.totalPages)
        ) return;

        setLoading(true);
        setPage(nextPage);
    }

    return (
        <main style={styles.page}>
            <header style={styles.header}>
                <div>
                    <p style={{ color: "#176f62", fontWeight: 700, letterSpacing: 1 }}>
                        FINTRACK WORKSPACE
                    </p>
                    <h1 style={{ margin: "0 0 8px", fontSize: 36 }}>
                        Budget Management
                    </h1>
                    <p style={{ margin: 0, color: "#687773" }}>
                        Set spending limits and track how much of each budget you use.
                    </p>
                </div>

                <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                    <button
                        type="button"
                        style={styles.button}
                        onClick={() => navigate("/dashboard")}
                    >
                        Dashboard
                    </button>
                    <button
                        type="button"
                        style={styles.primary}
                        onClick={openCreateForm}
                    >
                        + Create budget
                    </button>
                </div>
            </header>

            {error && <div style={styles.error} role="alert">{error}</div>}
            {notice && <div style={styles.notice} role="status">{notice}</div>}

            {formOpen && (
                <section style={styles.card}>
                    <h2>{editingBudget ? "Edit budget" : "Create a budget"}</h2>

                    {formError && (
                        <div style={styles.error} role="alert">{formError}</div>
                    )}

                    <form onSubmit={submitBudget}>
                        <div style={styles.grid}>
                            <label style={styles.field}>
                                Budget name *
                                <input
                                    style={styles.input}
                                    name="name"
                                    value={form.name}
                                    onChange={handleFormChange}
                                    maxLength={150}
                                    placeholder="e.g. October groceries"
                                    required
                                />
                            </label>

                            <label style={styles.field}>
                                Budget amount (₹) *
                                <input
                                    style={styles.input}
                                    name="amount"
                                    type="number"
                                    min="0.01"
                                    step="0.01"
                                    value={form.amount}
                                    onChange={handleFormChange}
                                    required
                                />
                            </label>

                            <label style={styles.field}>
                                Category *
                                <select
                                    style={styles.input}
                                    name="categoryId"
                                    value={form.categoryId}
                                    onChange={handleFormChange}
                                    required
                                    disabled={categoriesLoading}
                                >
                                    <option value="">
                                        {categoriesLoading ? "Loading categories…" : "Select category"}
                                    </option>
                                    {categories.map((category) => (
                                        <option key={category.id} value={category.id}>
                                            {category.name}
                                        </option>
                                    ))}
                                </select>
                            </label>

                            <label style={styles.field}>
                                Start date *
                                <input
                                    style={styles.input}
                                    type="date"
                                    name="startDate"
                                    value={form.startDate}
                                    onChange={handleFormChange}
                                    required
                                />
                            </label>

                            <label style={styles.field}>
                                End date *
                                <input
                                    style={styles.input}
                                    type="date"
                                    name="endDate"
                                    min={form.startDate || undefined}
                                    value={form.endDate}
                                    onChange={handleFormChange}
                                    required
                                />
                            </label>
                        </div>

                        {categories.length === 0 && !categoriesLoading && (
                            <p style={{ color: "#991b1b" }}>
                                No categories were returned. Add or seed categories before creating a budget.
                            </p>
                        )}

                        <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                            <button
                                type="submit"
                                style={styles.primary}
                                disabled={saving || categoriesLoading || categories.length === 0}
                            >
                                {saving ? "Saving…" : editingBudget ? "Update budget" : "Save budget"}
                            </button>

                            <button
                                type="button"
                                style={styles.button}
                                disabled={saving}
                                onClick={() => {
                                    setFormOpen(false);
                                    setEditingBudget(null);
                                    setFormError("");
                                }}
                            >
                                Cancel
                            </button>
                        </div>
                    </form>

                    <p style={{ color: "#687773", fontSize: 13, marginBottom: 0 }}>
                        The backend rejects overlapping budgets for the same category and date period.
                    </p>
                </section>
            )}

            <section style={styles.card}>
                <h2>Search and filters</h2>

                <form onSubmit={applyFilters}>
                    <div style={styles.grid}>
                        <label style={styles.field}>
                            Search budget name
                            <input
                                style={styles.input}
                                name="search"
                                value={filters.search}
                                onChange={handleFilterChange}
                                placeholder="Search by name"
                            />
                        </label>

                        <label style={styles.field}>
                            Category
                            <select
                                style={styles.input}
                                name="categoryId"
                                value={filters.categoryId}
                                onChange={handleFilterChange}
                            >
                                <option value="">All categories</option>
                                {categories.map((category) => (
                                    <option key={category.id} value={category.id}>
                                        {category.name}
                                    </option>
                                ))}
                            </select>
                        </label>

                        <label style={styles.field}>
                            Start date (on or after)
                            <input
                                style={styles.input}
                                type="date"
                                name="startDate"
                                value={filters.startDate}
                                onChange={handleFilterChange}
                            />
                        </label>

                        <label style={styles.field}>
                            End date (on or before)
                            <input
                                style={styles.input}
                                type="date"
                                name="endDate"
                                value={filters.endDate}
                                onChange={handleFilterChange}
                            />
                        </label>
                    </div>

                    <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                        <button type="submit" style={styles.primary}>Apply filters</button>
                        <button type="button" style={styles.button} onClick={resetFilters}>
                            Reset filters
                        </button>
                    </div>
                </form>
            </section>

            <section style={styles.card}>
                <div
                    style={{
                        display: "flex",
                        justifyContent: "space-between",
                        gap: 12,
                        alignItems: "center",
                        flexWrap: "wrap",
                    }}
                >
                    <h2>Budgets</h2>
                    <strong>Total records: {pageInfo.totalElements}</strong>
                </div>

                {loading ? (
                    <p role="status">Loading budgets…</p>
                ) : budgets.length === 0 ? (
                    <p>No budgets found. Create a budget or adjust your filters.</p>
                ) : (
                    <div style={styles.tableWrap}>
                        <table style={styles.table}>
                            <thead>
                                <tr>
                                    {["Budget", "Category", "Limit", "Spent", "Remaining", "Period", "Status", "Actions"].map((heading) => (
                                        <th key={heading} style={styles.cell}>{heading}</th>
                                    ))}
                                </tr>
                            </thead>

                            <tbody>
                                {budgets.map((budget) => {
                                    const percentage = Math.max(
                                        0,
                                        Math.min(100, Number(budget.percentageUsed) || 0),
                                    );
                                    const statusStyle = statusColor(budget.status);

                                    return (
                                        <tr key={budget.id}>
                                            <td style={styles.cell}>
                                                <strong>{budget.name}</strong>
                                                <div
                                                    style={{
                                                        height: 7,
                                                        width: 110,
                                                        marginTop: 8,
                                                        borderRadius: 99,
                                                        overflow: "hidden",
                                                        background: "#e7eeeb",
                                                    }}
                                                >
                                                    <div
                                                        style={{
                                                            width: `${percentage}%`,
                                                            height: "100%",
                                                            background:
                                                                budget.status === "EXCEEDED" ? "#dc2626" : "#176f62",
                                                        }}
                                                    />
                                                </div>
                                                <small>{Number(budget.percentageUsed || 0).toFixed(1)}% used</small>
                                            </td>

                                            <td style={styles.cell}>{budget.categoryName || "—"}</td>
                                            <td style={styles.cell}>{formatCurrency(budget.amount)}</td>
                                            <td style={styles.cell}>{formatCurrency(budget.spentAmount)}</td>
                                            <td
                                                style={{
                                                    ...styles.cell,
                                                    color: Number(budget.remainingAmount) < 0 ? "#b91c1c" : "#166534",
                                                }}
                                            >
                                                {formatCurrency(budget.remainingAmount)}
                                            </td>
                                            <td style={styles.cell}>
                                                {budget.startDate}
                                                <br />to {budget.endDate}
                                            </td>
                                            <td style={styles.cell}>
                                                <span
                                                    style={{
                                                        ...statusStyle,
                                                        display: "inline-block",
                                                        padding: "6px 9px",
                                                        borderRadius: 99,
                                                        fontSize: 12,
                                                        fontWeight: 800,
                                                    }}
                                                >
                                                    {budget.status || "UNKNOWN"}
                                                </span>
                                            </td>
                                            <td style={styles.cell}>
                                                <div style={{ display: "flex", gap: 8 }}>
                                                    <button
                                                        type="button"
                                                        style={styles.button}
                                                        onClick={() => openEditForm(budget)}
                                                    >
                                                        Edit
                                                    </button>
                                                    <button
                                                        type="button"
                                                        style={styles.button}
                                                        onClick={() => void deleteBudget(budget)}
                                                    >
                                                        Delete
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                )}

                <div
                    style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        gap: 12,
                        flexWrap: "wrap",
                        marginTop: 20,
                    }}
                >
                    <span>
                        Page {pageInfo.totalPages === 0 ? 0 : page + 1} of {pageInfo.totalPages}
                    </span>

                    <div style={{ display: "flex", gap: 8 }}>
                        <button
                            type="button"
                            style={styles.button}
                            disabled={loading || page <= 0}
                            onClick={() => changePage(page - 1)}
                        >
                            Previous
                        </button>
                        <button
                            type="button"
                            style={styles.button}
                            disabled={
                                loading ||
                                pageInfo.totalPages === 0 ||
                                page + 1 >= pageInfo.totalPages
                            }
                            onClick={() => changePage(page + 1)}
                        >
                            Next
                        </button>
                    </div>
                </div>
            </section>
        </main>
    );
}
