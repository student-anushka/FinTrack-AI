import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/useAuth'

const summaryCards = [
  {
    label: 'Monthly balance',
    value: 'Ready',
    detail: 'Backend auth connected',
  },
  {
    label: 'Budget modules',
    value: 'Next',
    detail: 'Add income, expense, and category APIs',
  },
  {
    label: 'Route guard',
    value: 'Active',
    detail: 'Dashboard requires JWT login',
  },
]

const Dashboard = () => {
  const navigate = useNavigate()
  const { user, logout } = useAuth()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <main className="dashboard-shell">
      <header className="dashboard-header">
        <div>
          <p className="eyebrow">FinTrack dashboard</p>
          <h1>Good to see you{user?.name ? `, ${user.name}` : ''}</h1>
          <p className="dashboard-copy">
            Your authentication foundation is ready. The next step is connecting
            budget and transaction data from the Spring Boot APIs.
          </p>
        </div>

        <button className="secondary-button" onClick={handleLogout} type="button">
          Logout
        </button>
      </header>

      <section className="summary-grid" aria-label="Dashboard summary">
        {summaryCards.map((card) => (
          <article className="summary-card" key={card.label}>
            <p>{card.label}</p>
            <strong>{card.value}</strong>
            <span>{card.detail}</span>
          </article>
        ))}
      </section>

      <section className="dashboard-panel" aria-labelledby="profile-title">
        <div>
          <p className="eyebrow">Signed in as</p>
          <h2 id="profile-title">{user?.email || 'Authenticated user'}</h2>
        </div>
        <p>
          JWT is stored in local storage and will be sent automatically in future
          API requests through the shared Axios instance.
        </p>
      </section>
    </main>
  )
}

export default Dashboard
