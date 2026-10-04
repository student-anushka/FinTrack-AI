import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getApiErrorMessage } from '../../api/getApiErrorMessage'
import { useAuth } from '../../context/useAuth'

const Register = () => {
  const navigate = useNavigate()
  const { register, loading } = useAuth()

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
  })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const handleChange = (event) => {
    setFormData({
      ...formData,
      [event.target.name]: event.target.value,
    })
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    setSuccess('')

    try {
      await register(formData)
      setSuccess('Registration successful. Please login.')

      window.setTimeout(() => {
        navigate('/login')
      }, 900)
    } catch (error) {
      setError(getApiErrorMessage(error, 'Registration failed.'))
    }
  }

  return (
    <main className="auth-shell">
      <section className="auth-panel" aria-labelledby="register-title">
        <div className="brand-mark">FT</div>
        <p className="eyebrow">Start with FinTrack</p>
        <h1 id="register-title">Create account</h1>
        <p className="auth-copy">
          Add your profile details and connect to the FinTrack backend.
        </p>

        {error && <p className="alert error">{error}</p>}
        {success && <p className="alert success">{success}</p>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            Name
            <input
              autoComplete="name"
              name="name"
              onChange={handleChange}
              placeholder="Your name"
              required
              type="text"
              value={formData.name}
            />
          </label>

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
              autoComplete="new-password"
              minLength="6"
              name="password"
              onChange={handleChange}
              placeholder="Create a password"
              required
              type="password"
              value={formData.password}
            />
          </label>

          <button className="primary-button" disabled={loading} type="submit">
            {loading ? 'Creating account...' : 'Register'}
          </button>
        </form>

        <p className="switch-auth">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </section>
    </main>
  )
}

export default Register
