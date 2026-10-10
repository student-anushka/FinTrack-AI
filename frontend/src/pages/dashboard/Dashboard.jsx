
import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import dashboardApi from "../../api/dashboardApi";

const initialDashboard = {
  summary: null,
  categorySpending: [],
  monthlyTrends: [],
  budgetUtilization: [],
  goalProgress: [],
  financialHealth: null,
  recentActivity: [],
};

function formatCurrency(value) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(Number(value) || 0);
}

function formatDate(value) {
  if (!value) return "—";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);

  return date.toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

function getErrorMessage(error) {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.error ||
    "Unable to load dashboard data. Please try again."
  );
}

function ProgressRow({ label, value, detail }) {
  const numericValue = Math.max(0, Math.min(100, Number(value) || 0));

  return (
    <div className="dashboard-progress-item">
      <div className="dashboard-progress-label">
        <span>{label}</span>
        <span>{detail ?? `${numericValue.toFixed(0)}%`}</span>
      </div>

      <div
        className="dashboard-progress-track"
        role="progressbar"
        aria-label={label}
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={numericValue}
      >
        <div
          className="dashboard-progress-fill"
          style={{ width: `${numericValue}%` }}
        />
      </div>
    </div>
  );
}

export default function Dashboard() {
  const navigate = useNavigate();

  const [summary, setSummary] = useState(initialDashboard.summary);
  const [categorySpending, setCategorySpending] = useState(
    initialDashboard.categorySpending,
  );
  const [monthlyTrends, setMonthlyTrends] = useState(
    initialDashboard.monthlyTrends,
  );
  const [budgetUtilization, setBudgetUtilization] = useState(
    initialDashboard.budgetUtilization,
  );
  const [goalProgress, setGoalProgress] = useState(
    initialDashboard.goalProgress,
  );
  const [financialHealth, setFinancialHealth] = useState(
    initialDashboard.financialHealth,
  );
  const [recentActivity, setRecentActivity] = useState(
    initialDashboard.recentActivity,
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadDashboard = useCallback(async (signal) => {
    const [
      summaryResult,
      categoryResult,
      trendsResult,
      budgetResult,
      goalsResult,
      healthResult,
      activityResult,
    ] = await Promise.all([
      dashboardApi.getSummary({ signal }),
      dashboardApi.getCategorySpending({ signal }),
      dashboardApi.getMonthlyTrends({ signal }),
      dashboardApi.getBudgetUtilization({ signal }),
      dashboardApi.getGoalProgress({ signal }),
      dashboardApi.getFinancialHealth({ signal }),
      dashboardApi.getRecentActivity({ signal }),
    ]);

    setSummary(summaryResult);
    setCategorySpending(
      Array.isArray(categoryResult) ? categoryResult : [],
    );
    setMonthlyTrends(
      Array.isArray(trendsResult) ? trendsResult : [],
    );
    setBudgetUtilization(
      Array.isArray(budgetResult) ? budgetResult : [],
    );
    setGoalProgress(
      Array.isArray(goalsResult) ? goalsResult : [],
    );
    setFinancialHealth(healthResult);
    setRecentActivity(
      Array.isArray(activityResult) ? activityResult : [],
    );
    setError("");
  }, []);

  useEffect(() => {
    const controller = new AbortController();
    let active = true;

    async function fetchDashboard() {
      try {
        await loadDashboard(controller.signal);
      } catch (err) {
        if (!active || err?.name === "CanceledError" ||
          err?.name === "AbortError" ||
          err?.code === "ERR_CANCELED") {
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

    void fetchDashboard();

    return () => {
      active = false;
      controller.abort();
    };
  }, [loadDashboard, navigate]);

  const healthBreakdown = [
    {
      label: "Savings",
      value: financialHealth?.savingsScore,
    },
    {
      label: "Expense control",
      value: financialHealth?.expenseControlScore,
    },
    {
      label: "Budget management",
      value: financialHealth?.budgetScore,
    },
    {
      label: "Financial goals",
      value: financialHealth?.goalScore,
    },
  ];

  if (loading) {
    return (
      <main className="dashboard-page">
        <p role="status">Loading your financial dashboard…</p>
      </main>
    );
  }

  return (
    <main className="dashboard-page">
      <header className="dashboard-header">
        <div>
          <p className="dashboard-eyebrow">FINTRACK OVERVIEW</p>
          <h1>Financial Dashboard</h1>
          <p>Track your income, expenses, budgets, and financial goals.</p>
        </div>

        <button
          className="dashboard-primary-button"
          type="button"
          onClick={() => navigate("/expenses")}
        >
          Manage expenses
        </button>
      </header>

      {error && (
        <div className="dashboard-error" role="alert">
          <p>{error}</p>
          <button
            type="button"
            onClick={() => window.location.reload()}
          >
            Retry
          </button>
        </div>
      )}

      <section className="dashboard-stats-grid" aria-label="Financial summary">
        <article className="dashboard-card">
          <p className="dashboard-label">Total income</p>
          <h2>{formatCurrency(summary?.totalIncome)}</h2>
        </article>

        <article className="dashboard-card">
          <p className="dashboard-label">Total expenses</p>
          <h2>{formatCurrency(summary?.totalExpense)}</h2>
        </article>

        <article className="dashboard-card">
          <p className="dashboard-label">Total savings</p>
          <h2>{formatCurrency(summary?.totalSavings)}</h2>
        </article>

        <article className="dashboard-card">
          <p className="dashboard-label">Savings rate</p>
          <h2>{Number(summary?.savingsRate ?? 0).toFixed(1)}%</h2>
        </article>
      </section>

      <section className="dashboard-content-grid">
        <article className="dashboard-card">
          <h2 className="dashboard-section-title">This month</h2>

          <div className="dashboard-detail-row">
            <span>Income</span>
            <strong>{formatCurrency(summary?.currentMonthIncome)}</strong>
          </div>

          <div className="dashboard-detail-row">
            <span>Expenses</span>
            <strong>{formatCurrency(summary?.currentMonthExpense)}</strong>
          </div>

          <div className="dashboard-detail-row">
            <span>Net savings</span>
            <strong className="dashboard-net-savings">
              {formatCurrency(
                Number(summary?.currentMonthIncome ?? 0) -
                Number(summary?.currentMonthExpense ?? 0),
              )}
            </strong>
          </div>
        </article>

        <article className="dashboard-card">
          <h2 className="dashboard-section-title">Financial health</h2>

          <div className="dashboard-health-score">
            {financialHealth?.totalScore ?? 0}
            <span>/100</span>
          </div>

          <p className="dashboard-health-rating">
            {financialHealth?.rating || "Not available"}
          </p>

          {healthBreakdown.map((item) => (
            <ProgressRow
              key={item.label}
              label={item.label}
              value={item.value}
            />
          ))}
        </article>
      </section>

      <section className="dashboard-content-grid">
        <article className="dashboard-card">
          <h2 className="dashboard-section-title">Category spending</h2>

          {categorySpending.length === 0 ? (
            <p className="dashboard-empty">No category spending available yet.</p>
          ) : (
            categorySpending.map((item, index) => (
              <div
                className="dashboard-category-item"
                key={item.categoryId ?? item.categoryName ?? index}
              >
                <div>
                  <strong>{item.categoryName || "Uncategorized"}</strong>
                  <p>{formatCurrency(item.totalAmount)}</p>
                </div>
                <span className="dashboard-category-percentage">
                  {Number(item.percentage ?? 0).toFixed(1)}%
                </span>
              </div>
            ))
          )}
        </article>

        <article className="dashboard-card">
          <h2 className="dashboard-section-title">Budget utilization</h2>

          {budgetUtilization.length === 0 ? (
            <p className="dashboard-empty">No budget data available yet.</p>
          ) : (
            budgetUtilization.map((item, index) => (
              <ProgressRow
                key={item.budgetId ?? item.categoryId ?? index}
                label={item.categoryName || item.name || "Budget"}
                value={
                  item.utilizationPercentage ??
                  item.percentage ??
                  item.utilization ??
                  0
                }
                detail={`${Number(
                  item.utilizationPercentage ??
                  item.percentage ??
                  item.utilization ??
                  0,
                ).toFixed(1)}% used`}
              />
            ))
          )}
        </article>
      </section>

      <section className="dashboard-content-grid">
        <article className="dashboard-card">
          <h2 className="dashboard-section-title">Goal progress</h2>

          {goalProgress.length === 0 ? (
            <p className="dashboard-empty">No financial goals available yet.</p>
          ) : (
            goalProgress.map((item, index) => (
              <ProgressRow
                key={item.goalId ?? item.id ?? index}
                label={item.goalName || item.name || "Financial goal"}
                value={
                  item.progressPercentage ??
                  item.percentage ??
                  item.progress ??
                  0
                }
              />
            ))
          )}
        </article>

        <article className="dashboard-card">
          <h2 className="dashboard-section-title">Monthly trends</h2>

          {monthlyTrends.length === 0 ? (
            <p className="dashboard-empty">No monthly trends available yet.</p>
          ) : (
            <div className="dashboard-table-wrapper">
              <table className="dashboard-table">
                <thead>
                  <tr>
                    <th>Month</th>
                    <th>Income</th>
                    <th>Expenses</th>
                  </tr>
                </thead>
                <tbody>
                  {monthlyTrends.map((item, index) => (
                    <tr key={`${item.year ?? ""}-${item.month ?? index}`}>
                      <td>
                        {item.monthName ||
                          `${item.month ?? ""}/${item.year ?? ""}`}
                      </td>
                      <td>{formatCurrency(item.income)}</td>
                      <td>{formatCurrency(item.expense)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </article>
      </section>

      <section className="dashboard-card">
        <h2 className="dashboard-section-title">Recent activity</h2>

        {recentActivity.length === 0 ? (
          <p className="dashboard-empty">No recent activity available yet.</p>
        ) : (
          <div className="dashboard-table-wrapper">
            <table className="dashboard-table">
              <thead>
                <tr>
                  <th>Activity</th>
                  <th>Type</th>
                  <th>Amount</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {recentActivity.map((item, index) => (
                  <tr key={item.id ?? item.transactionId ?? index}>
                    <td>
                      {item.title ||
                        item.description ||
                        item.name ||
                        "Transaction"}
                    </td>
                    <td>{item.type || item.transactionType || "—"}</td>
                    <td>
                      {item.amount == null
                        ? "—"
                        : formatCurrency(item.amount)}
                    </td>
                    <td>
                      {formatDate(
                        item.date ||
                        item.createdAt ||
                        item.expenseDate ||
                        item.incomeDate,
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </main>
  );
}
