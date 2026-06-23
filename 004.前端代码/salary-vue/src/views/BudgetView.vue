<template>
  <div class="page">
    <div class="header" style="padding:16px 20px;display:flex;align-items:center;gap:12px;border-radius:0">
      <button @click="$router.push('/settings')" style="background:rgba(255,255,255,0.2);border:none;color:#fff;width:36px;height:36px;border-radius:50%;cursor:pointer;font-size:20px">←</button>
      <h1 style="font-size:18px">预算设置</h1>
    </div>
    <div class="card">
      <div style="font-size:14px;font-weight:600;margin-bottom:12px">{{ monthLabel }} 月度总预算</div>
      <div style="position:relative;margin-bottom:12px">
        <span style="position:absolute;left:14px;top:50%;transform:translateY(-50%);font-size:24px;font-weight:700;color:var(--text2)">¥</span>
        <input class="input" v-model="amount" type="number" placeholder="输入预算金额" style="padding-left:42px;font-size:22px;font-weight:700" />
      </div>
      <p style="font-size:13px;color:var(--text2);margin-bottom:16px">设置后首页展示预算进度条，消费达80%预警</p>
      <button class="btn" style="width:100%;margin:0" @click="save" :disabled="saving">{{ saving ? '保存中...' : '保存预算' }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'
const router = useRouter(); const store = useAppStore()
const amount = ref(''), saving = ref(false)
const monthLabel = computed(() => { const d=new Date(); return d.getFullYear()+'年'+(d.getMonth()+1)+'月' })

onMounted(async () => {
  if (!store.user) { const ok = await store.checkAuth(); if (!ok) { router.push('/login'); return } }
  await store.loadAll()
  if (store.budget) amount.value = store.budget.amount
})

async function save() {
  const v = parseFloat(amount.value)
  if (!v || v <= 0) { store.showToast('请输入预算金额', 'error') return }
  saving.value = true
  try { await store.saveBudget(v); router.push('/settings') } catch(e) { store.showToast(e.message, 'error') }
  finally { saving.value = false }
}
</script>
