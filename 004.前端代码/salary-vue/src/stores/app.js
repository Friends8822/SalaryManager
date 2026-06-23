import { defineStore } from 'pinia'
import { authAPI, expenseAPI, categoryAPI, budgetAPI, statsAPI } from '../api'

export const useAppStore = defineStore('app', {
  state: () => ({
    user: null,
    currentMonth: '',
    stats: null,
    expenses: [],
    categories: [],
    budget: null,
    loading: false,
  }),
  actions: {
    async login(username, password) {
      const res = await authAPI.login({ username, password })
      if (res.data.code === 200) {
        localStorage.setItem('fm_token', res.data.data.token)
        this.user = { username: res.data.data.username }
        await this.loadAll()
        return true
      }
      throw new Error(res.data.message || '登录失败')
    },
    async register(username, password) {
      const res = await authAPI.register({ username, password })
      if (res.data.code === 200) return true
      throw new Error(res.data.message || '注册失败')
    },
    async checkAuth() {
      const token = localStorage.getItem('fm_token')
      if (!token) return false
      try {
        const res = await authAPI.me()
        if (res.data.code === 200) {
          this.user = { username: res.data.data.username }
          await this.loadAll()
          return true
        }
      } catch (e) { localStorage.removeItem('fm_token') }
      return false
    },
    async loadAll() {
      const m = this.getCurrentMonth()
      this.loading = true
      try {
        const [catRes, expRes, budRes, statsRes] = await Promise.all([
          categoryAPI.list(),
          expenseAPI.list(m),
          budgetAPI.get(m),
          statsAPI.monthly(m),
        ])
        if (catRes.data.code === 200) this.categories = catRes.data.data
        if (expRes.data.code === 200) this.expenses = expRes.data.data
        if (budRes.data.code === 200 && budRes.data.data) this.budget = budRes.data.data
        if (statsRes.data.code === 200) this.stats = statsRes.data.data
      } finally { this.loading = false }
    },
    async addExpense(data) {
      const res = await expenseAPI.create(data)
      if (res.data.code === 200) { await this.loadAll(); return true }
      throw new Error(res.data.message || '添加失败')
    },
    async updateExpense(id, data) {
      const res = await expenseAPI.update(id, data)
      if (res.data.code === 200) { await this.loadAll(); return true }
      throw new Error(res.data.message || '更新失败')
    },
    async deleteExpense(id) {
      const res = await expenseAPI.delete(id)
      if (res.data.code === 200) { await this.loadAll(); return true }
      throw new Error(res.data.message || '删除失败')
    },
    async addCategory(data) {
      const res = await categoryAPI.create(data)
      if (res.data.code === 200) { await this.loadAll(); return true }
      throw new Error(res.data.message || '添加失败')
    },
    async deleteCategory(id) {
      const res = await categoryAPI.delete(id)
      if (res.data.code === 200) { await this.loadAll(); return true }
      throw new Error(res.data.message || '删除失败')
    },
    async saveBudget(amount) {
      const res = await budgetAPI.set({ budgetMonth: this.getCurrentMonth(), amount })
      if (res.data.code === 200) { await this.loadAll(); return true }
      throw new Error(res.data.message || '保存失败')
    },
    async changePassword(oldPwd, newPwd) {
      const res = await authAPI.changePassword({ oldPassword: oldPwd, newPassword: newPwd })
      if (res.data.code === 200) return true
      throw new Error(res.data.message || '修改失败')
    },
    logout() {
      localStorage.removeItem('fm_token')
      this.user = null
      this.stats = null; this.expenses = []; this.categories = []; this.budget = null
    },
    getCurrentMonth() {
      const d = new Date()
      return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0')
    },
    fmt(n) {
      return Number(n || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },
  },
})
