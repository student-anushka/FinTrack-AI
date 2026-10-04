import { useEffect, useState } from 'react'
import { loginUser, registerUser } from '../api/authApi'
import { AuthContext } from './authContext'

const readStoredUser = () => {
  const storedUser = localStorage.getItem('user')

  if (!storedUser) {
    return null
  }

  try {
    return JSON.parse(storedUser)
  } catch {
    localStorage.removeItem('user')
    return null
  }
}

const getTokenFromResponse = (response) =>
  response?.token || response?.accessToken || response?.jwt || response?.data?.token

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem('jwt_token'))
  const [user, setUser] = useState(readStoredUser)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (token) {
      localStorage.setItem('jwt_token', token)
      return
    }

    localStorage.removeItem('jwt_token')
  }, [token])

  const login = async (loginData) => {
    setLoading(true)

    try {
      const response = await loginUser(loginData)
      const receivedToken = getTokenFromResponse(response)

      if (!receivedToken) {
        throw new Error('Login response does not contain a token')
      }

      const receivedUser = response.user || response.data?.user || {
        email: loginData.email,
      }

      localStorage.setItem('jwt_token', receivedToken)
      localStorage.setItem('user', JSON.stringify(receivedUser))

      setToken(receivedToken)
      setUser(receivedUser)

      return response
    } finally {
      setLoading(false)
    }
  }

  const register = async (registerData) => {
    setLoading(true)

    try {
      return await registerUser(registerData)
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    localStorage.removeItem('jwt_token')
    localStorage.removeItem('user')

    setToken(null)
    setUser(null)
  }

  const value = {
    token,
    user,
    loading,
    isAuthenticated: Boolean(token),
    login,
    register,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
