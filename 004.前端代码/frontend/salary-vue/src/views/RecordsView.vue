<template>
  <div class="page">
    <div class="header" style="padding:16px 20px;display:flex;align-items:center;gap:12px;border-radius:0">
      <button @click="$router.push('/')" style="background:rgba(255,255,255,0.2);border:none;color:#fff;width:36px;height:36px;border-radius:50%;cursor:pointer;font-size:20px">←</button>
      <h1 style="font-size:18px">全部记录</h1>
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
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAppStore } from '../stores/app'
const store = useAppStore()
onMounted(async () => { if (!store.user) { const ok = await store.checkAuth(); if (!ok) return } await store.loadAll() })
function edit(e) {
  // Navigate back to dashboard and open edit modal
  localStorage.setItem('edit_expense', JSON.stringify(e))
  window.location.hash = '#/'
}
async function del(id) { if (!confirm('确定删除？')) return; try { await store.deleteExpense(id) } catch(e) { alert(e.message) } }
</script>
