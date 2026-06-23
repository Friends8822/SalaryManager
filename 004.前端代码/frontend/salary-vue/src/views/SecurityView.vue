<template>
  <div class="page">
    <div class="header" style="padding:16px 20px;display:flex;align-items:center;gap:12px;border-radius:0">
      <button @click="$router.push('/settings')" style="background:rgba(255,255,255,0.2);border:none;color:#fff;width:36px;height:36px;border-radius:50%;cursor:pointer;font-size:20px">←</button>
      <h1 style="font-size:18px">安全设置</h1>
    </div>
    <div class="card">
      <h3 style="font-size:16px;font-weight:700;margin-bottom:16px">🔒 修改密码</h3>
      <div v-if="error" style="background:var(--danger);color:#fff;padding:10px 14px;border-radius:12px;font-size:13px;margin-bottom:16px">{{ error }}</div>
      <div style="margin-bottom:12px">
        <label style="font-size:13px;font-weight:600;display:block;margin-bottom:6px">当前密码</label>
        <input class="input" v-model="oldPwd" type="password" placeholder="输入当前密码" />
      </div>
      <div style="margin-bottom:12px">
        <label style="font-size:13px;font-weight:600;display:block;margin-bottom:6px">新密码（≥6位）</label>
        <input class="input" v-model="newPwd" type="password" placeholder="输入新密码" />
      </div>
      <div style="margin-bottom:16px">
        <label style="font-size:13px;font-weight:600;display:block;margin-bottom:6px">确认新密码</label>
        <input class="input" v-model="confirmPwd" type="password" placeholder="再次输入新密码" />
      </div>
      <button class="btn" style="width:100%;margin:0" @click="changePwd" :disabled="loading">{{ loading ? '修改中...' : '更新密码' }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'
const router = useRouter(); const store = useAppStore()
const oldPwd = ref(''), newPwd = ref(''), confirmPwd = ref('')
const error = ref(''), loading = ref(false)

async function changePwd() {
  if (!oldPwd.value) { error.value = '请输入当前密码'; return }
  if (!newPwd.value || newPwd.value.length < 6) { error.value = '新密码至少6位'; return }
  if (newPwd.value !== confirmPwd.value) { error.value = '两次密码不一致'; return }
  loading.value = true; error.value = ''
  try {
    await store.changePassword(oldPwd.value, newPwd.value)
    alert('密码修改成功'); router.push('/settings')
  } catch(e) { error.value = e.message }
  finally { loading.value = false }
}
</script>
