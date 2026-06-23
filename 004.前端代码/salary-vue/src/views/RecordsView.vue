<template>
  <div class="page">
    <div class="header" style="padding:16px 20px;display:flex;align-items:center;gap:12px;border-radius:0">
      <h1 style="font-size:18px">📋 全部记录</h1>
    </div>
    <div v-if="store.loading" class="empty"><div class="icon">⏳</div><p>加载中...</p></div>
    <div v-else-if="!store.expenses.length" class="empty"><div class="icon">📭</div><p>暂无记录</p></div>
    <div v-else style="padding:12px 16px">
      <div v-for="e in store.expenses" :key="e.id" class="expense-row" @click="edit(e)">
        <div class="e-icon" :style="{background:(e.category?.color||'#888')+'22'}">{{ e.category?.icon||'📦' }}</div>
        <div class="e-info"><div class="e-name">{{ e.category?.name||'分类' }}{{ e.note?' · '+e.note:'' }}</div><div class="e-date">{{ e.expenseDate }}</div></div>
        <div class="e-amount">-¥{{ store.fmt(e.amount) }}</div>
        <button @click.stop="del(e.id)" style="background:var(--danger);color:#fff;border:none;padding:6px 12px;border-radius:8px;cursor:pointer">删除</button>
      </div>
    </div>

    <!-- FAB -->
    <button class="fab" @click="$router.push('/')">+</button>

    <!-- Bottom Nav -->
    <nav class="bottom-nav">
      <button class="nav-item" @click="$router.push('/')"><span style="font-size:20px">📊</span>首页</button>
      <button class="nav-item active"><span style="font-size:20px">📋</span>记录</button>
      <button class="nav-item" @click="$router.push('/statistics')"><span style="font-size:20px">📈</span>统计</button>
      <button class="nav-item" @click="$router.push('/settings')"><span style="font-size:20px">⚙️</span>设置</button>
    </nav>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAppStore } from '../stores/app'
const store = useAppStore()
onMounted(async () => { if (!store.user) { const ok = await store.checkAuth(); if (!ok) return } await store.loadAll() })
function edit(e) {
  localStorage.setItem('edit_expense', JSON.stringify(e))
  window.location.hash = '#/'
}
async function del(id) { if (!confirm('确定删除？')) return; try { await store.deleteExpense(id) } catch(e) { alert(e.message) } }
</script>
