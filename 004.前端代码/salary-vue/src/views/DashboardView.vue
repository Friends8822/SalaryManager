<template>
  <div class="page">
    <div class="header">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
        <span style="font-size:13px;color:rgba(255,255,255,0.8)">👤 {{ store.user?.username }}</span>
        <span style="font-size:12px;color:rgba(255,255,255,0.7);cursor:pointer" @click="logout">退出</span>
      </div>
      <h1>🌱 财务管家</h1>
      <div style="display:flex;align-items:center;gap:12px;margin-top:12px">
        <button @click="prevMonth" style="background:rgba(255,255,255,0.2);border:none;color:#fff;width:32px;height:32px;border-radius:50%;cursor:pointer;font-size:18px">‹</button>
        <span style="color:#fff;font-weight:600;min-width:100px;text-align:center">{{ monthLabel }}</span>
        <button @click="nextMonth" style="background:rgba(255,255,255,0.2);border:none;color:#fff;width:32px;height:32px;border-radius:50%;cursor:pointer;font-size:18px">›</button>
      </div>
    </div>

    <div v-if="store.loading" class="empty"><div class="icon">⏳</div><p>加载中...</p></div>

    <template v-else>
      <!-- Overview Cards -->
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;margin:16px">
        <div class="card" style="margin:0">
          <div style="font-size:13px;color:var(--text2)">本月支出</div>
          <div style="font-size:28px;font-weight:800;color:var(--expense)">¥{{ store.fmt(store.stats?.totalExpense) }}</div>
        </div>
        <div class="card" style="margin:0">
          <div style="font-size:13px;color:var(--text2)">月度预算</div>
          <div style="font-size:28px;font-weight:800;color:var(--primary-dark)">{{ store.budget ? '¥'+store.fmt(store.budget.amount) : '未设置' }}</div>
        </div>
      </div>

      <!-- Category Breakdown -->
      <div class="card">
        <div class="card-title">📊 分类支出</div>
        <div v-if="!store.stats?.categories?.length" class="empty"><div class="icon">📝</div><p>暂无记录</p></div>
        <div v-else v-for="c in store.stats.categories" :key="c.categoryId" style="display:flex;align-items:center;gap:10px;padding:10px 12px;background:var(--card);border-radius:12px;margin-bottom:6px">
          <span :style="{background:c.color+'22',width:'36px',height:'36px',borderRadius:'12px',display:'flex',alignItems:'center',justifyContent:'center',fontSize:'18px'}">{{ c.icon }}</span>
          <span style="flex:1;font-size:14px;font-weight:500">{{ c.categoryName }}</span>
          <span style="font-size:14px;font-weight:700;color:var(--expense)">¥{{ store.fmt(c.amount) }} <small style="color:var(--text2)">{{ c.percent }}%</small></span>
        </div>
      </div>

      <!-- Budget Bar -->
      <div v-if="store.stats?.budget" class="card">
        <div class="card-title">月度预算</div>
        <div style="display:flex;justify-content:space-between;font-size:13px;margin-bottom:6px">
          <span>预算 ¥{{ store.fmt(store.stats.budget.budgetAmount) }}</span>
          <span>已用 ¥{{ store.fmt(store.stats.budget.spentAmount) }}</span>
        </div>
        <div class="budget-bar">
          <div :class="'budget-fill '+budgetClass" :style="{width:Math.min(store.stats.budget.progressPercent,100)+'%'}"></div>
        </div>
        <div style="font-size:13px;margin-top:6px" :style="{color:budgetColor}">{{ budgetText }}</div>
      </div>

      <!-- Recent Transactions -->
      <div class="card">
        <div class="card-title">📋 最近记录</div>
        <div v-if="!store.expenses.length" class="empty"><div class="icon">💭</div><p>暂无记录</p></div>
        <div v-for="e in store.expenses.slice(0,10)" :key="e.id" class="expense-row" @click="editExpense(e)">
          <div class="e-icon" :style="{background:(e.category?.color||'#888')+'22'}">{{ e.category?.icon || '📦' }}</div>
          <div class="e-info">
            <div class="e-name">{{ e.category?.name || '分类' }}{{ e.note ? ' · '+e.note : '' }}</div>
            <div class="e-date">{{ e.expenseDate }}</div>
          </div>
          <div class="e-amount">-¥{{ store.fmt(e.amount) }}</div>
          <button @click.stop="del(e.id)" style="background:var(--danger);color:#fff;border:none;padding:6px 12px;border-radius:8px;font-size:12px;cursor:pointer">删除</button>
        </div>
      </div>
    </template>

    <!-- FAB -->
    <button class="fab" @click="showAddModal=true">+</button>

    <!-- Bottom Nav -->
    <nav class="bottom-nav">
      <button class="nav-item active" @click="$router.push('/')"><span style="font-size:20px">📊</span>首页</button>
      <button class="nav-item" @click="$router.push('/records')"><span style="font-size:20px">📋</span>记录</button>
      <button class="nav-item" @click="$router.push('/statistics')"><span style="font-size:20px">📈</span>统计</button>
      <button class="nav-item" @click="$router.push('/settings')"><span style="font-size:20px">⚙️</span>设置</button>
    </nav>

    <!-- Add Expense Modal -->
    <div v-if="showAddModal" class="sheet-overlay" @click.self="closeAdd">
      <div class="sheet">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
          <h3 style="font-size:20px;font-weight:700">{{ editingId ? '编辑记录' : '记一笔' }}</h3>
          <button @click="closeAdd" style="width:36px;height:36px;border:none;background:var(--card);border-radius:50%;font-size:18px;cursor:pointer">✕</button>
        </div>
        <div style="margin-bottom:16px">
          <label style="font-size:14px;font-weight:600;display:block;margin-bottom:8px">选择分类</label>
          <div class="cat-grid">
            <button v-for="c in store.categories" :key="c.id" :class="'cat-item'+(selectedCatId===c.id?' selected':'')" @click="selectedCatId=c.id">
              <span class="ico">{{ c.icon }}</span><span class="name">{{ c.name }}</span>
            </button>
          </div>
        </div>
        <div style="margin-bottom:16px">
          <label style="font-size:14px;font-weight:600;display:block;margin-bottom:8px">金额</label>
          <div style="position:relative">
            <span style="position:absolute;left:14px;top:50%;transform:translateY(-50%);font-size:24px;font-weight:700;color:var(--text2)">¥</span>
            <input class="input" v-model="amount" type="number" placeholder="0.00" step="0.01" style="padding-left:42px;font-size:24px;font-weight:700" />
          </div>
        </div>
        <div style="margin-bottom:16px">
          <label style="font-size:14px;font-weight:600;display:block;margin-bottom:8px">日期</label>
          <input class="input" v-model="expenseDate" type="date" />
        </div>
        <div style="margin-bottom:16px">
          <label style="font-size:14px;font-weight:600;display:block;margin-bottom:8px">备注（可选）</label>
          <input class="input" v-model="note" type="text" placeholder="添加备注..." />
        </div>
        <button class="btn" style="width:100%;margin:0" @click="submit" :disabled="submitting">{{ submitting ? '保存中...' : (editingId ? '保存修改' : '✓ 记一笔') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'

const router = useRouter()
const store = useAppStore()
const showAddModal = ref(false)
const selectedCatId = ref(null)
const amount = ref(''), note = ref(''), expenseDate = ref(new Date().toISOString().split('T')[0])
const editingId = ref(null), submitting = ref(false)
const currentMonth = ref(new Date())
const monthLabel = computed(() => currentMonth.value.getFullYear()+'年'+(currentMonth.value.getMonth()+1)+'月')

onMounted(async () => {
  if (!store.user) {
    const ok = await store.checkAuth()
    if (!ok) { router.push('/login'); return }
  }
  await store.loadAll()
})

const budgetClass = computed(() => {
  const b = store.stats?.budget; if (!b) return ''
  return b.isOver ? 'over' : (b.isWarn ? 'warn' : '')
})
const budgetColor = computed(() => {
  const b = store.stats?.budget; if (!b) return ''
  return b.isOver ? 'var(--danger)' : (b.isWarn ? 'var(--warning)' : 'var(--text2)')
})
const budgetText = computed(() => {
  const b = store.stats?.budget; if (!b) return ''
  return b.isOver ? '本月已超支' : (b.isWarn ? `已用 ${b.progressPercent}%，注意控制哦` : `剩余 ¥${store.fmt(b.remaining)}`)
})

function monthStr(d) { return d.getFullYear() + '-' + String(d.getMonth()+1).padStart(2,'0') }
function prevMonth() { currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth()-1, 1); store.loadAll(monthStr(currentMonth.value)) }
function nextMonth() { currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth()+1, 1); store.loadAll(monthStr(currentMonth.value)) }

function closeAdd() { showAddModal.value = false; editingId.value = null; selectedCatId.value = null; amount.value = ''; note.value = '' }
function editExpense(e) { editingId.value = e.id; selectedCatId.value = e.categoryId; amount.value = e.amount; note.value = e.note||''; expenseDate.value = e.expenseDate; showAddModal.value = true }

async function submit() {
  const amt = parseFloat(amount.value)
  if (!amt || amt <= 0) { alert('请输入有效金额'); return }
  submitting.value = true
  try {
    const data = { categoryId: selectedCatId.value || store.categories[0]?.id, amount: Math.round(amt*100)/100, note: note.value, expenseDate: expenseDate.value }
    if (editingId.value) await store.updateExpense(editingId.value, data)
    else await store.addExpense(data)
    closeAdd()
  } catch (e) { alert(e.message) }
  finally { submitting.value = false }
}

async function del(id) {
  if (!confirm('确定删除？')) return
  try { await store.deleteExpense(id) } catch (e) { alert(e.message) }
}

function logout() { store.logout(); router.push('/login') }
</script>
