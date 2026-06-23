<template>
  <div style="display:flex;flex-direction:column;align-items:center;justify-content:center;min-height:100dvh;padding:40px 24px">
    <div style="font-size:48px;margin-bottom:8px">🌱</div>
    <h2 style="color:var(--primary-dark);margin-bottom:4px">财务管家</h2>
    <p style="color:var(--text2);font-size:14px;margin-bottom:32px">春日记账，轻松管理每一笔</p>
    <div class="card" style="width:100%">
      <div v-if="error" style="background:var(--danger);color:#fff;padding:10px 14px;border-radius:12px;font-size:13px;margin-bottom:16px">{{ error }}</div>
      <div style="margin-bottom:12px">
        <label style="font-size:14px;font-weight:600;display:block;margin-bottom:6px">用户名</label>
        <input class="input" v-model="username" placeholder="请输入用户名" @keydown.enter="login" />
      </div>
      <div style="margin-bottom:16px">
        <label style="font-size:14px;font-weight:600;display:block;margin-bottom:6px">密码</label>
        <input class="input" v-model="password" type="password" placeholder="请输入密码" @keydown.enter="login" />
      </div>
      <button class="btn" style="width:100%;margin:0" @click="login" :disabled="loading">{{ loading ? '登录中...' : '登 录' }}</button>
      <p style="text-align:center;margin-top:16px;font-size:14px;color:var(--text2)">
        还没有账号？<a @click="$router.push('/register')" style="color:var(--primary-dark);cursor:pointer;font-weight:600">立即注册</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'

const router = useRouter()
const store = useAppStore()
const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function login() {
  if (!username.value || !password.value) { error.value = '请输入用户名和密码'; return }
  loading.value = true; error.value = ''
  try {
    await store.login(username.value, password.value)
    router.push('/')
  } catch (e) {
    error.value = e.message
  } finally { loading.value = false }
}
</script>
