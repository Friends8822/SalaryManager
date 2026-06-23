<template>
  <div class="page">
    <div class="header"><h1>⚙️ 设置</h1></div>
    <div class="card" style="padding:0;overflow:hidden">
      <div class="set-row" @click="$router.push('/budget')"><span>💰 预算设置</span><span style="color:var(--text2)">›</span></div>
      <div class="set-row" @click="$router.push('/categories')"><span>🏷️ 类别管理</span><span style="color:var(--text2)">›</span></div>
      <div class="set-row" @click="exportCSV"><span>📤 数据导出</span><span style="color:var(--text2)">›</span></div>
    </div>
    <div class="card" style="padding:0;overflow:hidden">
      <div class="set-row" @click="$router.push('/security')"><span>🔐 安全设置</span><span style="color:var(--text2)">›</span></div>
      <div class="set-row" @click="logout"><span>🚪 退出登录</span><span style="color:var(--text2)">›</span></div>
    </div>
    <p style="text-align:center;color:var(--text2);font-size:13px;padding:20px">财务管家 V2.0 · 春日记账</p>
    <nav class="bottom-nav">
      <button class="nav-item" @click="$router.push('/')"><span style="font-size:20px">📊</span>首页</button>
      <button class="nav-item" @click="$router.push('/records')"><span style="font-size:20px">📋</span>记录</button>
      <button class="nav-item" @click="$router.push('/statistics')"><span style="font-size:20px">📈</span>统计</button>
      <button class="nav-item active"><span style="font-size:20px">⚙️</span>设置</button>
    </nav>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'
const router = useRouter(); const store = useAppStore()
function logout() { store.logout(); router.push('/login') }
function exportCSV() {
  const exps = store.expenses; if (!exps.length) { alert('无记录'); return }
  const m = new Date(); const month = m.getFullYear()+'-'+String(m.getMonth()+1).padStart(2,'0')
  const csv = '﻿日期,分类,金额,备注\n' + exps.map(e=>`${e.expenseDate},${e.category?.name||''},-${e.amount},${e.note||''}`).join('\n')
  const b = new Blob([csv],{type:'text/csv;charset=utf-8'}); const a = document.createElement('a')
  a.href = URL.createObjectURL(b); a.download = `财务管家_${month}.csv`; a.click()
}
</script>

<style scoped>
.set-row { display: flex; align-items: center; justify-content: space-between; padding: 14px 16px; cursor: pointer; border-bottom: 1px solid var(--card); font-size: 15px; }
.set-row:last-child { border-bottom: none; }
.set-row:hover { background: var(--card); }
</style>
