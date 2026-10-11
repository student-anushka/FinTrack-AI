
import { NavLink, Outlet } from "react-router-dom";
import NotificationBell from "../notifications/NotificationBell";
import "./AppLayout.css";

const AppLayout = () => {
    const handleLogout = () => {
        localStorage.removeItem("jwt_token");
        window.location.href = "/login";
    };

    const navigationItems = [
        { to: "/dashboard", icon: "◫", label: "Dashboard" },
        { to: "/expenses", icon: "↗", label: "Expenses" },
        { to: "/income", icon: "＋", label: "Income" },
        { to: "/budgets", icon: "▤", label: "Budgets" },
        { to: "/goals", icon: "🎯", label: "Goals" },
    ];

    return (
        <div className="app-layout">
            <aside className="app-sidebar">
                <div className="app-brand">
                    <div className="app-brand-icon">F</div>
                    <div>
                        <h1>FinTrack</h1>
                        <p>Personal Finance</p>
                    </div>
                </div>

                <p className="sidebar-section-label">WORKSPACE</p>

                <nav
                    className="sidebar-navigation"
                    aria-label="Main navigation"
                >
                    {navigationItems.map((item) => (
                        <NavLink
                            key={item.to}
                            to={item.to}
                            className={({ isActive }) =>
                                `sidebar-link ${isActive ? "active" : ""}`
                            }
                        >
                            <span aria-hidden="true">{item.icon}</span>
                            {item.label}
                        </NavLink>
                    ))}
                </nav>

                <div className="sidebar-bottom">
                    <div className="sidebar-profile">
                        <div className="profile-avatar">FT</div>
                        <div>
                            <strong>My FinTrack</strong>
                            <p>Personal account</p>
                        </div>
                    </div>

                    <button
                        type="button"
                        className="sidebar-logout"
                        onClick={handleLogout}
                    >
                        <span aria-hidden="true">↪</span>
                        Logout
                    </button>
                </div>
            </aside>

            <div className="app-main">
                <header className="app-topbar">
                    <div className="app-topbar-title">
                        <span>FinTrack</span>
                        <p>Your personal finance workspace</p>
                    </div>

                    <div className="app-topbar-actions">
                        <NotificationBell />
                    </div>
                </header>

                <main className="app-page-content">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default AppLayout;
