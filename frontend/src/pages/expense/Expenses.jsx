
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import expenseApi from "../../api/expenseApi";
import categoryApi from "../../api/categoryApi";

const PAGE_SIZE = 10;

const EMPTY_FORM = {
    title: "",
    amount: "",
    description: "",
    expenseDate: new Date().toISOString().slice(0, 10),
    paymentMethod: "UPI",
    categoryId: "",
};

const PAYMENT_METHODS = [
    "UPI",
    "CASH",
    "CREDIT_CARD",
    "DEBIT_CARD",
    "BANK_TRANSFER",
    "OTHER",
];

const styles = {
    page: {
        maxWidth: 1200,
        margin: "0 auto",
        padding: "24px",
        color: "var(--text-primary, #172033)",
    },
    header: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        gap: 16,
        flexWrap: "wrap",
        marginBottom: 24,
    },
    card: {
        background: "var(--card-bg, #ffffff)",
        border: "1px solid var(--border-color, #e5e7eb)",
        borderRadius: 12,
        padding: 20,
        marginBottom: 20,
    },
    grid: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(190px, 1fr))",
        gap: 16,
    },
    field: {
        display: "flex",
        flexDirection: "column",
        gap: 6,
        marginBottom: 14,
    },
    input: {
        width: "100%",
        boxSizing: "border-box",
        padding: "10px 12px",
        border: "1px solid #cbd5e1",
        borderRadius: 7,
        font: "inherit",
        background: "#ffffff",
        color: "#172033",
    },
    button: {
        padding: "10px 15px",
        borderRadius: 7,
        border: "1px solid #cbd5e1",
        cursor: "pointer",
        font: "inherit",
        background: "#ffffff",
        color: "#172033",
    },
    primaryButton: {
        padding: "10px 15px",
        borderRadius: 7,
        border: "1px solid #1d4ed8",
        cursor: "pointer",
        font: "inherit",
        background: "#1d4ed8",
        color: "#ffffff",
    },
    tableWrapper: {
        width: "100%",
        overflowX: "auto",
    },
    table: {
        width: "100%",
        borderCollapse: "collapse",
        textAlign: "left",
    },
    cell: {
        padding: "12px 10px",
        borderBottom: "1px solid #e5e7eb",
        whiteSpace: "nowrap",
    },
    message: {
        padding: 12,
        borderRadius: 7,
        marginBottom: 16,
        background: "#fef2f2",
        color: "#991b1b",
    },
};

function formatCurrency(value) {
    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2,
    }).format(Number(value) || 0);
}

function getErrorMessage(error) {
    return (
        error?.response?.data?.message ||
        error?.response?.data?.error ||
        "Something went wrong. Please try again."
    );
}

function normalizeCategories(data) {
    if (Array.isArray(data)) return data;
    if (Array.isArray(data?.categories)) return data.categories;
    return [];
}

function normalizeExpensePage(data) {
    const response = data?.data ?? data;

    return {
        expenses: Array.isArray(response?.expenses) ? response.expenses : [],
        currentPage: Number(response?.currentPage ?? 0),
        totalPages: Number(response?.totalPages ?? 0),
        totalElements: Number(response?.totalElements ?? 0),
        pageSize: Number(response?.pageSize ?? PAGE_SIZE),
    };
}

export default function Expenses() {
    const navigate = useNavigate();

    const [expenses, setExpenses] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [categoriesLoading, setCategoriesLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [notice, setNotice] = useState("");
    const [formError, setFormError] = useState("");
    const [formOpen, setFormOpen] = useState(false);
    const [editingExpense, setEditingExpense] = useState(null);
    const [form, setForm] = useState(EMPTY_FORM);

    const [page, setPage] = useState(0);
    const [pageInfo, setPageInfo] = useState({
        currentPage: 0,
        totalPages: 0,
        totalElements: 0,
        pageSize: PAGE_SIZE,
    });

    const [filters, setFilters] = useState({
        search: "",
        categoryId: "",
        startDate: "",
        endDate: "",
        minAmount: "",
        maxAmount: "",
    });

    const [appliedFilters, setAppliedFilters] = useState({});
    const [refreshKey, setRefreshKey] = useState(0);

    // Load categories once when the page mounts.
    useEffect(() => {
        const controller = new AbortController();
        let active = true;

        async function fetchCategories() {
            try {
                const data = await categoryApi.getCategories({
                    signal: controller.signal,
                });

                if (active) {
                    setCategories(normalizeCategories(data));
                }
            } catch (err) {
                if (
                    !active ||
                    err?.name === "CanceledError" ||
                    err?.name === "AbortError" ||
                    err?.code === "ERR_CANCELED"
                ) {
                    return;
                }

                setError(getErrorMessage(err));
            } finally {
                if (active) {
                    setCategoriesLoading(false);
                }
            }
        }

        void fetchCategories();

        return () => {
            active = false;
            controller.abort();
        };
    }, []);

    // Fetch the selected page and applied filters.
    useEffect(() => {
        const controller = new AbortController();
        let active = true;

        async function fetchExpenses() {
            const params = {
                page,
                size: PAGE_SIZE,
                sortBy: "expenseDate",
                direction: "desc",
                ...appliedFilters,
            };

            try {
                const data = await expenseApi.getExpenses({
                    ...params,
                    signal: controller.signal,
                });

                if (!active) return;

                const result = normalizeExpensePage(data);
                setExpenses(result.expenses);
                setPageInfo({
                    currentPage: result.currentPage,
                    totalPages: result.totalPages,
                    totalElements: result.totalElements,
                    pageSize: result.pageSize,
                });
                setError("");
            } catch (err) {
                if (
                    !active ||
                    err?.name === "CanceledError" ||
                    err?.name === "AbortError" ||
                    err?.code === "ERR_CANCELED"
                ) {
                    return;
                }

                if ([401, 403].includes(err?.response?.status)) {
                    localStorage.removeItem("jwt_token");
                    navigate("/login", { replace: true });
                    return;
                }

                setError(getErrorMessage(err));
            } finally {
                if (active) {
                    setLoading(false);
                }
            }
        }

        void fetchExpenses();

        return () => {
            active = false;
            controller.abort();
        };
    }, [page, appliedFilters, refreshKey, navigate]);

    function handleFilterChange(event) {
        const { name, value } = event.target;

        setFilters((current) => ({
            ...current,
            [name]: value,
        }));
    }

    function handleApplyFilters(event) {
        event.preventDefault();

        if (
            filters.startDate &&
            filters.endDate &&
            filters.startDate > filters.endDate
        ) {
            setError("Start date cannot be after end date.");
            return;
        }

        if (
            filters.minAmount !== "" &&
            filters.maxAmount !== "" &&
            Number(filters.minAmount) > Number(filters.maxAmount)
        ) {
            setError("Minimum amount cannot exceed maximum amount.");
            return;
        }

        const nextFilters = {};

        Object.entries(filters).forEach(([key, value]) => {
            if (value !== "") {
                nextFilters[key] = value;
            }
        });

        setError("");
        setNotice("");
        setLoading(true);
        setPage(0);
        setAppliedFilters(nextFilters);
    }

    function handleResetFilters() {
        setFilters({
            search: "",
            categoryId: "",
            startDate: "",
            endDate: "",
            minAmount: "",
            maxAmount: "",
        });
        setAppliedFilters({});
        setPage(0);
        setLoading(true);
        setError("");
        setNotice("");
    }

    function openCreateForm() {
        setEditingExpense(null);
        setForm({
            ...EMPTY_FORM,
            expenseDate: new Date().toISOString().slice(0, 10),
        });
        setFormError("");
        setNotice("");
        setFormOpen(true);
    }

    function openEditForm(expense) {
        setEditingExpense(expense);
        setForm({
            title: expense.title ?? "",
            amount: String(expense.amount ?? ""),
            description: expense.description ?? "",
            expenseDate: expense.expenseDate ?? "",
            paymentMethod: expense.paymentMethod ?? "UPI",
            categoryId: String(expense.categoryId ?? ""),
        });
        setFormError("");
        setNotice("");
        setFormOpen(true);
    }

    function handleFormChange(event) {
        const { name, value } = event.target;

        setForm((current) => ({
            ...current,
            [name]: value,
        }));
    }

    async function handleSubmitExpense(event) {
        event.preventDefault();
        setFormError("");
        setError("");
        setNotice("");

        if (!form.title.trim()) {
            setFormError("Please enter an expense title.");
            return;
        }

        if (form.amount === "" || !Number.isFinite(Number(form.amount)) ||
            Number(form.amount) <= 0) {
            setFormError("Amount must be greater than zero.");
            return;
        }

        if (!form.expenseDate) {
            setFormError("Please select an expense date.");
            return;
        }

        if (!form.paymentMethod) {
            setFormError("Please select a payment method.");
            return;
        }

        if (!form.categoryId) {
            setFormError("Please select a category.");
            return;
        }

        const payload = {
            title: form.title.trim(),
            amount: Number(form.amount),
            description: form.description.trim(),
            expenseDate: form.expenseDate,
            paymentMethod: form.paymentMethod,
            categoryId: Number(form.categoryId),
        };

        setSaving(true);

        try {
            if (editingExpense) {
                await expenseApi.updateExpense(editingExpense.id, payload);
                setNotice("Expense updated successfully.");
            } else {
                await expenseApi.createExpense(payload);
                setNotice("Expense created successfully.");
            }

            setFormOpen(false);
            setEditingExpense(null);
            setForm(EMPTY_FORM);
            setPage(0);
            setLoading(true);
            setRefreshKey((current) => current + 1);
        } catch (err) {
            setFormError(getErrorMessage(err));
        } finally {
            setSaving(false);
        }
    }

    async function handleDeleteExpense(expense) {
        const confirmed = window.confirm(
            `Delete "${expense.title}"? This action cannot be undone.`,
        );

        if (!confirmed) return;

        setError("");
        setNotice("");

        try {
            await expenseApi.deleteExpense(expense.id);

            setNotice("Expense deleted successfully.");
            setLoading(true);

            // If the last item on a page was deleted, go back one page.
            if (expenses.length === 1 && page > 0) {
                setPage((current) => current - 1);
            } else {
                setRefreshKey((current) => current + 1);
            }
        } catch (err) {
            setError(getErrorMessage(err));
        }
    }

    function handlePageChange(nextPage) {
        if (
            nextPage < 0 ||
            (pageInfo.totalPages > 0 && nextPage >= pageInfo.totalPages)
        ) {
            return;
        }

        setLoading(true);
        setPage(nextPage);
    }

    return (
        <main style={styles.page}>
            <header style={styles.header}>
                <div>
                    <h1>Expenses</h1>
                    <p>Record, search, filter, and manage your expenses.</p>
                </div>

                <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                    <button
                        type="button"
                        style={styles.button}
                        onClick={() => navigate("/dashboard")}
                    >
                        Back to dashboard
                    </button>

                    <button
                        type="button"
                        style={styles.primaryButton}
                        onClick={openCreateForm}
                    >
                        + Add expense
                    </button>
                </div>
            </header>

            {error && (
                <div style={styles.message} role="alert">
                    {error}
                </div>
            )}

            {notice && (
                <div
                    role="status"
                    style={{
                        ...styles.message,
                        background: "#ecfdf5",
                        color: "#065f46",
                    }}
                >
                    {notice}
                </div>
            )}

            {formOpen && (
                <section style={styles.card}>
                    <h2>{editingExpense ? "Edit expense" : "Add expense"}</h2>

                    {formError && (
                        <div style={styles.message} role="alert">
                            {formError}
                        </div>
                    )}

                    <form onSubmit={handleSubmitExpense}>
                        <div style={styles.grid}>
                            <label style={styles.field}>
                                Title *
                                <input
                                    style={styles.input}
                                    name="title"
                                    value={form.title}
                                    onChange={handleFormChange}
                                    maxLength={150}
                                    required
                                />
                            </label>

                            <label style={styles.field}>
                                Amount (₹) *
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
                                Expense date *
                                <input
                                    style={styles.input}
                                    name="expenseDate"
                                    type="date"
                                    value={form.expenseDate}
                                    onChange={handleFormChange}
                                    required
                                />
                            </label>

                            <label style={styles.field}>
                                Payment method *
                                <select
                                    style={styles.input}
                                    name="paymentMethod"
                                    value={form.paymentMethod}
                                    onChange={handleFormChange}
                                    required
                                >
                                    {PAYMENT_METHODS.map((method) => (
                                        <option key={method} value={method}>
                                            {method.replaceAll("_", " ")}
                                        </option>
                                    ))}
                                </select>
                            </label>

                            <label style={styles.field}>
                                Category *
                                <select
                                    style={styles.input}
                                    name="categoryId"
                                    value={form.categoryId}
                                    onChange={handleFormChange}
                                    disabled={categoriesLoading || categories.length === 0}
                                    required
                                >
                                    <option value="">
                                        {categoriesLoading
                                            ? "Loading categories…"
                                            : categories.length === 0
                                                ? "No categories available"
                                                : "Select category"}
                                    </option>

                                    {categories.map((category) => (
                                        <option key={category.id} value={category.id}>
                                            {category.name}
                                        </option>
                                    ))}
                                </select>
                            </label>
                        </div>

                        <label style={styles.field}>
                            Description
                            <textarea
                                style={{ ...styles.input, minHeight: 90, resize: "vertical" }}
                                name="description"
                                value={form.description}
                                onChange={handleFormChange}
                                maxLength={1000}
                            />
                        </label>

                        <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                            <button
                                type="submit"
                                style={styles.primaryButton}
                                disabled={saving}
                            >
                                {saving
                                    ? "Saving…"
                                    : editingExpense
                                        ? "Update expense"
                                        : "Save expense"}
                            </button>

                            <button
                                type="button"
                                style={styles.button}
                                disabled={saving}
                                onClick={() => {
                                    setFormOpen(false);
                                    setEditingExpense(null);
                                    setFormError("");
                                }}
                            >
                                Cancel
                            </button>
                        </div>
                    </form>
                </section>
            )}

            <section style={styles.card}>
                <h2>Search and filters</h2>

                <form onSubmit={handleApplyFilters}>
                    <div style={styles.grid}>
                        <label style={styles.field}>
                            Search
                            <input
                                style={styles.input}
                                name="search"
                                value={filters.search}
                                onChange={handleFilterChange}
                                placeholder="Search expense title"
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
                            Start date
                            <input
                                style={styles.input}
                                type="date"
                                name="startDate"
                                value={filters.startDate}
                                onChange={handleFilterChange}
                            />
                        </label>

                        <label style={styles.field}>
                            End date
                            <input
                                style={styles.input}
                                type="date"
                                name="endDate"
                                value={filters.endDate}
                                onChange={handleFilterChange}
                            />
                        </label>

                        <label style={styles.field}>
                            Minimum amount (₹)
                            <input
                                style={styles.input}
                                type="number"
                                min="0"
                                step="0.01"
                                name="minAmount"
                                value={filters.minAmount}
                                onChange={handleFilterChange}
                            />
                        </label>

                        <label style={styles.field}>
                            Maximum amount (₹)
                            <input
                                style={styles.input}
                                type="number"
                                min="0"
                                step="0.01"
                                name="maxAmount"
                                value={filters.maxAmount}
                                onChange={handleFilterChange}
                            />
                        </label>
                    </div>

                    <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                        <button type="submit" style={styles.primaryButton}>
                            Apply filters
                        </button>
                        <button
                            type="button"
                            style={styles.button}
                            onClick={handleResetFilters}
                        >
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
                        alignItems: "center",
                        flexWrap: "wrap",
                        gap: 12,
                    }}
                >
                    <h2>Expense history</h2>
                    <span>
                        Total records: {pageInfo.totalElements}
                    </span>
                </div>

                {loading ? (
                    <p role="status">Loading expenses…</p>
                ) : expenses.length === 0 ? (
                    <p>No expenses found. Add an expense or adjust your filters.</p>
                ) : (
                    <div style={styles.tableWrapper}>
                        <table style={styles.table}>
                            <thead>
                                <tr>
                                    {[
                                        "Title",
                                        "Category",
                                        "Amount",
                                        "Date",
                                        "Payment method",
                                        "Description",
                                        "Actions",
                                    ].map((heading) => (
                                        <th key={heading} style={styles.cell}>
                                            {heading}
                                        </th>
                                    ))}
                                </tr>
                            </thead>

                            <tbody>
                                {expenses.map((expense) => (
                                    <tr key={expense.id}>
                                        <td style={styles.cell}>{expense.title}</td>
                                        <td style={styles.cell}>
                                            {expense.categoryName || "—"}
                                        </td>
                                        <td style={styles.cell}>
                                            {formatCurrency(expense.amount)}
                                        </td>
                                        <td style={styles.cell}>
                                            {expense.expenseDate || "—"}
                                        </td>
                                        <td style={styles.cell}>
                                            {expense.paymentMethod?.replaceAll("_", " ") || "—"}
                                        </td>
                                        <td style={styles.cell}>
                                            {expense.description || "—"}
                                        </td>
                                        <td style={styles.cell}>
                                            <div style={{ display: "flex", gap: 8 }}>
                                                <button
                                                    type="button"
                                                    style={styles.button}
                                                    onClick={() => openEditForm(expense)}
                                                >
                                                    Edit
                                                </button>
                                                <button
                                                    type="button"
                                                    style={styles.button}
                                                    onClick={() => void handleDeleteExpense(expense)}
                                                >
                                                    Delete
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
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
                        Page {pageInfo.totalPages === 0 ? 0 : page + 1} of{" "}
                        {pageInfo.totalPages}
                    </span>

                    <div style={{ display: "flex", gap: 8 }}>
                        <button
                            type="button"
                            style={styles.button}
                            disabled={loading || page <= 0}
                            onClick={() => handlePageChange(page - 1)}
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
                            onClick={() => handlePageChange(page + 1)}
                        >
                            Next
                        </button>
                    </div>
                </div>
            </section>
        </main>
    );
}
