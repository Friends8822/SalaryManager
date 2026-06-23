<template>
  <div class="page">
    <div class="header" style="padding:16px 20px;display:flex;align-items:center;gap:12px;border-radius:0">
      <button @click="$router.push('/settings')" style="background:rgba(255,255,255,0.2);border:none;color:#fff;width:36px;height:36px;border-radius:50%;cursor:pointer;font-size:20px">←</button>
      <h1 style="font-size:18px">类别管理</h1>
    </div>

    <div v-if="store.loading" class="empty"><div class="icon">⏳</div><p>加载中...</p></div>
    <template v-else>
      <div v-for="c in store.categories" :key="c.id" style="display:flex;align-items:center;padding:12px 16px;gap:12px;border-bottom:1px solid var(--card)">
        <span :style="{background:c.color+'22',width:'36px',height:'36px',borderRadius:'10px',display:'flex',alignItems:'center',justifyContent:'center',fontSize:'20px'}">{{ c.icon }}</span>
        <div style="flex:1"><div style="font-size:15px;font-weight:500">{{ c.name }}</div><div style="font-size:12px;color:var(--text2)">{{ c.isPreset ? '预设分类' : '自定义' }}</div></div>
        <button v-if="!c.isPreset" @click="del(c.id)" style="color:var(--danger);font-size:13px;border:none;background:none;cursor:pointer">删除</button>
        <span v-else style="color:var(--text2)">🔒</span>
      </div>

      <!-- Add form -->
      <div v-if="showForm" class="card">
        <div style="margin-bottom:12px">
          <label style="font-size:13px;font-weight:600;display:block;margin-bottom:6px">名称（≤6字）</label>
          <input class="input" v-model="newName" maxlength="6" placeholder="分类名称" />
        </div>
        <div style="margin-bottom:12px">
          <label style="font-size:13px;font-weight:600;display:block;margin-bottom:6px">图标</label>
          <div style="display:grid;grid-template-columns:repeat(8,1fr);gap:4px">
            <button v-for="e in emojis" :key="e" @click="newIcon=e" :style="{background:newIcon===e?'var(--primary)':'var(--card)',color:newIcon===e?'#fff':'var(--text)',border:'none',borderRadius:'8px',padding:'6px',fontSize:'18px',cursor:'pointer'}">{{ e }}</button>
          </div>
        </div>
        <div style="display:flex;gap:12px">
          <button @click="showForm=false" style="flex:1;padding:10px;background:var(--card);border:none;border-radius:14px;cursor:pointer">取消</button>
          <button @click="addCat" style="flex:1;padding:10px;background:var(--primary);color:#fff;border:none;border-radius:14px;cursor:pointer">确定</button>
        </div>
      </div>
      <button v-else class="btn" @click="showForm=true">+ 新增分类</button>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'

const router = useRouter(); const store = useAppStore()
const showForm = ref(false), newName = ref(''), newIcon = ref('📦')
const emojis = ['🍚','🚗','🛒','🏠','🎬','💊','📚','📦','☕','🍕','🎮','✈️','💇','🎁','📱','🐱','💻','🎵','📷','🏋️','💄','🌿','💰','🔧']

onMounted(async () => {
  if (!store.user) { const ok = await store.checkAuth(); if (!ok) { router.push('/login'); return } }
  await store.loadAll()
})

async function addCat() {
  if (!newName.value.trim()) { store.showToast('请输入分类名称', 'error') return }
  try {
    await store.addCategory({ name: newName.value.trim().slice(0,6), icon: newIcon.value, color: '#7EC850' })
    showForm.value = false; newName.value = ''; newIcon.value = '📦'
  } catch(e) { store.showToast(e.message, 'error') }
}

async function del(id) {
  if (!confirm('删除此分类？')) return
  try { await store.deleteCategory(id) } catch(e) { store.showToast(e.message, 'error') }
}
</script>
