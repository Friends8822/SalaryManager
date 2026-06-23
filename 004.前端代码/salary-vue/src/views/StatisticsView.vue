<template>
  <div class="page">
    <div class="header">
      <div style="display:flex;align-items:center;gap:12px">
        <button @click="prevMonth" style="background:rgba(255,255,255,0.2);border:none;color:#fff;width:32px;height:32px;border-radius:50%;cursor:pointer;font-size:18px">‹</button>
        <span style="color:#fff;font-weight:600;min-width:100px;text-align:center">{{ monthLabel }}</span>
        <button @click="nextMonth" style="background:rgba(255,255,255,0.2);border:none;color:#fff;width:32px;height:32px;border-radius:50%;cursor:pointer;font-size:18px">›</button>
      </div>
    </div>
    <div v-if="store.loading" class="empty"><div class="icon">⏳</div><p>加载中...</p></div>
    <template v-else>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;margin:16px">
        <div class="card" style="margin:0"><div style="font-size:13px;color:var(--text2)">本月支出</div><div style="font-size:28px;font-weight:800">¥{{ store.fmt(store.stats?.totalExpense) }}</div></div>
        <div class="card" style="margin:0"><div style="font-size:13px;color:var(--text2)">日均支出</div><div style="font-size:28px;font-weight:800">¥{{ store.fmt(dailyAvg) }}</div></div>
      </div>
      <div class="card">
        <div class="card-title">🍩 分类占比</div>
        <canvas ref="pieCanvas" height="220"></canvas>
      </div>
      <div class="card">
        <div class="card-title">📈 消费趋势</div>
        <canvas ref="trendCanvas" height="200"></canvas>
      </div>
      <button class="btn" @click="exportCSV">📥 导出本月CSV</button>
    </template>
    <nav class="bottom-nav">
      <button class="nav-item" @click="$router.push('/')"><span style="font-size:20px">📊</span>首页</button>
      <button class="nav-item" @click="$router.push('/records')"><span style="font-size:20px">📋</span>记录</button>
      <button class="nav-item active"><span style="font-size:20px">📈</span>统计</button>
      <button class="nav-item" @click="$router.push('/settings')"><span style="font-size:20px">⚙️</span>设置</button>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useAppStore } from '../stores/app'
import { Chart, DoughnutController, ArcElement, LineController, LineElement, PointElement, CategoryScale, LinearScale, Filler, Legend, Tooltip } from 'chart.js'

Chart.register(DoughnutController, ArcElement, LineController, LineElement, PointElement, CategoryScale, LinearScale, Filler, Legend, Tooltip)

const store = useAppStore()
const pieCanvas = ref(null), trendCanvas = ref(null)
const currentMonth = ref(new Date())
const monthLabel = computed(() => currentMonth.value.getFullYear()+'年'+(currentMonth.value.getMonth()+1)+'月')
const dailyAvg = computed(() => {
  const days = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth()+1, 0).getDate()
  const elapsed = Math.min(new Date().getDate(), days)
  return elapsed > 0 ? (Number(store.stats?.totalExpense||0)/elapsed) : 0
})

let pieInst = null, trendInst = null

function drawCharts() {
  if (pieInst) pieInst.destroy(); if (trendInst) trendInst.destroy()
  const cats = store.stats?.categories || []
  if (cats.length && pieCanvas.value) {
    pieInst = new Chart(pieCanvas.value, { type: 'doughnut', data: { labels: cats.map(c=>c.categoryName), datasets: [{ data: cats.map(c=>c.amount), backgroundColor: cats.map(c=>c.color) }] }, options: { responsive: true, plugins: { legend: { position: 'right', labels: { font: { size: 10 } } } } } })
  }
  const trend = store.stats?.dailyTrend || []
  if (trend.length && trendCanvas.value) {
    trendInst = new Chart(trendCanvas.value, { type: 'line', data: { labels: trend.map(t=>t.date.slice(5)), datasets: [{ data: trend.map(t=>t.amount), borderColor: '#7EC850', backgroundColor: 'rgba(126,200,80,0.1)', fill: true, tension: 0.4, pointRadius: 3 }] }, options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } } })
  }
}

onMounted(async () => {
  if (!store.user) { const ok = await store.checkAuth(); if (!ok) return }
  await store.loadAll()
  nextTick(drawCharts)
})

function monthStr(d) { return d.getFullYear() + '-' + String(d.getMonth()+1).padStart(2,'0') }
function prevMonth() { currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth()-1, 1); store.loadAll(monthStr(currentMonth.value)).then(()=>nextTick(drawCharts)) }
function nextMonth() { currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth()+1, 1); store.loadAll(monthStr(currentMonth.value)).then(()=>nextTick(drawCharts)) }

function exportCSV() {
  const exps = store.expenses; if (!exps.length) { alert('无记录'); return }
  const csv = '﻿日期,分类,金额,备注\n' + exps.map(e=>`${e.expenseDate},${e.category?.name||''},-${e.amount},${e.note||''}`).join('\n')
  const b = new Blob([csv], { type: 'text/csv;charset=utf-8' }), a = document.createElement('a')
  a.href = URL.createObjectURL(b); a.download = `财务管家_${currentMonth.value.getFullYear()}-${String(currentMonth.value.getMonth()+1).padStart(2,'0')}.csv`; a.click()
}
</script>
