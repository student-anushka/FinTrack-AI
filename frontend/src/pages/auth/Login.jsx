import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getApiErrorMessage } from '../../api/getApiErrorMessage'
import { useAuth } from '../../context/useAuth'

const Login = () => {
  const navigate = useNavigate()
  const { login, loading } = useAuth()

  const [formData, setFormData] = useState({
    email: '',
    password: '',
  })
  const [error, setError] = useState('')

  const handleChange = (event) => {
    setFormData({
      ...formData,
      [event.target.name]: event.target.value,
    })
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')

    try {
      await login(formData)
      navigate('/dashboard')
    } catch (error) {
      setError(
        getApiErrorMessage(
          error,
          'Login failed. Please check your credentials.',
        ),
      )
    }
  }

  return (
    <main className="auth-shell">
      <section className="auth-panel" aria-labelledby="login-title">
        <div className="brand-mark">FT</div>
        <p className="eyebrow">FinTrack account</p>
        <h1 id="login-title">Welcome back</h1>
        <p className="auth-copy">
          Sign in to continue tracking your budgets, income, and daily spending.
        </p>

        {error && <p className="alert error">{error}</p>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            Email
            <input
              autoComplete="email"
              name="email"
              onChange={handleChange}
              placeholder="you@example.com"
              required
              type="email"
              value={formData.email}
            />
          </label>

          <label>
            Password
            <input
              autoComplete="current-password"
              name="password"
              onChange={handleChange}
              placeholder="Enter your password"
              required
              type="password"
              value={formData.password}
            />
          </label>

          <button className="primary-button" disabled={loading} type="submit">
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <p className="switch-auth">
          New to FinTrack? <Link to="/register">Create account</Link>
        </p>
      </section>
    </main>
  )
}

export default Login
