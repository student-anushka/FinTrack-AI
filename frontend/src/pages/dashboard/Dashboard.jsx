import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import dashboardApi from "../../api/dashboardApi";

function formatCurrency(value) {
  const amount = Number(value || 0);

  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(amount);
}

function formatPercentage(value) {
  return `${Number(value || 0).toFixed(1)}%`;
}

function Dashboard() {
  const navigate = useNavigate();

  const [summary, setSummary] = useState(null);
  const [categorySpending, setCategorySpending] = useState([]);
  const [monthlyTrends, setMonthlyTrends] = useState([]);
  const [financialHealth, setFinancialHealth] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      setError("");

      const [
        summaryData,
        categoryData,
        monthlyData,
        healthData,
      ] = await Promise.all([
        dashboardApi.getSummary(),
        dashboardApi.getCategorySpending(),
        dashboardApi.getMonthlyTrends(),
        dashboardApi.getFinancialHealth(),
      ]);

      setSummary(summaryData);
      setCategorySpending(categoryData || []);
      setMonthlyTrends(monthlyData || []);
      setFinancialHealth(healthData);
    } catch (err) {
      console.error("Dashboard loading failed:", err);

      if (err.response?.status === 401 || err.response?.status === 403) {
        localStorage.removeItem("jwt_token");
        navigate("/login");
        return;
      }

      setError(
        err.response?.data?.message ||
        "Unable to load your dashboard. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("jwt_token");
    navigate("/login");
  };

  if (loading) {
    return (
      <div className="dashboard-page">
        <div className="dashboard-loading">
          <h2>Loading your FinTrack dashboard...</h2>
          <p>Fetching your financial data.</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-page">
        <div className="dashboard-error">
          <h2>Something went wrong</h2>
          <p>{error}</p>

          <button onClick={loadDashboard}>
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-page">
      <header className="dashboard-header">
        <div>
          <p className="eyebrow">FINTRACK DASHBOARD</p>

          <h1>Good to see you</h1>

          <p className="dashboard-subtitle">
            Understand your income, spending, savings and financial
            health in one place.
          </p>
        </div>

        <button
          className="logout-button"
          onClick={handleLogout}
        >
          Logout
        </button>
      </header>

      {/* SUMMARY */}

      <section className="summary-grid">
        <div className="summary-card">
          <p>Total Income</p>
          <h2>
            {formatCurrency(summary?.totalIncome)}
          </h2>
        </div>

        <div className="summary-card">
          <p>Total Expense</p>
          <h2>
            {formatCurrency(summary?.totalExpense)}
          </h2>
        </div>

        <div className="summary-card">
          <p>Total Savings</p>
          <h2>
            {formatCurrency(summary?.totalSavings)}
          </h2>
        </div>

        <div className="summary-card">
          <p>Savings Rate</p>
          <h2>
            {formatPercentage(summary?.savingsRate)}
          </h2>
        </div>
      </section>

      {/* CURRENT MONTH */}

      <section className="section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">CURRENT MONTH</p>
            <h2>Monthly overview</h2>
          </div>
        </div>

        <div className="monthly-grid">
          <div className="financial-card">
            <span>Income</span>

            <strong>
              {formatCurrency(summary?.currentMonthIncome)}
            </strong>
          </div>

          <div className="financial-card">
            <span>Expenses</span>

            <strong>
              {formatCurrency(summary?.currentMonthExpense)}
            </strong>
          </div>

          <div className="financial-card">
            <span>Net Savings</span>

            <strong>
              {formatCurrency(
                Number(summary?.currentMonthIncome || 0) -
                Number(summary?.currentMonthExpense || 0)
              )}
            </strong>
          </div>
        </div>
      </section>

      {/* CATEGORY SPENDING */}

      <section className="section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">SPENDING ANALYSIS</p>
            <h2>Where your money goes</h2>
          </div>
        </div>

        {categorySpending.length === 0 ? (
          <div className="empty-card">
            <h3>No expense data yet</h3>
            <p>
              Add some expenses to see your spending breakdown.
            </p>
          </div>
        ) : (
          <div className="category-card">
            {categorySpending.map((category) => (
              <div
                className="category-row"
                key={category.categoryId}
              >
                <div className="category-info">
                  <div>
                    <strong>
                      {category.categoryName}
                    </strong>

                    <span>
                      {formatCurrency(category.totalAmount)}
                    </span>
                  </div>

                  <span>
                    {formatPercentage(category.percentage)}
                  </span>
                </div>

                <div className="progress-track">
                  <div
                    className="progress-fill"
                    style={{
                      width: `${Math.min(
                        Number(category.percentage || 0),
                        100
                      )}%`,
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* MONTHLY TRENDS */}

      <section className="section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">FINANCIAL TREND</p>
            <h2>Monthly income vs expenses</h2>
          </div>
        </div>

        {monthlyTrends.length === 0 ? (
          <div className="empty-card">
            <h3>No monthly data yet</h3>
            <p>
              Your monthly trend will appear once you have
              income or expense records.
            </p>
          </div>
        ) : (
          <div className="trend-table-wrapper">
            <table className="trend-table">
              <thead>
                <tr>
                  <th>Month</th>
                  <th>Income</th>
                  <th>Expense</th>
                  <th>Net</th>
                </tr>
              </thead>

              <tbody>
                {monthlyTrends.map((month) => {
                  const net =
                    Number(month.income || 0) -
                    Number(month.expense || 0);

                  return (
                    <tr
                      key={`${month.year}-${month.month}`}
                    >
                      <td>
                        {month.monthName} {month.year}
                      </td>

                      <td>
                        {formatCurrency(month.income)}
                      </td>

                      <td>
                        {formatCurrency(month.expense)}
                      </td>

                      <td>
                        {formatCurrency(net)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {/* FINANCIAL HEALTH */}

      <section className="section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">FINANCIAL HEALTH</p>
            <h2>Your financial health score</h2>
          </div>
        </div>

        {financialHealth && (
          <div className="health-card">
            <div className="health-main">
              <span>Overall Score</span>

              <strong>
                {Number(
                  financialHealth.totalScore || 0
                ).toFixed(1)}
                /100
              </strong>

              <span className="health-rating">
                {financialHealth.rating}
              </span>
            </div>

            <div className="health-breakdown">
              <div>
                <span>Savings</span>
                <strong>
                  {financialHealth.savingsScore}
                </strong>
              </div>

              <div>
                <span>Expense Control</span>
                <strong>
                  {financialHealth.expenseControlScore}
                </strong>
              </div>

              <div>
                <span>Budget</span>
                <strong>
                  {financialHealth.budgetScore}
                </strong>
              </div>

              <div>
                <span>Goals</span>
                <strong>
                  {financialHealth.goalScore}
                </strong>
              </div>
            </div>
          </div>
        )}
      </section>
    </div>
  );
}

export default Dashboard;