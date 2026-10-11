import { useEffect, useState } from "react";
import dashboardApi from "../../api/dashboardApi";

const FinancialInsights = () => {
    const [insights, setInsights] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadInsights = async () => {
            try {
                const data = await dashboardApi.getFinancialInsights();
                setInsights(Array.isArray(data) ? data : []);
            } catch (err) {
                setError(
                    err.response?.data?.message ||
                    "Unable to load financial insights. Please try again."
                );
            } finally {
                setLoading(false);
            }
        };

        loadInsights();
    }, []);

    if (loading) {
        return <p>Analyzing your financial data...</p>;
    }

    if (error) {
        return <p role="alert">{error}</p>;
    }

    return (
        <section className="financial-insights">
            <h1>Financial Insights</h1>
            <p>
                Personalized observations based on your recorded financial data.
            </p>

            {insights.length === 0 ? (
                <p>
                    Add income and expense records to unlock your financial insights.
                </p>
            ) : (
                <div className="insights-list">
                    {insights.map((insight, index) => (
                        <article
                            className="insight-card"
                            key={`${insight.type}-${index}`}
                        >
                            <span className={`insight-severity ${String(insight.severity).toLowerCase()}`}>
                                {insight.severity}
                            </span>
                            <h2>{insight.title}</h2>
                            <p>{insight.message}</p>
                        </article>
                    ))}
                </div>
            )}
        </section>
    );
};

export default FinancialInsights;