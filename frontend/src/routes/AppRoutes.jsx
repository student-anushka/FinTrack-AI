import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import Login from '../pages/auth/Login'
import Register from '../pages/auth/Register'
import Dashboard from '../pages/dashboard/Dashboard'
import ProtectedRoute from './ProtectedRoute'

const AppRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Navigate replace to="/dashboard" />} path="/" />
        <Route element={<Login />} path="/login" />
        <Route element={<Register />} path="/register" />

        <Route element={<ProtectedRoute />}>
          <Route element={<Dashboard />} path="/dashboard" />
        </Route>

        <Route element={<Navigate replace to="/dashboard" />} path="*" />
      </Routes>
    </BrowserRouter>
  )
}

export default AppRoutes
