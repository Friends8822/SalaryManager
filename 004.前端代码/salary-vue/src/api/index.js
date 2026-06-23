import axios from 'axios'

const api = axios.create({ baseURL: 'http://localhost:8080' })

// Request interceptor: attach JWT token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('fm_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Response interceptor: handle 401
api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('fm_token')
      window.location.hash = '#/login'
    }
    return Promise.reject(err)
  }
)

// ========== Auth ==========
export const authAPI = {
  login: (data) => api.post('/api/auth/login', data),
  register: (data) => api.post('/api/auth/register', data),
  me: () => api.get('/api/auth/me'),
  changePassword: (data) => api.post('/api/auth/change-password', data),
}

// ========== Expenses ==========
export const expenseAPI = {
  list: (month) => api.get('/api/expenses', { params: { month } }),
  create: (data) => api.post('/api/expenses', data),
  update: (id, data) => api.put(`/api/expenses/${id}`, data),
  delete: (id) => api.delete(`/api/expenses/${id}`),
}

// ========== Categories ==========
export const categoryAPI = {
  list: () => api.get('/api/categories'),
  create: (data) => api.post('/api/categories', data),
  delete: (id) => api.delete(`/api/categories/${id}`),
}

// ========== Budget ==========
export const budgetAPI = {
  get: (month) => api.get('/api/budgets', { params: { month } }),
  set: (data) => api.post('/api/budgets', data),
}

// ========== Stats ==========
export const statsAPI = {
  monthly: (month) => api.get('/api/stats/monthly', { params: { month } }),
}

export default api
