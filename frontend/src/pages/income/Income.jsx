
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import incomeApi from "../../api/incomeApi";

const PAGE_SIZE = 10;

function getTodayLocalDate() {
    const date = new Date();
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
    return date.toISOString().slice(0, 10);
}

const EMPTY_FORM = {
    source: "",
    amount: "",
    description: "",
    incomeDate: getTodayLocalDate(),
    incomeType: "Salary",
};

const INCOME_TYPES = [
    "Salary",
    "Freelance",
    "Business",
    "Investment",
    "Rental",
    "Interest",
    "Gift",
    "Other",
];

const styles = {
    page: {
        maxWidth: 1200,
        margin: "0 auto",
        padding: 24,
        color: "#172033",
    },
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
        border: "1px solid #e5e7eb",
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
        background: "#fff",
        color: "#172033",
    },
    button: {
        padding: "10px 15px",
        border: "1px solid #cbd5e1",
        borderRadius: 7,
        cursor: "pointer",
        font: "inherit",
        background: "#fff",
        color: "#172033",
    },
    primary: {
        padding: "10px 15px",
        border: "1px solid #1d4ed8",
        borderRadius: 7,
        cursor: "pointer",
        font: "inherit",
        background: "#1d4ed8",
        color: "#fff",
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
    error: {
        padding: 12,
        borderRadius: 7,
        marginBottom: 16,
        background: "#fef2f2",
        color: "#991b1b",
    },
    success: {
        padding: 12,
        borderRadius: 7,
        marginBottom: 16,
        background: "#ecfdf5",
        color: "#065f46",
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

function normalizePage(data) {
    const response = data?.data ?? data;

    return {
        incomes: Array.isArray(response?.incomes) ? response.incomes : [],
        currentPage: Number(response?.currentPage ?? 0),
        totalPages: Number(response?.totalPages ?? 0),
        totalElements: Number(response?.totalElements ?? 0),
        pageSize: Number(response?.pageSize ?? PAGE_SIZE),
    };
}

export default function Income() {
    const navigate = useNavigate();

    const [incomes, setIncomes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [notice, setNotice] = useState("");
    const [formError, setFormError] = useState("");

    const [formOpen, setFormOpen] = useState(false);
    const [editingIncome, setEditingIncome] = useState(null);
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
        incomeType: "",
        startDate: "",
        endDate: "",
        minAmount: "",
        maxAmount: "",
    });

    const [appliedFilters, setAppliedFilters] = useState({});
    const [refreshKey, setRefreshKey] = useState(0);

    // Load income records for the current page and filters.
    useEffect(() => {
        const controller = new AbortController();
        let active = true;

        async function fetchIncomes() {
            try {
                const data = await incomeApi.getIncomes(
                    {
                        page,
                        size: PAGE_SIZE,
                        sortBy: "incomeDate",
                        direction: "desc",
                        ...appliedFilters,
                    },
                    { signal: controller.signal },
                );

                if (!active) return;

                const result = normalizePage(data);

                setIncomes(result.incomes);
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
                if (active) setLoading(false);
            }
        }

        void fetchIncomes();

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
            if (value !== "") nextFilters[key] = value;
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
            incomeType: "",
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
        setEditingIncome(null);
        setForm({
            ...EMPTY_FORM,
            incomeDate: getTodayLocalDate(),
        });
        setFormError("");
        setFormOpen(true);
        setNotice("");
    }

    function openEditForm(income) {
        setEditingIncome(income);
        setForm({
            source: income.source ?? "",
            amount: String(income.amount ?? ""),
            description: income.description ?? "",
            incomeDate: income.incomeDate ?? "",
            incomeType: income.incomeType ?? "",
        });
        setFormError("");
        setFormOpen(true);
        setNotice("");
    }

    function handleFormChange(event) {
        const { name, value } = event.target;

        setForm((current) => ({
            ...current,
            [name]: value,
        }));
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setFormError("");
        setError("");
        setNotice("");

        if (!form.source.trim()) {
            setFormError("Please enter the income source.");
            return;
        }

        if (
            form.amount === "" ||
            !Number.isFinite(Number(form.amount)) ||
            Number(form.amount) <= 0
        ) {
            setFormError("Amount must be greater than zero.");
            return;
        }

        if (!form.incomeDate) {
            setFormError("Please select the income date.");
            return;
        }

        if (form.incomeDate > getTodayLocalDate()) {
            setFormError("Income date cannot be in the future.");
            return;
        }

        if (!form.incomeType.trim()) {
            setFormError("Please enter an income type.");
            return;
        }

        const payload = {
            source: form.source.trim(),
            amount: Number(form.amount),
            description: form.description.trim(),
            incomeDate: form.incomeDate,
            incomeType: form.incomeType.trim(),
        };

        setSaving(true);

        try {
            if (editingIncome) {
                await incomeApi.updateIncome(editingIncome.id, payload);
                setNotice("Income updated successfully.");
            } else {
                await incomeApi.createIncome(payload);
                setNotice("Income added successfully.");
            }

            setFormOpen(false);
            setEditingIncome(null);
            setForm(EMPTY_FORM);
            setPage(0);
            setLoading(true);
            setRefreshKey((current) => current + 1);
        } catch (err) {
            if ([401, 403].includes(err?.response?.status)) {
                localStorage.removeItem("jwt_token");
                navigate("/login", { replace: true });
                return;
            }

            setFormError(getErrorMessage(err));
        } finally {
            setSaving(false);
        }
    }

    async function handleDelete(income) {
        const confirmed = window.confirm(
            `Delete income from "${income.source}"? This cannot be undone.`,
        );

        if (!confirmed) return;

        setError("");
        setNotice("");

        try {
            await incomeApi.deleteIncome(income.id);

            setNotice("Income deleted successfully.");
            setLoading(true);

            if (incomes.length === 1 && page > 0) {
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

            setError(getErrorMessage(err));
        }
    }

    function changePage(nextPage) {
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
                    <h1>Income Management</h1>
                    <p>Track and manage the money you earn.</p>
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
                        style={styles.primary}
                        onClick={openCreateForm}
                    >
                        + Add income
                    </button>
                </div>
            </header>

            {error && (
                <div style={styles.error} role="alert">
                    {error}
                </div>
            )}

            {notice && (
                <div style={styles.success} role="status">
                    {notice}
                </div>
            )}

            {formOpen && (
                <section style={styles.card}>
                    <h2>{editingIncome ? "Edit income" : "Add income"}</h2>

                    {formError && (
                        <div style={styles.error} role="alert">
                            {formError}
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <div style={styles.grid}>
                            <label style={styles.field}>
                                Income source *
                                <input
                                    style={styles.input}
                                    name="source"
                                    value={form.source}
                                    onChange={handleFormChange}
                                    maxLength={150}
                                    placeholder="e.g. Monthly salary"
                                    required
                                />
                            </label>

                            <label style={styles.field}>
                                Amount (₹) *
                                <input
                                    style={styles.input}
                                    type="number"
                                    name="amount"
                                    min="0.01"
                                    step="0.01"
                                    value={form.amount}
                                    onChange={handleFormChange}
                                    required
                                />
                            </label>

                            <label style={styles.field}>
                                Income date *
                                <input
                                    style={styles.input}
                                    type="date"
                                    name="incomeDate"
                                    max={getTodayLocalDate()}
                                    value={form.incomeDate}
                                    onChange={handleFormChange}
                                    required
                                />
                            </label>

                            <label style={styles.field}>
                                Income type *
                                <input
                                    style={styles.input}
                                    name="incomeType"
                                    list="fintrack-income-types"
                                    value={form.incomeType}
                                    onChange={handleFormChange}
                                    maxLength={50}
                                    placeholder="e.g. Salary"
                                    required
                                />
                                <datalist id="fintrack-income-types">
                                    {INCOME_TYPES.map((type) => (
                                        <option key={type} value={type} />
                                    ))}
                                </datalist>
                            </label>
                        </div>

                        <label style={styles.field}>
                            Description (optional)
                            <textarea
                                style={{ ...styles.input, minHeight: 90, resize: "vertical" }}
                                name="description"
                                value={form.description}
                                onChange={handleFormChange}
                                placeholder="Add details about this income"
                            />
                        </label>

                        <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                            <button
                                type="submit"
                                style={styles.primary}
                                disabled={saving}
                            >
                                {saving
                                    ? "Saving…"
                                    : editingIncome
                                        ? "Update income"
                                        : "Save income"}
                            </button>

                            <button
                                type="button"
                                style={styles.button}
                                disabled={saving}
                                onClick={() => {
                                    setFormOpen(false);
                                    setEditingIncome(null);
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
                            Search source or description
                            <input
                                style={styles.input}
                                name="search"
                                value={filters.search}
                                onChange={handleFilterChange}
                                placeholder="Search income"
                            />
                        </label>

                        <label style={styles.field}>
                            Income type
                            <input
                                style={styles.input}
                                name="incomeType"
                                list="fintrack-filter-income-types"
                                value={filters.incomeType}
                                onChange={handleFilterChange}
                                placeholder="All types"
                            />
                            <datalist id="fintrack-filter-income-types">
                                {INCOME_TYPES.map((type) => (
                                    <option key={type} value={type} />
                                ))}
                            </datalist>
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
                        <button type="submit" style={styles.primary}>
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
                    <h2>Income history</h2>
                    <strong>
                        Total records: {pageInfo.totalElements}
                    </strong>
                </div>

                {loading ? (
                    <p role="status">Loading income records…</p>
                ) : incomes.length === 0 ? (
                    <p>No income records found. Add income or adjust your filters.</p>
                ) : (
                    <div style={styles.tableWrapper}>
                        <table style={styles.table}>
                            <thead>
                                <tr>
                                    {[
                                        "Source",
                                        "Amount",
                                        "Date",
                                        "Type",
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
                                {incomes.map((income) => (
                                    <tr key={income.id}>
                                        <td style={styles.cell}>{income.source}</td>
                                        <td style={styles.cell}>
                                            {formatCurrency(income.amount)}
                                        </td>
                                        <td style={styles.cell}>
                                            {income.incomeDate || "—"}
                                        </td>
                                        <td style={styles.cell}>
                                            {income.incomeType || "—"}
                                        </td>
                                        <td style={styles.cell}>
                                            {income.description || "—"}
                                        </td>
                                        <td style={styles.cell}>
                                            <div style={{ display: "flex", gap: 8 }}>
                                                <button
                                                    type="button"
                                                    style={styles.button}
                                                    onClick={() => openEditForm(income)}
                                                >
                                                    Edit
                                                </button>
                                                <button
                                                    type="button"
                                                    style={styles.button}
                                                    onClick={() => void handleDelete(income)}
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
                        flexWrap: "wrap",
                        gap: 12,
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
